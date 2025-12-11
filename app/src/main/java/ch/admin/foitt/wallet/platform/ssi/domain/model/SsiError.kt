@file:Suppress("TooManyFunctions")

package ch.admin.foitt.wallet.platform.ssi.domain.model

import ch.admin.foitt.wallet.platform.credential.domain.model.CredentialError
import ch.admin.foitt.wallet.platform.credential.domain.model.MapToCredentialDisplayDataError
import timber.log.Timber

interface SsiError {
    data class Unexpected(val cause: Throwable?) :
        CredentialClaimDisplayRepositoryError,
        CredentialClaimRepositoryError,
        CredentialIssuerDisplayRepositoryError,
        CredentialRepositoryError,
        CredentialWithDisplaysRepositoryError,
        DeleteCredentialError,
        MapToCredentialClaimDataError,
        CredentialOfferRepositoryError,
        GetCredentialDetailFlowError,
        GetCredentialsWithDetailsFlowError,
        CredentialWithKeyBindingRepositoryError,
        VerifiableCredentialWithDisplaysAndClustersRepositoryError
}

sealed interface CredentialClaimDisplayRepositoryError
sealed interface CredentialClaimRepositoryError
sealed interface CredentialIssuerDisplayRepositoryError
sealed interface CredentialRepositoryError
sealed interface CredentialWithDisplaysRepositoryError
sealed interface CredentialOfferRepositoryError
sealed interface DeleteCredentialError
sealed interface MapToCredentialClaimDataError
sealed interface GetCredentialDetailFlowError
sealed interface GetCredentialsWithDetailsFlowError
sealed interface CredentialWithKeyBindingRepositoryError
sealed interface VerifiableCredentialWithDisplaysAndClustersRepositoryError

internal fun CredentialRepositoryError.toDeleteCredentialError() = when (this) {
    is SsiError.Unexpected -> SsiError.Unexpected(cause)
}
internal fun CredentialWithKeyBindingRepositoryError.toDeleteCredentialError() = when (this) {
    is SsiError.Unexpected -> SsiError.Unexpected(cause)
}

internal fun Throwable.toMapToCredentialClaimDataError(message: String): MapToCredentialClaimDataError {
    Timber.e(t = this, message = message)
    return SsiError.Unexpected(this)
}

internal fun Throwable.toCredentialOfferRepositoryError(message: String): CredentialOfferRepositoryError {
    Timber.e(t = this, message = message)
    return SsiError.Unexpected(this)
}

internal fun Throwable.toCredentialIssuerDisplayRepositoryError(message: String): CredentialIssuerDisplayRepositoryError {
    Timber.e(t = this, message = message)
    return SsiError.Unexpected(this)
}

internal fun Throwable.toVerifiableCredentialWithDisplaysAndClustersRepositoryError(
    message: String
): VerifiableCredentialWithDisplaysAndClustersRepositoryError {
    Timber.e(t = this, message = message)
    return SsiError.Unexpected(this)
}

internal fun MapToCredentialClaimDataError.toGetCredentialDetailFlowError(): GetCredentialDetailFlowError = when (this) {
    is SsiError.Unexpected -> this
}

internal fun VerifiableCredentialWithDisplaysAndClustersRepositoryError.toGetCredentialDetailFlowError():
    GetCredentialDetailFlowError = when (this) {
    is SsiError.Unexpected -> this
}

internal fun VerifiableCredentialWithDisplaysAndClustersRepositoryError.toGetCredentialsWithDetailsFlowError():
    GetCredentialsWithDetailsFlowError = when (this) {
    is SsiError.Unexpected -> this
}

internal fun MapToCredentialDisplayDataError.toGetCredentialDetailFlowError(): GetCredentialDetailFlowError = when (this) {
    is CredentialError.Unexpected -> SsiError.Unexpected(cause)
}

internal fun MapToCredentialDisplayDataError.toGetCredentialsWithDisplaysFlowError(): GetCredentialsWithDetailsFlowError = when (this) {
    is CredentialError.Unexpected -> SsiError.Unexpected(cause)
}
