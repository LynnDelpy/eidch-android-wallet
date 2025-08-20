@file:Suppress("TooManyFunctions")

package ch.admin.foitt.wallet.platform.credential.domain.model

import ch.admin.foitt.openid4vc.domain.model.credentialoffer.FetchCredentialByConfigError
import ch.admin.foitt.openid4vc.domain.model.credentialoffer.FetchIssuerCredentialInfoError
import ch.admin.foitt.openid4vc.domain.model.credentialoffer.PrepareFetchVerifiableCredentialError
import ch.admin.foitt.wallet.platform.credential.domain.model.CredentialError.CredentialParsingError
import ch.admin.foitt.wallet.platform.credential.domain.model.CredentialError.DatabaseError
import ch.admin.foitt.wallet.platform.credential.domain.model.CredentialError.IntegrityCheckFailed
import ch.admin.foitt.wallet.platform.credential.domain.model.CredentialError.InvalidCredentialOffer
import ch.admin.foitt.wallet.platform.credential.domain.model.CredentialError.InvalidGenerateMetadataClaims
import ch.admin.foitt.wallet.platform.credential.domain.model.CredentialError.InvalidGrant
import ch.admin.foitt.wallet.platform.credential.domain.model.CredentialError.InvalidJsonScheme
import ch.admin.foitt.wallet.platform.credential.domain.model.CredentialError.NetworkError
import ch.admin.foitt.wallet.platform.credential.domain.model.CredentialError.Unexpected
import ch.admin.foitt.wallet.platform.credential.domain.model.CredentialError.UnknownIssuer
import ch.admin.foitt.wallet.platform.credential.domain.model.CredentialError.UnsupportedCredentialFormat
import ch.admin.foitt.wallet.platform.credential.domain.model.CredentialError.UnsupportedCredentialIdentifier
import ch.admin.foitt.wallet.platform.credential.domain.model.CredentialError.UnsupportedCryptographicSuite
import ch.admin.foitt.wallet.platform.credential.domain.model.CredentialError.UnsupportedGrantType
import ch.admin.foitt.wallet.platform.credential.domain.model.CredentialError.UnsupportedProofType
import ch.admin.foitt.wallet.platform.holderBinding.domain.model.GenerateKeyPairError
import ch.admin.foitt.wallet.platform.holderBinding.domain.model.KeyPairError
import ch.admin.foitt.wallet.platform.oca.domain.model.FetchVcMetadataByFormatError
import ch.admin.foitt.wallet.platform.oca.domain.model.GenerateOcaDisplaysError
import ch.admin.foitt.wallet.platform.oca.domain.model.OcaError
import ch.admin.foitt.wallet.platform.ssi.domain.model.CredentialOfferRepositoryError
import ch.admin.foitt.wallet.platform.ssi.domain.model.CredentialWithKeyBindingRepositoryError
import ch.admin.foitt.wallet.platform.ssi.domain.model.SsiError
import ch.admin.foitt.wallet.platform.utils.JsonError
import ch.admin.foitt.wallet.platform.utils.JsonParsingError
import timber.log.Timber
import ch.admin.foitt.openid4vc.domain.model.credentialoffer.CredentialOfferError as OpenIdCredentialOfferError

sealed interface CredentialError {
    data object InvalidGrant : FetchCredentialError
    data object UnsupportedGrantType : FetchCredentialError
    data object UnsupportedCredentialIdentifier : FetchCredentialError
    data object UnsupportedProofType : FetchCredentialError
    data object UnsupportedCryptographicSuite : FetchCredentialError
    data object InvalidCredentialOffer : FetchCredentialError
    data object UnsupportedCredentialFormat : FetchCredentialError, SaveCredentialError
    data object CredentialParsingError : FetchCredentialError, SaveCredentialError
    data object IntegrityCheckFailed : FetchCredentialError
    data object InvalidGenerateMetadataClaims :
        FetchCredentialError,
        SaveCredentialError,
        GenerateCredentialDisplaysError,
        GenerateMetadataDisplaysError

    data object InvalidJsonScheme : FetchCredentialError
    data object DatabaseError : FetchCredentialError, SaveCredentialError
    data object NetworkError : FetchCredentialError
    data object UnknownIssuer : FetchCredentialError
    data object UnsupportedKeyStorageSecurityLevel : FetchCredentialError
    data object IncompatibleDeviceKeyStorage : FetchCredentialError
    data class Unexpected(val cause: Throwable?) :
        FetchCredentialError,
        SaveCredentialError,
        GetAnyCredentialError,
        GetAnyCredentialsError,
        AnyCredentialError,
        MapToCredentialDisplayDataError,
        GenerateCredentialDisplaysError,
        GenerateMetadataDisplaysError,
        KeyBindingError
}

sealed interface FetchCredentialError
sealed interface SaveCredentialError
sealed interface GetAnyCredentialError
sealed interface GetAnyCredentialsError
sealed interface AnyCredentialError
sealed interface MapToCredentialDisplayDataError
sealed interface GenerateCredentialDisplaysError
sealed interface GenerateMetadataDisplaysError
sealed interface KeyBindingError

fun FetchIssuerCredentialInfoError.toFetchCredentialError(): FetchCredentialError = when (this) {
    OpenIdCredentialOfferError.NetworkInfoError -> NetworkError
    is OpenIdCredentialOfferError.Unexpected -> Unexpected(cause)
}

