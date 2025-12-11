@file:Suppress("TooManyFunctions")

package ch.admin.foitt.wallet.feature.presentationRequest.domain.model

import ch.admin.foitt.openid4vc.domain.model.presentationRequest.SubmitAnyCredentialPresentationError
import ch.admin.foitt.wallet.platform.credential.domain.model.AnyCredentialError
import ch.admin.foitt.wallet.platform.credential.domain.model.CredentialError
import ch.admin.foitt.wallet.platform.credential.domain.model.CredentialError.Unexpected
import ch.admin.foitt.wallet.platform.credential.domain.model.GetAnyCredentialError
import ch.admin.foitt.wallet.platform.credential.domain.model.MapToCredentialDisplayDataError
import ch.admin.foitt.wallet.platform.ssi.domain.model.GetCredentialsWithDetailsFlowError
import ch.admin.foitt.wallet.platform.ssi.domain.model.MapToCredentialClaimDataError
import ch.admin.foitt.wallet.platform.ssi.domain.model.SsiError
import ch.admin.foitt.wallet.platform.ssi.domain.model.VerifiableCredentialWithDisplaysAndClustersRepositoryError
import ch.admin.foitt.openid4vc.domain.model.presentationRequest.PresentationRequestError as OpenIdPresentationRequestError

interface PresentationRequestError {
    data object RawSdJwtParsingError : SubmitPresentationError
    data object ValidationError : SubmitPresentationError
    data object InvalidCredentialError : SubmitPresentationError
    data object VerificationError : SubmitPresentationError
    data object InvalidUrl : SubmitPresentationError
    data object NetworkError : SubmitPresentationError
    data class Unexpected(val throwable: Throwable?) :
        SubmitPresentationError,
        GetPresentationRequestFlowError,
        GetPresentationRequestCredentialListFlowError
}

sealed interface SubmitPresentationError
sealed interface GetPresentationRequestFlowError
sealed interface GetPresentationRequestCredentialListFlowError

fun GetAnyCredentialError.toSubmitPresentationError(): SubmitPresentationError = when (this) {
    is Unexpected -> PresentationRequestError.Unexpected(cause)
}

fun SubmitAnyCredentialPresentationError.toSubmitPresentationError(): SubmitPresentationError = when (this) {
    OpenIdPresentationRequestError.NetworkError -> PresentationRequestError.NetworkError
    OpenIdPresentationRequestError.ValidationError -> PresentationRequestError.ValidationError
    OpenIdPresentationRequestError.VerificationError -> PresentationRequestError.VerificationError
    OpenIdPresentationRequestError.InvalidCredentialError -> PresentationRequestError.InvalidCredentialError
    is OpenIdPresentationRequestError.Unexpected -> PresentationRequestError.Unexpected(throwable)
}

fun MapToCredentialClaimDataError.toGetPresentationRequestFlowError(): GetPresentationRequestFlowError = when (this) {
    is SsiError.Unexpected -> PresentationRequestError.Unexpected(cause)
}

fun VerifiableCredentialWithDisplaysAndClustersRepositoryError.toGetPresentationRequestFlowError():
    GetPresentationRequestFlowError = when (this) {
    is SsiError.Unexpected -> PresentationRequestError.Unexpected(cause)
}

fun GetCredentialsWithDetailsFlowError.toGetPresentationRequestCredentialListFlowError():
    GetPresentationRequestCredentialListFlowError = when (this) {
    is SsiError.Unexpected -> PresentationRequestError.Unexpected(cause)
}

fun AnyCredentialError.toGetPresentationRequestFlowError(): GetPresentationRequestFlowError = when (this) {
    is CredentialError.Unexpected -> PresentationRequestError.Unexpected(cause)
}

fun MapToCredentialDisplayDataError.toGetPresentationRequestFlowError(): GetPresentationRequestFlowError = when (this) {
    is CredentialError.Unexpected -> PresentationRequestError.Unexpected(cause)
}
