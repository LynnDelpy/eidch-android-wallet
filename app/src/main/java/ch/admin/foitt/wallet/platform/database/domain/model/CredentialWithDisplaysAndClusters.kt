package ch.admin.foitt.wallet.platform.database.domain.model

import androidx.room.Embedded
import androidx.room.Relation

data class CredentialWithDisplaysAndClusters(
    @Embedded
    val credential: Credential,
    @Relation(
        entity = CredentialDisplay::class,
        parentColumn = "id",
        entityColumn = "credentialId",
    )
    val credentialDisplays: List<CredentialDisplay>,
    @Relation(
        entity = CredentialClaimClusterEntity::class,
        parentColumn = "id",
        entityColumn = "credentialId",
    )
    val clusters: List<ClusterWithDisplaysAndClaims>,
)
