package ch.admin.foitt.openid4vc.domain.usecase.implementation

import ch.admin.foitt.openid4vc.domain.repository.CredentialOfferRepository
import ch.admin.foitt.openid4vc.domain.usecase.FetchRawAndParsedIssuerCredentialInfo
import javax.inject.Inject

internal class FetchRawAndParsedIssuerCredentialInfoImpl @Inject constructor(
    private val credentialOfferRepository: CredentialOfferRepository
) : FetchRawAndParsedIssuerCredentialInfo {
    override suspend fun invoke(issuerEndpoint: String) =
        credentialOfferRepository.fetchRawAndParsedIssuerCredentialInformation(issuerEndpoint)
}
