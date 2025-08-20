@file:Suppress("TooManyFunctions")

package ch.admin.foitt.openid4vc.domain.model.credentialoffer

import ch.admin.foitt.openid4vc.domain.model.CreateJwkError
import ch.admin.foitt.openid4vc.domain.model.JwkError
import ch.admin.foitt.openid4vc.domain.model.vcSdJwt.VcSdJwtError
import ch.admin.foitt.openid4vc.domain.model.vcSdJwt.VerifyJwtError
import ch.admin.foitt.openid4vc.utils.JsonError
import ch.admin.foitt.openid4vc.utils.JsonParsingError
import timber.log.Timber

interface CredentialOfferError {
    data object InvalidGrant :
        FetchCredentialByConfigError, FetchVerifiableCredentialError, FetchCredentialError

    data object UnsupportedGrantType :
        FetchCredentialByConfigError, FetchVerifiableCredentialError, FetchCredentialError

    data object UnsupportedCredentialIdentifier : FetchCredentialByConfigError
    data object UnsupportedProofType :
        FetchCredentialByConfigError,
        FetchVerifiableCredentialError,
        PrepareFetchVerifiableCredentialError,
        FetchCredentialError

    data object UnsupportedCryptographicSuite :
        FetchCredentialByConfigError,
        FetchVerifiableCredentialError,
        PrepareFetchVerifiableCredentialError,
        FetchCredentialError

    data object InvalidCredentialOffer :
        FetchCredentialByConfigError,
        FetchVerifiableCredentialError,
        PrepareFetchVerifiableCredentialError,
        FetchCredentialError

    data object UnsupportedCredentialFormat : FetchCredentialByConfigError
    data object IntegrityCheckFailed : FetchCredentialByConfigError, FetchCredentialError
    data object UnknownIssuer : FetchCredentialByConfigError, FetchCredentialError
    data object UnsupportedKeyStorageSecurityLevel : FetchVerifiableCredentialError, FetchCredentialError, FetchCredentialByConfigError
    data object IncompatibleDeviceKeyStorage : FetchVerifiableCredentialError, FetchCredentialError, FetchCredentialByConfigError
    data object NetworkInfoError :
        FetchIssuerCredentialInfoError,
        FetchCredentialByConfigError,
        FetchVerifiableCredentialError,
        PrepareFetchVerifiableCredentialError,
        FetchIssuerConfigurationError,
        FetchCredentialError

    data class Unexpected(val cause: Throwable?) :
        FetchIssuerCredentialInfoError,
        FetchCredentialByConfigError,
        FetchVerifiableCredentialError,
        PrepareFetchVerifiableCredentialError,
        FetchIssuerConfigurationError,
        FetchCredentialError
}

sealed interface FetchIssuerCredentialInfoError
sealed interface FetchCredentialByConfigError
internal sealed interface FetchCredentialError
sealed interface FetchVerifiableCredentialError
sealed interface PrepareFetchVerifiableCredentialError
sealed interface FetchIssuerConfigurationError

internal fun FetchCredentialError.toFetchCredentialByConfigError(): FetchCredentialByConfigError = when (this) {
    is CredentialOfferError.IntegrityCheckFailed -> this
    is CredentialOfferError.InvalidCredentialOffer -> this
    is CredentialOfferError.InvalidGrant -> this
    is CredentialOfferError.NetworkInfoError -> this
    is CredentialOfferError.Unexpected -> this
    is CredentialOfferError.UnsupportedCryptographicSuite -> this
    is CredentialOfferError.UnsupportedGrantType -> this
    is CredentialOfferError.UnsupportedProofType -> this
    is CredentialOfferError.UnknownIssuer -> this
    is CredentialOfferError.UnsupportedKeyStorageSecurityLevel -> this
    is CredentialOfferError.IncompatibleDeviceKeyStorage -> this
}

internal fun FetchVerifiableCredentialError.toFetchCredentialError(): FetchCredentialError = when (this) {
    is CredentialOfferError.InvalidCredentialOffer -> this
    is CredentialOfferError.InvalidGrant -> this
    is CredentialOfferError.NetworkInfoError -> this
    is CredentialOfferError.Unexpected -> this
    is CredentialOfferError.UnsupportedCryptographicSuite -> this
    is CredentialOfferError.UnsupportedGrantType -> this
    is CredentialOfferError.UnsupportedProofType -> this
    is CredentialOfferError.UnsupportedKeyStorageSecurityLevel -> this
    is CredentialOfferError.IncompatibleDeviceKeyStorage -> this
}

internal fun FetchIssuerCredentialInfoError.toPrepareFetchVerifiableCredentialError(): PrepareFetchVerifiableCredentialError = when (this) {
    is CredentialOfferError.NetworkInfoError -> this
    is CredentialOfferError.Unexpected -> this
}

internal fun JsonParsingError.toFetchIssuerCredentialInfoError(): FetchIssuerCredentialInfoError = when (this) {
    is JsonError.Unexpected -> CredentialOfferError.Unexpected(this.throwable)
}

internal fun FetchIssuerConfigurationError.toPrepareFetchVerifiableCredentialError(): PrepareFetchVerifiableCredentialError = when (this) {
    is CredentialOfferError.NetworkInfoError -> this
    is CredentialOfferError.Unexpected -> this
}

internal fun CreateJwkError.toFetchVerifiableCredentialError(): FetchVerifiableCredentialError = when (this) {
    is JwkError.UnsupportedCryptographicSuite -> CredentialOfferError.UnsupportedCryptographicSuite
    is JwkError.Unexpected -> CredentialOfferError.Unexpected(cause)
}

internal fun VerifyJwtError.toFetchCredentialError(): FetchCredentialError = when (this) {
    VcSdJwtError.InvalidJwt,
    VcSdJwtError.DidDocumentDeactivated -> CredentialOfferError.IntegrityCheckFailed

    VcSdJwtError.NetworkError -> CredentialOfferError.NetworkInfoError
    VcSdJwtError.IssuerValidationFailed -> CredentialOfferError.UnknownIssuer
    is VcSdJwtError.Unexpected -> CredentialOfferError.Unexpected(cause)
}

internal fun Throwable.toFetchCredentialError(message: String): FetchCredentialError {
    Timber.e(t = this, message = message)
    return CredentialOfferError.Unexpected(this)
}
