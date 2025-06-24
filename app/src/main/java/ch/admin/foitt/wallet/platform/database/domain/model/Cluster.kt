package ch.admin.foitt.wallet.platform.database.domain.model

import ch.admin.foitt.wallet.platform.credential.domain.model.AnyClaimDisplay

data class Cluster(
    val order: Int = -1,
    val claims: Map<CredentialClaim, List<AnyClaimDisplay>>,
    val clusterDisplays: List<ClusterDisplay> = emptyList(),
    val childClusters: List<Cluster> = emptyList(),
)
