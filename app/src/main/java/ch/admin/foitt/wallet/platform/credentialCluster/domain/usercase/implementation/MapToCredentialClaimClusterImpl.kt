package ch.admin.foitt.wallet.platform.credentialCluster.domain.usercase.implementation

import ch.admin.foitt.wallet.platform.credentialCluster.domain.usercase.MapToCredentialClaimCluster
import ch.admin.foitt.wallet.platform.database.domain.model.ClusterWithDisplaysAndClaims
import ch.admin.foitt.wallet.platform.database.domain.model.CredentialClaimWithDisplays
import ch.admin.foitt.wallet.platform.locale.domain.usecase.GetLocalizedDisplay
import ch.admin.foitt.wallet.platform.ssi.domain.model.CredentialClaimCluster
import ch.admin.foitt.wallet.platform.ssi.domain.model.CredentialClaimImage
import ch.admin.foitt.wallet.platform.ssi.domain.model.CredentialClaimItem
import ch.admin.foitt.wallet.platform.ssi.domain.model.CredentialClaimText
import ch.admin.foitt.wallet.platform.ssi.domain.model.GetCredentialDetailFlowError
import ch.admin.foitt.wallet.platform.ssi.domain.model.MapToCredentialClaimDataError
import ch.admin.foitt.wallet.platform.ssi.domain.model.toGetCredentialDetailFlowError
import ch.admin.foitt.wallet.platform.ssi.domain.usecase.MapToCredentialClaimData
import ch.admin.foitt.wallet.platform.utils.sortByOrder
import ch.admin.foitt.wallet.platform.utils.sortInPlaceByOrder
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.coroutines.coroutineBinding
import com.github.michaelbull.result.get
import com.github.michaelbull.result.mapError
import javax.inject.Inject

/**
 * returns cluster tree with all elements sorted by order fields
 */
class MapToCredentialClaimClusterImpl @Inject constructor(
    private val getLocalizedDisplay: GetLocalizedDisplay,
    private val mapToCredentialClaimData: MapToCredentialClaimData,
) : MapToCredentialClaimCluster {
    override suspend fun invoke(clustersWithDisplaysAndClaims: List<ClusterWithDisplaysAndClaims>): List<CredentialClaimCluster> {
        // Step 1: Create all nodes with only Claim items
        val nodes = clustersWithDisplaysAndClaims.associate { (clusterWithDisplays, claimsWithDisplays) ->
            clusterWithDisplays.cluster.id to CredentialClaimCluster(
                id = clusterWithDisplays.cluster.id,
                localizedLabel = getLocalizedDisplay(clusterWithDisplays.displays)?.name ?: "",
                parentId = clusterWithDisplays.cluster.parentClusterId,
                order = clusterWithDisplays.cluster.order,
                items = getCredentialClaimData(claimsWithDisplays).get()?.toMutableList() ?: mutableListOf()
            )
        }

        // Step 2: Link each Cluster to its parent
        nodes.values.forEach { node ->
            if (node.parentId != null) {
                val parent = nodes[node.parentId] ?: return@forEach
                parent.items.add(
                    CredentialClaimCluster(
                        id = node.id,
                        order = node.order,
                        localizedLabel = node.localizedLabel,
                        parentId = node.parentId,
                        items = node.items
                    )
                )
                parent.items.sortInPlaceByOrder()
            } else {
                node.items.sortInPlaceByOrder()
            }
        }

        // Step 3: Only return root nodes (parentId == null)
        val roots = nodes.values.filter { it.parentId == null }
        roots.setNumberOfNonClusterChildren()

        // Step 4: Filter empty clusters (mainly for presentation when verifier requests only a few fields)
        val cleanedClusters = filterEmptyClusters(roots)

        // Step 5: Return ordered root clusters
        return cleanedClusters.sortByOrder()
    }

    private suspend fun getCredentialClaimData(
        claims: List<CredentialClaimWithDisplays>
    ): Result<List<CredentialClaimItem>, GetCredentialDetailFlowError> = coroutineBinding {
        claims.map { claimWithDisplays ->
            mapToCredentialClaimData(
                claimWithDisplays
            ).mapError(MapToCredentialClaimDataError::toGetCredentialDetailFlowError).bind()
        }
    }

    private fun List<CredentialClaimItem>.setNumberOfNonClusterChildren(): Int {
        var count = 0
        for (item in this) {
            when (item) {
                is CredentialClaimText, is CredentialClaimImage -> count++
                is CredentialClaimCluster -> {
                    val childCount = item.items.setNumberOfNonClusterChildren()
                    item.numberOfNonClusterChildren = childCount
                    count += childCount
                }
            }
        }
        return count
    }

    private fun filterEmptyClusters(items: List<CredentialClaimCluster>): List<CredentialClaimCluster> {
        return items.mapNotNull { item ->
            if (item.numberOfNonClusterChildren == 0) {
                null
            } else {
                item.copy(
                    items = filterEmptyClusterItems(item.items).toMutableList(),
                )
            }
        }
    }

    private fun filterEmptyClusterItems(items: MutableList<CredentialClaimItem>): List<CredentialClaimItem> {
        return items.mapNotNull { item ->
            when (item) {
                is CredentialClaimCluster -> {
                    if (item.numberOfNonClusterChildren == 0) {
                        null
                    } else {
                        item.copy(
                            items = filterEmptyClusterItems(item.items).toMutableList(),
                        )
                    }
                }

                is CredentialClaimText, is CredentialClaimImage -> item
            }
        }
    }
}
