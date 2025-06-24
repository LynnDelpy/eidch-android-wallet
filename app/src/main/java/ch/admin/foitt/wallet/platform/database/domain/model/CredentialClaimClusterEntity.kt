package ch.admin.foitt.wallet.platform.database.domain.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = Credential::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("credentialId"),
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = CredentialClaimClusterEntity::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("parentClusterId"),
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE,
        )
    ],
    indices = [
        Index("credentialId"),
        Index("parentClusterId")
    ]
)
data class CredentialClaimClusterEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val credentialId: Long, // Foreign key
    val parentClusterId: Long?, // Foreign key (on this table)
    val order: Int,
)

fun Cluster.toCredentialClaimClusterEntity(credentialId: Long, parentClusterId: Long? = null) = CredentialClaimClusterEntity(
    credentialId = credentialId,
    parentClusterId = parentClusterId,
    order = this.order,
)
