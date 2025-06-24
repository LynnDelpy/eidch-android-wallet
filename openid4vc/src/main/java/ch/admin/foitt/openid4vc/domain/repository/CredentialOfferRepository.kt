package ch.admin.foitt.openid4vc.domain.repository

import androidx.annotation.CheckResult
import ch.admin.foitt.openid4vc.domain.model.credentialoffer.CredentialRequestProof
import ch.admin.foitt.openid4vc.domain.model.credentialoffer.CredentialResponse
import ch.admin.foitt.openid4vc.domain.model.credentialoffer.FetchIssuerConfigurationError
import ch.admin.foitt.openid4vc.domain.model.credentialoffer.FetchIssuerCredentialInfoError
import ch.admin.foitt.openid4vc.domain.model.credentialoffer.FetchVerifiableCredentialError
import ch.admin.foitt.openid4vc.domain.model.credentialoffer.TokenResponse
import ch.admin.foitt.openid4vc.domain.model.credentialoffer.metadata.AnyCredentialConfiguration
import ch.admin.foitt.openid4vc.domain.model.credentialoffer.metadata.IssuerConfiguration
import ch.admin.foitt.openid4vc.domain.model.credentialoffer.metadata.IssuerCredentialInfo
import ch.admin.foitt.openid4vc.domain.model.credentialoffer.metadata.RawAndParsedIssuerCredentialInfo
import com.github.michaelbull.result.Result

interface CredentialOfferRepository {

    @CheckResult
    suspend fun fetchRawAndParsedIssuerCredentialInformation(
        issuerEndpoint: String
    ): Result<RawAndParsedIssuerCredentialInfo, FetchIssuerCredentialInfoError>

    @CheckResult
    suspend fun getIssuerCredentialInfo(
        issuerEndpoint: String
    ): Result<IssuerCredentialInfo, FetchIssuerCredentialInfoError>

    @CheckResult
    suspend fun fetchIssuerConfiguration(
        issuerEndpoint: String
    ): Result<IssuerConfiguration, FetchIssuerConfigurationError>

    @CheckResult
    suspend fun fetchAccessToken(
        tokenEndpoint: String,
        preAuthorizedCode: String
    ): Result<TokenResponse, FetchVerifiableCredentialError>

    @CheckResult
    suspend fun fetchCredential(
        issuerEndpoint: String,
        credentialConfiguration: AnyCredentialConfiguration,
        proof: CredentialRequestProof?,
        accessToken: String
    ): Result<CredentialResponse, FetchVerifiableCredentialError>
}
