package ch.admin.foitt.openid4vc.domain.usecase.implementation.mock

import ch.admin.foitt.openid4vc.domain.model.SigningAlgorithm
import ch.admin.foitt.openid4vc.domain.model.VerifiableCredential
import ch.admin.foitt.openid4vc.domain.model.VerifiableCredentialParams
import ch.admin.foitt.openid4vc.domain.model.credentialoffer.CredentialOffer
import ch.admin.foitt.openid4vc.domain.model.credentialoffer.CredentialRequestProofJwt
import ch.admin.foitt.openid4vc.domain.model.credentialoffer.CredentialResponse
import ch.admin.foitt.openid4vc.domain.model.credentialoffer.Grant
import ch.admin.foitt.openid4vc.domain.model.credentialoffer.PreAuthorizedContent
import ch.admin.foitt.openid4vc.domain.model.credentialoffer.TokenResponse
import ch.admin.foitt.openid4vc.domain.model.credentialoffer.metadata.CredentialFormat
import ch.admin.foitt.openid4vc.domain.model.credentialoffer.metadata.IssuerConfiguration
import ch.admin.foitt.openid4vc.domain.model.credentialoffer.metadata.IssuerCredentialInfo
import ch.admin.foitt.openid4vc.domain.model.keyBinding.KeyBinding
import ch.admin.foitt.openid4vc.domain.model.keyBinding.KeyBindingType
import ch.admin.foitt.openid4vc.domain.usecase.implementation.mock.MockIssuerCredentialConfiguration.proofTypeConfigHardwareBinding
import ch.admin.foitt.openid4vc.domain.usecase.implementation.mock.MockIssuerCredentialConfiguration.proofTypeConfigSoftwareBinding
import ch.admin.foitt.openid4vc.domain.usecase.implementation.mock.MockIssuerCredentialConfiguration.vcSdJwtCredentialConfiguration

internal object MockCredentialOffer {
    const val CREDENTIAL_ISSUER = "credentialIssuer"
    const val CREDENTIAL_IDENTIFIER = "credentialIdentifier"
    private val CREDENTIALS = listOf(CREDENTIAL_IDENTIFIER)
    private const val PRE_AUTHORIZED_CODE = "preAuthorizedCode"
    val offerWithPreAuthorizedCode = CredentialOffer(
        credentialIssuer = CREDENTIAL_ISSUER,
        credentialConfigurationIds = CREDENTIALS,
        grants = Grant(PreAuthorizedContent(PRE_AUTHORIZED_CODE))
    )
    val offerWithoutPreAuthorizedCode = offerWithPreAuthorizedCode.copy(grants = Grant())
    val offerWithoutMatchingCredentialIdentifier =
        offerWithPreAuthorizedCode.copy(credentialConfigurationIds = listOf("otherCredentialIdentifier"))

    const val KEY_ATTESTATION_JWT = "keyAttestationJwt"
    private const val ACCESS_TOKEN = "accessToken"
    const val C_NONCE = "cNonce"
    private const val C_NONCE_EXPIRES_IN = 1
    private const val EXPIRES_IN = 2
    private const val TOKEN_TYPE = "tokenType"
    val validTokenResponse = TokenResponse(
        accessToken = ACCESS_TOKEN,
        cNonce = C_NONCE,
        cNonceExpiresIn = C_NONCE_EXPIRES_IN,
        expiresIn = EXPIRES_IN,
        tokenType = TOKEN_TYPE
    )

    private const val TOKEN_ENDPOINT = "tokenEndpoint"
    val validIssuerConfig = IssuerConfiguration(
        issuer = CREDENTIAL_ISSUER,
        tokenEndpoint = TOKEN_ENDPOINT
    )

    private const val CREDENTIAL_ENDPOINT = "credentialEndpoint"
    val validIssuerCredentialInfo = IssuerCredentialInfo(
        credentialEndpoint = CREDENTIAL_ENDPOINT,
        credentialIssuer = CREDENTIAL_ISSUER,
        credentialConfigurations = listOf(vcSdJwtCredentialConfiguration),
        credentialResponseEncryption = null,
        display = listOf(),
    )

    const val KEY_ID = "keyId"
    private const val PROOF_JWT = "proofJwt"
    val jwtProof = CredentialRequestProofJwt(PROOF_JWT)

    private val ALGORITHM = SigningAlgorithm.ES512

    private const val CREDENTIAL = "credential"
    private const val TRANSACTION_ID = "transaction_id"
    private const val NOTIFICATION_ID = "notification_id"
    val validCredentialResponse = CredentialResponse(
        credential = CREDENTIAL,
        transactionId = TRANSACTION_ID,
        cNonce = C_NONCE,
        cNonceExpiresIn = C_NONCE_EXPIRES_IN,
        notificationId = NOTIFICATION_ID,
    )

    private val keyBinding = KeyBinding(
        identifier = KEY_ID,
        algorithm = ALGORITHM,
        bindingType = KeyBindingType.SOFTWARE,
    )

    val validVerifiableCredential = VerifiableCredential(
        format = CredentialFormat.VC_SD_JWT,
        credential = CREDENTIAL,
        keyBinding = keyBinding
    )

    val verifiableCredentialParamsSoftwareBinding = VerifiableCredentialParams(
        proofTypeConfig = proofTypeConfigSoftwareBinding,
        tokenEndpoint = validIssuerConfig.tokenEndpoint,
        grants = offerWithPreAuthorizedCode.grants,
        issuerEndpoint = offerWithPreAuthorizedCode.credentialIssuer,
        credentialEndpoint = validIssuerCredentialInfo.credentialEndpoint,
        credentialConfiguration = vcSdJwtCredentialConfiguration,
    )

    val verifiableCredentialParamsHardwareBinding = VerifiableCredentialParams(
        proofTypeConfig = proofTypeConfigHardwareBinding,
        tokenEndpoint = validIssuerConfig.tokenEndpoint,
        grants = offerWithPreAuthorizedCode.grants,
        issuerEndpoint = offerWithPreAuthorizedCode.credentialIssuer,
        credentialEndpoint = validIssuerCredentialInfo.credentialEndpoint,
        credentialConfiguration = vcSdJwtCredentialConfiguration,
    )

    val verifiableCredentialParamsWithoutBinding = VerifiableCredentialParams(
        proofTypeConfig = null,
        tokenEndpoint = validIssuerConfig.tokenEndpoint,
        grants = offerWithPreAuthorizedCode.grants,
        issuerEndpoint = offerWithPreAuthorizedCode.credentialIssuer,
        credentialEndpoint = validIssuerCredentialInfo.credentialEndpoint,
        credentialConfiguration = vcSdJwtCredentialConfiguration,
    )
}
