package ch.admin.foitt.wallet.platform.holderBinding.domain.model

import ch.admin.foitt.wallet.platform.appAttestation.domain.model.AttestationError
import ch.admin.foitt.wallet.platform.appAttestation.domain.model.RequestKeyAttestationError
import timber.log.Timber

interface KeyPairError {
    data object UnsupportedCryptographicSuite : GenerateKeyPairError
    data object UnsupportedKeyStorageSecurityLevel : CreateJWSKeyPairError, GenerateKeyPairError
    data object IncompatibleDeviceKeyStorage : CreateJWSKeyPairError, GenerateKeyPairError
    data object InvalidKeyAttestation : GenerateKeyPairError
    data class Unexpected(val throwable: Throwable?) : CreateJWSKeyPairError, GenerateKeyPairError
}

sealed interface CreateJWSKeyPairError
sealed interface GenerateKeyPairError

fun CreateKeyGenSpecError.toCreateJWSKeyPairError(): CreateJWSKeyPairError = when (this) {
    is KeyGenSpecError.Unexpected -> KeyPairError.Unexpected(cause)
}

fun CreateJWSKeyPairError.toGenerateKeyPairError(): GenerateKeyPairError = when (this) {
    is KeyPairError.UnsupportedKeyStorageSecurityLevel -> this
    is KeyPairError.IncompatibleDeviceKeyStorage -> this
    is KeyPairError.Unexpected -> this
}

fun RequestKeyAttestationError.toGenerateKeyPairError(): GenerateKeyPairError = when (this) {
    is AttestationError.UnsupportedKeyStorageSecurityLevel -> KeyPairError.UnsupportedKeyStorageSecurityLevel
    is AttestationError.IncompatibleDeviceKeyStorage -> KeyPairError.IncompatibleDeviceKeyStorage
    is AttestationError.ValidationError -> KeyPairError.InvalidKeyAttestation
    is AttestationError.NetworkError -> KeyPairError.Unexpected(null)
    is AttestationError.Unexpected -> KeyPairError.Unexpected(throwable)
}

fun Throwable.toCreateJWSKeyPairError(message: String): CreateJWSKeyPairError {
    Timber.e(this, message)
    return KeyPairError.Unexpected(this)
}
