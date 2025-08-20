package ch.admin.foitt.wallet.platform.holderBinding.domain.model

import timber.log.Timber

sealed interface KeyGenSpecError {
    data class Unexpected(val cause: Throwable) : CreateKeyGenSpecError
}

sealed interface CreateKeyGenSpecError

internal fun Throwable.toCreateKeyGenSpecError(message: String): CreateKeyGenSpecError {
    Timber.e(t = this, message = message)
    return KeyGenSpecError.Unexpected(this)
}
