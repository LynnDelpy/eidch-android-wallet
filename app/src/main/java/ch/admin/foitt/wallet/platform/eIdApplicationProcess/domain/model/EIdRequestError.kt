@file:Suppress("TooManyFunctions")

package ch.admin.foitt.wallet.platform.eIdApplicationProcess.domain.model

import ch.admin.foitt.wallet.platform.appAttestation.domain.model.AttestationError
import ch.admin.foitt.wallet.platform.appAttestation.domain.model.ClientAttestationRepositoryError
import ch.admin.foitt.wallet.platform.appAttestation.domain.model.GenerateProofOfPossessionError
import ch.admin.foitt.wallet.platform.eIdApplicationProcess.domain.model.EIdRequestError.InsufficientKeyStorageResistance
import ch.admin.foitt.wallet.platform.eIdApplicationProcess.domain.model.EIdRequestError.InvalidClientAttestation
import ch.admin.foitt.wallet.platform.eIdApplicationProcess.domain.model.EIdRequestError.InvalidKeyAttestation
import ch.admin.foitt.wallet.platform.eIdApplicationProcess.domain.model.EIdRequestError.NetworkError
import ch.admin.foitt.wallet.platform.eIdApplicationProcess.domain.model.EIdRequestError.Unexpected
import ch.admin.foitt.wallet.platform.utils.JsonError
import ch.admin.foitt.wallet.platform.utils.JsonParsingError
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
        GuardianVerificationError,
        StartOnlineSessionError,
        PairWalletError,
        StartAutoVerificationError,
        AvRepositoryError,
        AvUploadFilesError

    data object InvalidClientAttestation : ValidateAttestationsError
    data object InvalidKeyAttestation : ValidateAttestationsError
    data object InsufficientKeyStorageResistance : ValidateAttestationsError
    data class DeclinedProcessData(val cause: String?) :
        AvRepositoryError,
        AvUploadFilesError
    data object MissingJwt : AvUploadFilesError
    data class FileNotFound(val fileName: String) : AvUploadFilesError

    data class Unexpected(val cause: Throwable?) :
        EIdRequestCaseRepositoryError,
        EIdRequestStateRepositoryError,
        EIdRequestCaseWithStateRepositoryError,
        SIdRepositoryError,
        ApplyRequestError,
        StateRequestError,
        GuardianVerificationError,
        ValidateAttestationsError,
        EIdRequestFileRepositoryError,
        StartOnlineSessionError,
        PairWalletError,
        StartAutoVerificationError,
        AvRepositoryError,
        AvUploadFilesError
}

sealed interface EIdRequestCaseRepositoryError
sealed interface EIdRequestStateRepositoryError
sealed interface EIdRequestCaseWithStateRepositoryError
sealed interface ApplyRequestError
sealed interface StateRequestError
sealed interface GuardianVerificationError
sealed interface SIdRepositoryError
sealed interface ValidateAttestationsError
sealed interface EIdRequestFileRepositoryError
sealed interface StartOnlineSessionError
sealed interface PairWalletError
sealed interface StartAutoVerificationError
sealed interface AvRepositoryError
sealed interface AvUploadFilesError

internal fun SIdRepositoryError.toStartOnlineSessionError(): StartOnlineSessionError = when (this) {
    is Unexpected -> this
    is NetworkError -> this
}

internal fun SIdRepositoryError.toApplyRequestError(): ApplyRequestError = when (this) {
    is Unexpected -> this
    is NetworkError -> this
}

internal fun SIdRepositoryError.toStartAutoVerificationError(): StartAutoVerificationError = when (this) {
    is Unexpected -> this
    is NetworkError -> this
}

internal fun SIdRepositoryError.toPairWalletError(): PairWalletError = when (this) {
    is Unexpected -> this
    is NetworkError -> this
}

internal fun AvRepositoryError.toAvUploadFilesError(): AvUploadFilesError = when (this) {
    is Unexpected -> this
    is EIdRequestError.DeclinedProcessData -> this
    is NetworkError -> this
}

internal fun EIdRequestFileRepositoryError.toAvUploadFilesError(): AvUploadFilesError = when (this) {
    is Unexpected -> this
}

internal fun ClientAttestationRepositoryError.toStartOnlineSessionError(): StartOnlineSessionError = when (this) {
    is AttestationError.Unexpected -> Unexpected(this.throwable)
}

internal fun ClientAttestationRepositoryError.toPairWalletError(): PairWalletError = when (this) {
    is AttestationError.Unexpected -> Unexpected(this.throwable)
}

internal fun ClientAttestationRepositoryError.toStartAutoVerificationError(): StartAutoVerificationError = when (this) {
    is AttestationError.Unexpected -> Unexpected(this.throwable)
}

internal fun ClientAttestationRepositoryError.toApplyRequestError(): ApplyRequestError = when (this) {
    is AttestationError.Unexpected -> Unexpected(this.throwable)
}

internal fun GenerateProofOfPossessionError.toApplyRequestError(): ApplyRequestError = when (this) {
    is AttestationError.Unexpected -> Unexpected(this.throwable)
}

internal fun GenerateProofOfPossessionError.toStartOnlineSessionError(): StartOnlineSessionError = when (this) {
    is AttestationError.Unexpected -> Unexpected(this.throwable)
}

internal fun GenerateProofOfPossessionError.toPairWalletError(): PairWalletError = when (this) {
    is AttestationError.Unexpected -> Unexpected(this.throwable)
}

internal fun GenerateProofOfPossessionError.toStartAutoVerificationError(): StartAutoVerificationError = when (this) {
    is AttestationError.Unexpected -> Unexpected(this.throwable)
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

internal fun JsonParsingError.toApplyRequestError(): ApplyRequestError = when (this) {
    is JsonError.Unexpected -> Unexpected(this.throwable)
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

internal fun Throwable.toEIdRequestFileError(message: String): EIdRequestFileRepositoryError {
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

internal fun Throwable.toAvRepositoryError(message: String): AvRepositoryError {
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

private suspend fun ClientRequestException.toValidateAttestationsErrors(): ValidateAttestationsError = when (this.response.status.value) {
    HttpStatusCode.UnprocessableEntity.value -> {
        val errors = runSuspendCatching { this.response.body<SIdErrorResponse>() }
            .getOrElse {
                return Unexpected(this)
            }
        when {
            errors.contains(SIdError.INSUFFICIENT_KEY_STORAGE_RESISTANCE) -> InsufficientKeyStorageResistance
            errors.contains(SIdError.INVALID_KEY_ATTESTATION) -> InvalidKeyAttestation
            errors.contains(SIdError.INVALID_CLIENT_ATTESTATION) -> InvalidClientAttestation
            else -> Unexpected(this)
        }
    }
    else -> Unexpected(this)
}

private fun SIdErrorResponse.contains(errorCode: String) = errors.any { error -> error.code == errorCode }