fun FetchCredentialByConfigError.toFetchCredentialError(): FetchCredentialError = when (this) {
    is OpenIdCredentialOfferError.InvalidGrant -> InvalidGrant
    is OpenIdCredentialOfferError.InvalidCredentialOffer -> InvalidCredentialOffer
    is OpenIdCredentialOfferError.UnsupportedCryptographicSuite -> UnsupportedCryptographicSuite
    is OpenIdCredentialOfferError.UnsupportedGrantType -> UnsupportedGrantType
    is OpenIdCredentialOfferError.UnsupportedProofType -> UnsupportedProofType
    is OpenIdCredentialOfferError.IntegrityCheckFailed -> IntegrityCheckFailed
    is OpenIdCredentialOfferError.NetworkInfoError -> NetworkError
    is OpenIdCredentialOfferError.UnsupportedCredentialIdentifier -> UnsupportedCredentialIdentifier
    is OpenIdCredentialOfferError.UnsupportedCredentialFormat -> UnsupportedCredentialFormat
    is OpenIdCredentialOfferError.Unexpected -> Unexpected(cause)
    is OpenIdCredentialOfferError.UnknownIssuer -> UnknownIssuer
    is OpenIdCredentialOfferError.UnsupportedKeyStorageSecurityLevel -> CredentialError.UnsupportedKeyStorageSecurityLevel
    is OpenIdCredentialOfferError.IncompatibleDeviceKeyStorage -> CredentialError.IncompatibleDeviceKeyStorage
}

fun PrepareFetchVerifiableCredentialError.toFetchCredentialError(): FetchCredentialError = when (this) {
    is OpenIdCredentialOfferError.UnsupportedProofType -> UnsupportedProofType
    is OpenIdCredentialOfferError.UnsupportedCryptographicSuite -> UnsupportedCryptographicSuite
    is OpenIdCredentialOfferError.InvalidCredentialOffer -> InvalidCredentialOffer
    is OpenIdCredentialOfferError.NetworkInfoError -> NetworkError
    is OpenIdCredentialOfferError.Unexpected -> Unexpected(cause)
}

fun GenerateKeyPairError.toFetchCredentialError(): FetchCredentialError = when (this) {
    is KeyPairError.InvalidKeyAttestation -> Unexpected(null)
    is KeyPairError.UnsupportedCryptographicSuite -> InvalidCredentialOffer
    is KeyPairError.IncompatibleDeviceKeyStorage -> CredentialError.IncompatibleDeviceKeyStorage
    is KeyPairError.UnsupportedKeyStorageSecurityLevel -> CredentialError.UnsupportedKeyStorageSecurityLevel
    is KeyPairError.Unexpected -> Unexpected(throwable)
}

fun SaveCredentialError.toFetchCredentialError(): FetchCredentialError = when (this) {
    is CredentialParsingError -> this
    is DatabaseError -> this
    is Unexpected -> this
    is UnsupportedCredentialFormat -> this
    is InvalidGenerateMetadataClaims -> this
}

fun JsonParsingError.toGenerateMetadataDisplaysError(): GenerateMetadataDisplaysError = when (this) {
    is JsonError.Unexpected -> Unexpected(throwable)
}

fun Throwable.toGenerateCredentialDisplaysError(message: String): GenerateCredentialDisplaysError {
    Timber.e(t = this, message = message)
    return Unexpected(this)
}

fun CredentialOfferRepositoryError.toSaveCredentialError(): SaveCredentialError = when (this) {
    is SsiError.Unexpected -> Unexpected(cause)
}

fun AnyCredentialError.toGetAnyCredentialError(): GetAnyCredentialError = when (this) {
    is Unexpected -> this
}

fun Throwable.toAnyCredentialError(message: String): AnyCredentialError {
    Timber.e(t = this, message = message)
    return Unexpected(this)
}

fun FetchVcMetadataByFormatError.toFetchCredentialError(): FetchCredentialError = when (this) {
    is OcaError.InvalidOca -> InvalidCredentialOffer
    is OcaError.NetworkError -> NetworkError
    is OcaError.Unexpected -> Unexpected(cause)
    OcaError.InvalidJsonScheme -> InvalidJsonScheme
}

fun GenerateMetadataDisplaysError.toGenerateCredentialDisplaysError(): GenerateCredentialDisplaysError = when (this) {
    is InvalidGenerateMetadataClaims -> this
    is Unexpected -> this
}

fun GenerateOcaDisplaysError.toGenerateCredentialDisplaysError(): GenerateCredentialDisplaysError = when (this) {
    is OcaError.InvalidRootCaptureBase -> InvalidGenerateMetadataClaims
    is OcaError.Unexpected -> Unexpected(cause)
}

fun GenerateCredentialDisplaysError.toFetchCredentialError(): FetchCredentialError = when (this) {
    is InvalidGenerateMetadataClaims -> this
    is Unexpected -> this
}

fun CredentialWithKeyBindingRepositoryError.toGetAnyCredentialsError(): GetAnyCredentialsError = when (this) {
    is SsiError.Unexpected -> Unexpected(cause)
}

fun CredentialWithKeyBindingRepositoryError.toGetAnyCredentialError(): GetAnyCredentialError = when (this) {
    is SsiError.Unexpected -> Unexpected(cause)
}

fun Throwable.toKeyBindingError(message: String): KeyBindingError {
    Timber.e(t = this, message = message)
    return Unexpected(this)
}

fun KeyBindingError.toAnyCredentialError(): AnyCredentialError = when (this) {
    is Unexpected -> this
}
