@file:Suppress("TooManyFunctions")

package ch.admin.foitt.wallet.platform.trustRegistry.domain.model

import ch.admin.foitt.openid4vc.domain.model.vcSdJwt.VcSdJwtError
import ch.admin.foitt.openid4vc.domain.model.vcSdJwt.VerifyJwtError
import ch.admin.foitt.wallet.platform.utils.JsonError
import ch.admin.foitt.wallet.platform.utils.JsonParsingError
import timber.log.Timber

interface TrustRegistryError {
    data object InvalidTrustStatus :
        FetchTrustStatementForIssuanceError,
        FetchTrustStatementForVerificationError,
        ProcessMetadataV1TrustStatementError,
        ProcessIdentityV1TrustStatementError,
        FetchVcSchemaTrustStatusError

    data class Unexpected(val cause: Throwable?) :
        FetchTrustStatementForIssuanceError,
        FetchTrustStatementForVerificationError,
        ProcessMetadataV1TrustStatementError,
        ProcessIdentityV1TrustStatementError,
        FetchVcSchemaTrustStatusError,
        GetTrustUrlFromDidError,
        TrustStatementRepositoryError,
        ValidateTrustStatementError
}

sealed interface FetchTrustStatementForIssuanceError
sealed interface FetchTrustStatementForVerificationError
sealed interface ProcessMetadataV1TrustStatementError
sealed interface ProcessIdentityV1TrustStatementError
sealed interface FetchVcSchemaTrustStatusError
sealed interface GetTrustUrlFromDidError {
    data class NoTrustRegistryMapping(val message: String) : GetTrustUrlFromDidError
}
sealed interface TrustStatementRepositoryError
sealed interface ValidateTrustStatementError

fun GetTrustUrlFromDidError.toProcessMetadataV1TrustStatementError(): ProcessMetadataV1TrustStatementError = when (this) {
    is TrustRegistryError.Unexpected -> this
    is GetTrustUrlFromDidError.NoTrustRegistryMapping -> {
        Timber.w(message = this.message)
        TrustRegistryError.Unexpected(null)
    }
}

fun TrustStatementRepositoryError.toProcessMetadataV1TrustStatementError(): ProcessMetadataV1TrustStatementError = when (this) {
    is TrustRegistryError.Unexpected -> this
}

fun Throwable.toProcessMetadataV1TrustStatementError(message: String): ProcessMetadataV1TrustStatementError {
    Timber.e(t = this, message = message)
    return TrustRegistryError.Unexpected(this)
}

fun JsonParsingError.toProcessMetadataV1TrustStatementError(): ProcessMetadataV1TrustStatementError = when (this) {
    is JsonError.Unexpected -> TrustRegistryError.Unexpected(throwable)
}

fun GetTrustUrlFromDidError.toProcessIdentityV1TrustStatementError(): ProcessIdentityV1TrustStatementError = when (this) {
    is TrustRegistryError.Unexpected -> this
    is GetTrustUrlFromDidError.NoTrustRegistryMapping -> {
        Timber.w(message = this.message)
        TrustRegistryError.Unexpected(null)
    }
}

fun TrustStatementRepositoryError.toProcessIdentityV1TrustStatementError(): ProcessIdentityV1TrustStatementError = when (this) {
    is TrustRegistryError.Unexpected -> this
}

fun Throwable.toProcessIdentityV1TrustStatementError(message: String): ProcessIdentityV1TrustStatementError {
    Timber.e(t = this, message = message)
    return TrustRegistryError.Unexpected(this)
}

fun JsonParsingError.toProcessIdentityV1TrustStatementError(): ProcessIdentityV1TrustStatementError = when (this) {
    is JsonError.Unexpected -> TrustRegistryError.Unexpected(throwable)
}

fun GetTrustUrlFromDidError.toFetchVcSchemaTrustStatusError(): FetchVcSchemaTrustStatusError = when (this) {
    is TrustRegistryError.Unexpected -> this
    is GetTrustUrlFromDidError.NoTrustRegistryMapping -> {
        Timber.w(message = this.message)
        TrustRegistryError.Unexpected(null)
    }
}

fun TrustStatementRepositoryError.toFetchVcSchemaTrustStatusError(): FetchVcSchemaTrustStatusError = when (this) {
    is TrustRegistryError.Unexpected -> this
}

fun Throwable.toFetchVcSchemaTrustStatusError(message: String): FetchVcSchemaTrustStatusError {
    Timber.e(t = this, message = message)
    return TrustRegistryError.Unexpected(this)
}

fun Throwable.toTrustStatementRepositoryError(message: String): TrustStatementRepositoryError {
    Timber.e(t = this, message = message)
    return TrustRegistryError.Unexpected(this)
}

fun Throwable.toGetTrustDomainFromDidError(message: String): GetTrustUrlFromDidError {
    Timber.e(t = this, message = message)
    return TrustRegistryError.Unexpected(this)
}

fun VerifyJwtError.toValidateTrustStatementError(): ValidateTrustStatementError = when (this) {
    VcSdJwtError.NetworkError,
    VcSdJwtError.InvalidJwt,
    VcSdJwtError.IssuerValidationFailed -> TrustRegistryError.Unexpected(null)
    VcSdJwtError.DidDocumentDeactivated -> TrustRegistryError.Unexpected(null)
    is VcSdJwtError.Unexpected -> TrustRegistryError.Unexpected(cause)
}

fun Throwable.toValidateTrustStatementError(message: String): ValidateTrustStatementError {
    Timber.e(t = this, message = message)
    return TrustRegistryError.Unexpected(this)
}
