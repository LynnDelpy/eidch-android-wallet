package ch.admin.foitt.wallet.platform.database.domain.model

import androidx.room.Embedded
import androidx.room.Relation

data class ClusterWithDisplaysAndClaims(
    @Embedded
    val cluster: CredentialClaimClusterEntity,

    @Relation(
        entity = CredentialClaimClusterDisplayEntity::class,
        parentColumn = "id",
        entityColumn = "clusterId",
    )
    val displays: List<CredentialClaimClusterDisplayEntity>,

    @Relation(
        entity = CredentialClaim::class,
        parentColumn = "id",
        entityColumn = "clusterId"
    )
    val claims: List<CredentialClaimWithDisplays>,

    /*
    @Relation(
        entity = ClusterEntity::class,
        parentColumn = "id",
        entityColumn = "parentClusterId",
    )
    val childClusters: List<ClusterWithDisplaysAndClaims>

     */
)
