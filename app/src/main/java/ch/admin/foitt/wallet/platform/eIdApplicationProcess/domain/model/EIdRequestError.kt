package ch.admin.foitt.wallet.platform.eIdApplicationProcess.domain.model

import ch.admin.foitt.wallet.platform.eIdApplicationProcess.domain.model.EIdRequestError.InvalidClientAttestation
import ch.admin.foitt.wallet.platform.eIdApplicationProcess.domain.model.EIdRequestError.InvalidKeyAttestation
import ch.admin.foitt.wallet.platform.eIdApplicationProcess.domain.model.EIdRequestError.NetworkError
import ch.admin.foitt.wallet.platform.eIdApplicationProcess.domain.model.EIdRequestError.Unexpected
import com.github.michaelbull.result.coroutines.runSuspendCatching
import com.github.michaelbull.result.getOrElse
import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.http.HttpStatusCode
import timber.log.Timber
import java.io.IOException

interface EIdRequestError {
    data object NetworkError :
        SIdRepositoryError,
        ValidateAttestationsError,
        ApplyRequestError,
        StateRequestError,
        GuardianVerificationError

    data object InvalidClientAttestation : ValidateAttestationsError
    data object InvalidKeyAttestation : ValidateAttestationsError

    data class Unexpected(val cause: Throwable?) :
        EIdRequestCaseRepositoryError,
        EIdRequestStateRepositoryError,
        EIdRequestCaseWithStateRepositoryError,
        SIdRepositoryError,
        ApplyRequestError,
        StateRequestError,
        GuardianVerificationError,
        ValidateAttestationsError
}

sealed interface EIdRequestCaseRepositoryError
sealed interface EIdRequestStateRepositoryError
sealed interface EIdRequestCaseWithStateRepositoryError
sealed interface ApplyRequestError
sealed interface StateRequestError
sealed interface GuardianVerificationError
sealed interface SIdRepositoryError
sealed interface ValidateAttestationsError

internal fun SIdRepositoryError.toAttestationsValidationError(): ValidateAttestationsError = when (this) {
    is Unexpected -> this
    is NetworkError -> this
}

internal fun SIdRepositoryError.toApplyRequestError(): ApplyRequestError = when (this) {
    is Unexpected -> this
    is NetworkError -> this
}

internal fun SIdRepositoryError.toStateRequestError(): StateRequestError = when (this) {
    is Unexpected -> this
    is NetworkError -> this
}

internal fun SIdRepositoryError.toGuardianVerificationError(): GuardianVerificationError = when (this) {
    is Unexpected -> this
    is NetworkError -> this
}

internal fun EIdRequestStateRepositoryError.toUpdateEIdRequestStateError() = when (this) {
    is Unexpected -> this
}

internal fun Throwable.toEIdRequestCaseRepositoryError(message: String): EIdRequestCaseRepositoryError {
    Timber.e(t = this, message = message)
    return Unexpected(this)
}

internal fun Throwable.toEIdRequestStateRepositoryError(message: String): EIdRequestStateRepositoryError {
    Timber.e(t = this, message = message)
    return Unexpected(this)
}

internal fun Throwable.toEIdRequestCaseWithStateRepositoryError(message: String): EIdRequestCaseWithStateRepositoryError {
    Timber.e(t = this, message = message)
    return Unexpected(this)
}

internal fun Throwable.toSIdRepositoryError(message: String): SIdRepositoryError {
    Timber.e(t = this, message = message)
    return when (this) {
        is IOException -> NetworkError
        else -> Unexpected(this)
    }
}

internal suspend fun Throwable.toValidateAttestationsError(message: String): ValidateAttestationsError {
    Timber.e(t = this, message = message)
    return when (this) {
        is ClientRequestException -> this.toValidateAttestationsErrors()
        is IOException -> NetworkError
        else -> Unexpected(this)
    }
}

private suspend fun ClientRequestException.toValidateAttestationsErrors(): ValidateAttestationsError = when (this.response.status) {
    HttpStatusCode.UnprocessableEntity -> {
        val errors = runSuspendCatching { this.response.body<SIdErrorResponse>() }
            .getOrElse {
                return Unexpected(this)
            }
        if (errors.errors.any { error -> error.code == SIdError.INVALID_KEY_ATTESTATION }) {
            InvalidKeyAttestation
        } else if (errors.errors.any { error -> error.code == SIdError.INVALID_CLIENT_ATTESTATION }) {
            InvalidClientAttestation
        } else {
            Unexpected(this)
        }
    }
    else -> Unexpected(this)
}
