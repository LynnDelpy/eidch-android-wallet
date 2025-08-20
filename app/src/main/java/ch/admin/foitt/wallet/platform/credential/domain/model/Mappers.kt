package ch.admin.foitt.wallet.platform.credential.domain.model

import ch.admin.foitt.openid4vc.domain.model.SigningAlgorithm
import ch.admin.foitt.openid4vc.domain.model.anycredential.AnyCredential
import ch.admin.foitt.openid4vc.domain.model.anycredential.CredentialValidity
import ch.admin.foitt.openid4vc.domain.model.anycredential.getCredentialValidity
import ch.admin.foitt.openid4vc.domain.model.credentialoffer.metadata.CredentialFormat
import ch.admin.foitt.openid4vc.domain.model.keyBinding.KeyBinding
import ch.admin.foitt.openid4vc.domain.model.vcSdJwt.VcSdJwtCredential
import ch.admin.foitt.wallet.platform.credentialStatus.domain.model.CredentialDisplayStatus
import ch.admin.foitt.wallet.platform.database.domain.model.Credential
import ch.admin.foitt.wallet.platform.database.domain.model.CredentialKeyBindingEntity
import ch.admin.foitt.wallet.platform.database.domain.model.CredentialStatus
import ch.admin.foitt.wallet.platform.database.domain.model.CredentialWithKeyBinding
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import com.github.michaelbull.result.coroutines.runSuspendCatching
import com.github.michaelbull.result.mapError

internal fun CredentialWithKeyBinding.toAnyCredential(): Result<AnyCredential, AnyCredentialError> = binding {
    val credential = this@toAnyCredential.credential
    val keyBinding = this@toAnyCredential.keyBinding?.toKeyBinding()
        ?.mapError(KeyBindingError::toAnyCredentialError)
        ?.bind()

    val anyCredential = runSuspendCatching {
        when (credential.format) {
            CredentialFormat.VC_SD_JWT -> VcSdJwtCredential(
                id = credential.id,
                keyBinding = keyBinding,
                payload = credential.payload,
                validFrom = credential.validFrom,
                validUntil = credential.validUntil,
            )

            CredentialFormat.UNKNOWN -> error("Unsupported credential format")
        }
    }.mapError { throwable ->
        throwable.toAnyCredentialError("Credential.toAnyCredential() error")
    }.bind()

    anyCredential
}

fun CredentialKeyBindingEntity.toKeyBinding(): Result<KeyBinding, KeyBindingError> = binding {
    val signingAlgorithm = runSuspendCatching {
        SigningAlgorithm.valueOf(algorithm)
    }.mapError { throwable ->
        throwable.toKeyBindingError("SigningAlgorithm.valueOf($algorithm) error")
    }.bind()

    KeyBinding(
        identifier = id,
        algorithm = signingAlgorithm,
        bindingType = bindingType,
        publicKey = publicKey,
        privateKey = privateKey,
    )
}

internal fun Credential.getDisplayStatus(status: CredentialStatus): CredentialDisplayStatus {
    val validity = getCredentialValidity(validFrom, validUntil)
    return status.getDisplayStatus(validity)
}

private fun CredentialStatus.getDisplayStatus(validity: CredentialValidity) = when (validity) {
    // Priority is given to local state
    is CredentialValidity.Expired -> CredentialDisplayStatus.Expired(validity.expiredAt)
    is CredentialValidity.NotYetValid -> CredentialDisplayStatus.NotYetValid(validity.validFrom)
    CredentialValidity.Valid -> this.toDisplayStatus()
}

internal fun CredentialStatus.toDisplayStatus() = when (this) {
    CredentialStatus.VALID -> CredentialDisplayStatus.Valid
    CredentialStatus.REVOKED -> CredentialDisplayStatus.Revoked
    CredentialStatus.SUSPENDED -> CredentialDisplayStatus.Suspended
    CredentialStatus.UNSUPPORTED -> CredentialDisplayStatus.Unsupported
    CredentialStatus.UNKNOWN -> CredentialDisplayStatus.Unknown
}
