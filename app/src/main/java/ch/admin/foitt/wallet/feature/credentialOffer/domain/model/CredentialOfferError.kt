package ch.admin.foitt.wallet.feature.credentialOffer.domain.model

import ch.admin.foitt.wallet.platform.credential.domain.model.CredentialError
import ch.admin.foitt.wallet.platform.credential.domain.model.MapToCredentialDisplayDataError
import ch.admin.foitt.wallet.platform.ssi.domain.model.MapToCredentialClaimDataError
import ch.admin.foitt.wallet.platform.ssi.domain.model.SsiError
import ch.admin.foitt.wallet.platform.ssi.domain.model.VerifiableCredentialWithDisplaysAndClustersRepositoryError

interface CredentialOfferError {
    data class Unexpected(val throwable: Throwable?) :
        GetCredentialOfferFlowError
}

sealed interface GetCredentialOfferFlowError

internal fun VerifiableCredentialWithDisplaysAndClustersRepositoryError.toGetCredentialOfferFlowError(): GetCredentialOfferFlowError =
    when (this) {
        is SsiError.Unexpected -> CredentialOfferError.Unexpected(cause)
    }

internal fun MapToCredentialClaimDataError.toGetCredentialOfferFlowError(): GetCredentialOfferFlowError = when (this) {
    is SsiError.Unexpected -> CredentialOfferError.Unexpected(cause)
}

internal fun MapToCredentialDisplayDataError.toGetCredentialOfferFlowError(): GetCredentialOfferFlowError = when (this) {
    is CredentialError.Unexpected -> CredentialOfferError.Unexpected(cause)
}
