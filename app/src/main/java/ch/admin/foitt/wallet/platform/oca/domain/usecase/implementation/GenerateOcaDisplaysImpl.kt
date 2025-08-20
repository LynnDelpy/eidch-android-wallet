package ch.admin.foitt.wallet.platform.oca.domain.usecase.implementation

import ch.admin.foitt.wallet.platform.credential.domain.model.AnyClaimDisplay
import ch.admin.foitt.wallet.platform.credential.domain.model.AnyCredentialDisplay
import ch.admin.foitt.wallet.platform.credential.domain.model.toAnyCredentialDisplay
import ch.admin.foitt.wallet.platform.database.domain.model.Cluster
import ch.admin.foitt.wallet.platform.database.domain.model.ClusterDisplay
import ch.admin.foitt.wallet.platform.database.domain.model.CredentialClaim
import ch.admin.foitt.wallet.platform.database.domain.model.DisplayLanguage
import ch.admin.foitt.wallet.platform.oca.domain.model.AttributeType
import ch.admin.foitt.wallet.platform.oca.domain.model.GenerateOcaDisplaysError
import ch.admin.foitt.wallet.platform.oca.domain.model.GetRootCaptureBaseError
import ch.admin.foitt.wallet.platform.oca.domain.model.OcaBundle
import ch.admin.foitt.wallet.platform.oca.domain.model.OcaClaimData
import ch.admin.foitt.wallet.platform.oca.domain.model.OcaDisplays
import ch.admin.foitt.wallet.platform.oca.domain.model.getReferenceValue
import ch.admin.foitt.wallet.platform.oca.domain.model.toGenerateOcaDisplaysError
import ch.admin.foitt.wallet.platform.oca.domain.usecase.GenerateOcaClaimDisplays
import ch.admin.foitt.wallet.platform.oca.domain.usecase.GenerateOcaDisplays
import ch.admin.foitt.wallet.platform.oca.domain.usecase.GetRootCaptureBase
import ch.admin.foitt.wallet.platform.ssi.domain.model.ValueType
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.coroutines.coroutineBinding
import com.github.michaelbull.result.mapError
import javax.inject.Inject

class GenerateOcaDisplaysImpl @Inject constructor(
    private val getRootCaptureBase: GetRootCaptureBase,
    private val generateOcaClaimDisplays: GenerateOcaClaimDisplays,
) : GenerateOcaDisplays {

    override suspend fun invoke(
        credentialClaims: Map<String, String?>,
        ocaBundle: OcaBundle,
    ): Result<OcaDisplays, GenerateOcaDisplaysError> = coroutineBinding {
        val rootCaptureBase = getRootCaptureBase(ocaBundle.captureBases)
            .mapError(GetRootCaptureBaseError::toGenerateOcaDisplaysError)
            .bind()

        val credentialDisplays = createLocalizedCredentialDisplays(ocaBundle)
        val clusters = createClusters(credentialClaims, ocaBundle, rootCaptureBase.digest)

        OcaDisplays(
            credentialDisplays = credentialDisplays,
            clusters = clusters,
        )
    }

    private fun createLocalizedCredentialDisplays(ocaBundle: OcaBundle): List<AnyCredentialDisplay> {
        return ocaBundle.ocaCredentialData.map { credentialData ->
            credentialData.toAnyCredentialDisplay()
        }
    }

    private fun createClusters(
        credentialClaims: Map<String, String?>,
        ocaBundle: OcaBundle,
        rootCaptureBaseDigest: String,
    ): List<Cluster> {
        val clusters = mutableListOf<Cluster>()
        val rootCluster = createCluster(credentialClaims, ocaBundle, rootCaptureBaseDigest)
        if (rootCluster.claims.isEmpty() && rootCluster.childClusters.isNotEmpty()) {
            clusters.addAll(rootCluster.childClusters)
        } else {
            clusters.add(rootCluster)
        }

        val claimsWithoutOca = credentialClaims.filterKeys { key ->
            ocaBundle.getAttributeForJsonPath("$.$key") == null
        }.map(::createFallbackClaim).toMap()

        if (claimsWithoutOca.isNotEmpty()) {
            val cluster = Cluster(claims = claimsWithoutOca)
            clusters.add(cluster)
        }

        return clusters
    }

    private fun createCluster(
        credentialClaims: Map<String, String?>,
        ocaBundle: OcaBundle,
        captureBaseDigest: String,
        labels: Map<String, String> = emptyMap(),
        order: Int? = null,
    ): Cluster {
        val attributes = ocaBundle.getAttributes(captureBaseDigest)
        val referenceAttributes = attributes
            .filter { it.attributeType.getReferenceValue() != null }
            .toSet()
        val childClusters = createChildClusters(credentialClaims, ocaBundle, referenceAttributes)
        val otherAttributes = attributes - referenceAttributes
        val claims = createClaims(credentialClaims, otherAttributes)
        val clusterDisplays = createClusterDisplays(labels)
        return Cluster(claims = claims, clusterDisplays = clusterDisplays, childClusters = childClusters, order = order ?: -1)
    }

    private fun createChildClusters(
        credentialClaims: Map<String, String?>,
        ocaBundle: OcaBundle,
        attributes: Collection<OcaClaimData>
    ): List<Cluster> {
        return attributes.mapNotNull { attribute ->
            if (attribute.attributeType is AttributeType.Reference) {
                val referenceDigest = attribute.attributeType.getReferenceValue() ?: return@mapNotNull null
                createCluster(credentialClaims, ocaBundle, referenceDigest, attribute.labels, attribute.order)
            } else {
                null
            }
        }
    }

    private fun createClaims(
        credentialClaims: Map<String, String?>,
        attributes: List<OcaClaimData>
    ): Map<CredentialClaim, List<AnyClaimDisplay>> {
        return attributes.mapNotNull { attribute ->
            val claim = credentialClaims.entries.find { (key, _) ->
                "$.$key" in attribute.dataSources.values
            }?.toPair()

            if (claim == null) {
                null
            } else {
                generateOcaClaimDisplays(claim.first, value = claim.second, ocaClaimData = attribute)
            }
        }.toMap()
    }

    private fun createClusterDisplays(labels: Map<String, String>) = labels.map { (locale, label) ->
        ClusterDisplay(name = label, locale = locale)
    }

    private fun createFallbackClaim(claim: Map.Entry<String, String?>): Pair<CredentialClaim, List<AnyClaimDisplay>> {
        val displays = listOf(AnyClaimDisplay(name = claim.key, locale = DisplayLanguage.FALLBACK))
        return CredentialClaim(
            clusterId = UNKNOWN_CLUSTER_ID,
            key = claim.key,
            value = claim.value,
            valueType = ValueType.STRING.value,
        ) to displays
    }

    companion object {
        private const val UNKNOWN_CLUSTER_ID = -1L
    }
}
