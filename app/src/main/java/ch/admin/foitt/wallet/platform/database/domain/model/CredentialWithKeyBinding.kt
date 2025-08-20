package ch.admin.foitt.wallet.platform.database.domain.model

import androidx.room.Embedded
import androidx.room.Relation

data class CredentialWithKeyBinding(
    @Embedded
    val credential: Credential,
    @Relation(
        entity = CredentialKeyBindingEntity::class,
        parentColumn = "id",
        entityColumn = "credentialId",
    )
    val keyBinding: CredentialKeyBindingEntity?,
)
