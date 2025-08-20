package ch.admin.foitt.wallet.platform.credential.domain.usecase.implementation.mock

import ch.admin.foitt.openid4vc.domain.model.KeyStorageSecurityLevel
import ch.admin.foitt.openid4vc.domain.model.SigningAlgorithm
import ch.admin.foitt.openid4vc.domain.model.VerifiableCredentialParams
import ch.admin.foitt.openid4vc.domain.model.credentialoffer.CredentialOffer
import ch.admin.foitt.openid4vc.domain.model.credentialoffer.JWSKeyPair
import ch.admin.foitt.openid4vc.domain.model.credentialoffer.metadata.IssuerCredentialInfo
import ch.admin.foitt.openid4vc.domain.model.credentialoffer.metadata.KeyAttestationConfig
import ch.admin.foitt.openid4vc.domain.model.credentialoffer.metadata.ProofTypeConfig
import ch.admin.foitt.openid4vc.domain.model.credentialoffer.metadata.RawAndParsedIssuerCredentialInfo
import ch.admin.foitt.openid4vc.domain.model.credentialoffer.metadata.VcSdJwtCredentialConfiguration
import ch.admin.foitt.openid4vc.domain.model.jwt.Jwt
import ch.admin.foitt.wallet.platform.holderBinding.domain.model.BindingKeyPair
import io.mockk.every
import io.mockk.mockk

internal object MockFetchCredential {
    const val CREDENTIAL_IDENTIFIER = "credentialIdentifier"
    const val CREDENTIAL_IDENTIFIER_2 = "credentialIdentifier2"
    const val CREDENTIAL_ISSUER = "credentialIssuer"
    const val CREDENTIAL_ENDPOINT = "credentialEndpoint"
    private val SIGNING_ALG = SigningAlgorithm.ES256
    private val PROOF_SIGNING_ALG_VALUES_SUPPORTED = listOf(SIGNING_ALG)
    val strongboxKeyStorage = listOf(KeyStorageSecurityLevel.HIGH)

    val proofTypeConfigSoftwareBinding = ProofTypeConfig(PROOF_SIGNING_ALG_VALUES_SUPPORTED)
    val proofTypeConfigHardwareBinding = ProofTypeConfig(
        proofSigningAlgValuesSupported = PROOF_SIGNING_ALG_VALUES_SUPPORTED,
        keyAttestationsRequired = KeyAttestationConfig(
            keyStorage = strongboxKeyStorage,
            userAuthentication = listOf(KeyStorageSecurityLevel.HIGH)
        )
    )

    val validSoftwareKeyPair = BindingKeyPair(
        keyPair = mockk<JWSKeyPair>(),
        attestationJwt = null,
    )

    val validHardwareKeyPair = BindingKeyPair(
        keyPair = mockk<JWSKeyPair>(),
        attestationJwt = mockk<Jwt>(),
    )

    val credentialConfig = mockk<VcSdJwtCredentialConfiguration> {
        every { identifier } returns CREDENTIAL_IDENTIFIER
    }
    private val credentialConfig2 = mockk<VcSdJwtCredentialConfiguration> {
        every { identifier } returns CREDENTIAL_IDENTIFIER_2
    }

    val oneConfigCredentialInformation = mockk<IssuerCredentialInfo> {
        every { credentialEndpoint } returns CREDENTIAL_ENDPOINT
        every { credentialConfigurations } returns listOf(credentialConfig)
    }

    val oneRawAndParsedCredentialInformation = mockk<RawAndParsedIssuerCredentialInfo> {
        every { issuerCredentialInfo } returns oneConfigCredentialInformation
        every { rawIssuerCredentialInfo } returns ""
    }

    val multipleConfigCredentialInformation = mockk<IssuerCredentialInfo> {
        every { credentialEndpoint } returns CREDENTIAL_ENDPOINT
        every { credentialConfigurations } returns listOf(credentialConfig, credentialConfig2)
    }

    val noConfigCredentialInformation = mockk<IssuerCredentialInfo> {
        every { credentialEndpoint } returns CREDENTIAL_ENDPOINT
        every { credentialConfigurations } returns emptyList()
    }

    val oneIdentifierCredentialOffer = mockk<CredentialOffer> {
        every { credentialIssuer } returns CREDENTIAL_ISSUER
        every { credentialConfigurationIds } returns listOf(CREDENTIAL_IDENTIFIER)
    }

    val multipleIdentifiersCredentialOffer = mockk<CredentialOffer> {
        every { credentialIssuer } returns CREDENTIAL_ISSUER
        every { credentialConfigurationIds } returns listOf(CREDENTIAL_IDENTIFIER, CREDENTIAL_IDENTIFIER_2)
    }

    val noMatchingIdentifierCredentialOffer = mockk<CredentialOffer> {
        every { credentialIssuer } returns CREDENTIAL_ISSUER
        every { credentialConfigurationIds } returns listOf("otherCredentialIdentifier")
    }

    val noIdentifierCredentialOffer = mockk<CredentialOffer> {
        every { credentialIssuer } returns CREDENTIAL_ISSUER
        every { credentialConfigurationIds } returns emptyList()
    }

    val verifiableCredentialParamsHardwareBinding = mockk<VerifiableCredentialParams> {
        every { proofTypeConfig } returns proofTypeConfigHardwareBinding
    }

    val verifiableCredentialParamsSoftwareBinding = mockk<VerifiableCredentialParams> {
        every { proofTypeConfig } returns proofTypeConfigSoftwareBinding
    }

    val verifiableCredentialParamsNoBinding = mockk<VerifiableCredentialParams> {
        every { proofTypeConfig } returns null
    }
}
