package ch.admin.foitt.wallet.platform.credential.domain.usecase.implementation

import android.annotation.SuppressLint
import ch.admin.foitt.openid4vc.domain.model.VerifiableCredentialParams
import ch.admin.foitt.openid4vc.domain.model.credentialoffer.CredentialOffer
import ch.admin.foitt.openid4vc.domain.model.credentialoffer.metadata.IssuerCredentialInfo
import ch.admin.foitt.openid4vc.domain.model.credentialoffer.metadata.RawAndParsedIssuerCredentialInfo
import ch.admin.foitt.openid4vc.domain.model.vcSdJwt.VcSchema
import ch.admin.foitt.openid4vc.domain.model.vcSdJwt.VcSdJwtCredential
import ch.admin.foitt.openid4vc.domain.usecase.FetchCredentialByConfig
import ch.admin.foitt.openid4vc.domain.usecase.FetchRawAndParsedIssuerCredentialInfo
import ch.admin.foitt.openid4vc.domain.usecase.GetVerifiableCredentialParams
import ch.admin.foitt.wallet.platform.credential.domain.model.AnyDisplays
import ch.admin.foitt.wallet.platform.credential.domain.model.CredentialError
import ch.admin.foitt.wallet.platform.credential.domain.usecase.FetchAndSaveCredential
import ch.admin.foitt.wallet.platform.credential.domain.usecase.GenerateAnyDisplays
import ch.admin.foitt.wallet.platform.credential.domain.usecase.SaveCredential
import ch.admin.foitt.wallet.platform.credential.domain.usecase.implementation.mock.MockFetchCredential.CREDENTIAL_ISSUER
import ch.admin.foitt.wallet.platform.credential.domain.usecase.implementation.mock.MockFetchCredential.credentialConfig
import ch.admin.foitt.wallet.platform.credential.domain.usecase.implementation.mock.MockFetchCredential.multipleConfigCredentialInformation
import ch.admin.foitt.wallet.platform.credential.domain.usecase.implementation.mock.MockFetchCredential.multipleIdentifiersCredentialOffer
import ch.admin.foitt.wallet.platform.credential.domain.usecase.implementation.mock.MockFetchCredential.noConfigCredentialInformation
import ch.admin.foitt.wallet.platform.credential.domain.usecase.implementation.mock.MockFetchCredential.noIdentifierCredentialOffer
import ch.admin.foitt.wallet.platform.credential.domain.usecase.implementation.mock.MockFetchCredential.noMatchingIdentifierCredentialOffer
import ch.admin.foitt.wallet.platform.credential.domain.usecase.implementation.mock.MockFetchCredential.oneConfigCredentialInformation
import ch.admin.foitt.wallet.platform.credential.domain.usecase.implementation.mock.MockFetchCredential.oneIdentifierCredentialOffer
import ch.admin.foitt.wallet.platform.credential.domain.usecase.implementation.mock.MockFetchCredential.proofTypeConfigHardwareBinding
import ch.admin.foitt.wallet.platform.credential.domain.usecase.implementation.mock.MockFetchCredential.proofTypeConfigSoftwareBinding
import ch.admin.foitt.wallet.platform.credential.domain.usecase.implementation.mock.MockFetchCredential.validHardwareKeyPair
import ch.admin.foitt.wallet.platform.credential.domain.usecase.implementation.mock.MockFetchCredential.validSoftwareKeyPair
import ch.admin.foitt.wallet.platform.credential.domain.usecase.implementation.mock.MockFetchCredential.verifiableCredentialParamsHardwareBinding
import ch.admin.foitt.wallet.platform.credential.domain.usecase.implementation.mock.MockFetchCredential.verifiableCredentialParamsNoBinding
import ch.admin.foitt.wallet.platform.credential.domain.usecase.implementation.mock.MockFetchCredential.verifiableCredentialParamsSoftwareBinding
import ch.admin.foitt.wallet.platform.holderBinding.domain.model.KeyPairError
import ch.admin.foitt.wallet.platform.holderBinding.domain.usecase.GenerateKeyPair
import ch.admin.foitt.wallet.platform.oca.domain.model.OcaBundle
import ch.admin.foitt.wallet.platform.oca.domain.model.OcaError
import ch.admin.foitt.wallet.platform.oca.domain.model.RawOcaBundle
import ch.admin.foitt.wallet.platform.oca.domain.model.VcMetadata
import ch.admin.foitt.wallet.platform.oca.domain.usecase.FetchVcMetadataByFormat
import ch.admin.foitt.wallet.platform.oca.domain.usecase.OcaBundler
import ch.admin.foitt.wallet.util.assertErrorType
import ch.admin.foitt.wallet.util.assertOk
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.unmockkAll
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json.Default.parseToJsonElement
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import ch.admin.foitt.openid4vc.domain.model.credentialoffer.CredentialOfferError as OpenIdCredentialOfferError

class FetchAndSaveCredentialImplTest {

    @MockK
    private lateinit var mockFetchRawAndParsedCredentialInfo: FetchRawAndParsedIssuerCredentialInfo

    @MockK
    private lateinit var mockGetVerifiableCredentialParams: GetVerifiableCredentialParams

    @MockK
    private lateinit var mockGenerateKeyPair: GenerateKeyPair

    @MockK
    private lateinit var mockFetchCredentialByConfig: FetchCredentialByConfig

    @MockK
    private lateinit var mockFetchVcMetadataByFormat: FetchVcMetadataByFormat

    @MockK
    private lateinit var mockOcaBundler: OcaBundler

    @MockK
    private lateinit var mockGenerateAnyDisplays: GenerateAnyDisplays

    @MockK
    private lateinit var mockSaveCredential: SaveCredential

    @MockK
    private lateinit var mockVcSdJwtCredential: VcSdJwtCredential

    private lateinit var useCase: FetchAndSaveCredential

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)

        useCase = FetchAndSaveCredentialImpl(
            fetchRawAndParsedIssuerCredentialInfo = mockFetchRawAndParsedCredentialInfo,
            getVerifiableCredentialParams = mockGetVerifiableCredentialParams,
            generateKeyPair = mockGenerateKeyPair,
            fetchCredentialByConfig = mockFetchCredentialByConfig,
            fetchVcMetadataByFormat = mockFetchVcMetadataByFormat,
            ocaBundler = mockOcaBundler,
            generateAnyDisplays = mockGenerateAnyDisplays,
            saveCredential = mockSaveCredential
        )

        setupDefaultMocks()
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @SuppressLint("CheckResult")
    @Test
    fun `Fetching and saving the credential runs specific things`() = runTest {
        setupDefaultMocks()

        val result = useCase(oneIdentifierCredentialOffer)

        val credentialId = result.assertOk()
        assertEquals(CREDENTIAL_ID, credentialId)

        coVerify {
            mockFetchRawAndParsedCredentialInfo(CREDENTIAL_ISSUER)
            mockGetVerifiableCredentialParams(credentialConfig, oneIdentifierCredentialOffer)
            mockGenerateKeyPair(proofTypeConfigHardwareBinding)
            mockFetchCredentialByConfig(
                verifiableCredentialParamsHardwareBinding,
                validHardwareKeyPair.keyPair,
                validHardwareKeyPair.attestationJwt
            )
            mockFetchVcMetadataByFormat(mockVcSdJwtCredential)
            mockOcaBundler(vcMetadata.rawOcaBundle!!.rawOcaBundle)
            mockGenerateAnyDisplays(mockVcSdJwtCredential, oneConfigCredentialInformation, credentialConfig, ocaBundle)
            mockSaveCredential(mockVcSdJwtCredential, anyDisplays, any())
        }
    }

    @Test
    fun `Fetching and saving credential for offer with one identifier and one matching config returns a valid id`() = runTest {
        setupDefaultMocks(
            credentialOffer = oneIdentifierCredentialOffer,
            credentialInfo = oneConfigCredentialInformation,
        )

        val result = useCase(oneIdentifierCredentialOffer)

        val credentialId = result.assertOk()
        assertEquals(CREDENTIAL_ID, credentialId)
    }

    @Test
    fun `Fetching and saving credential for offer with multiple identifiers and multiple matching configs returns a valid id for first identifier`() = runTest {
        setupDefaultMocks(
            credentialOffer = multipleIdentifiersCredentialOffer,
            credentialInfo = multipleConfigCredentialInformation,
        )

        val result = useCase(multipleIdentifiersCredentialOffer)

        val credentialId = result.assertOk()
        assertEquals(CREDENTIAL_ID, credentialId)
    }

    @Test
    fun `Fetching and saving credential for offer with multiple identifiers and one matching config returns a valid id`() = runTest {
        setupDefaultMocks(
            credentialOffer = multipleIdentifiersCredentialOffer,
            credentialInfo = oneConfigCredentialInformation,
        )

        val result = useCase(multipleIdentifiersCredentialOffer)

        val credentialId = result.assertOk()
        assertEquals(CREDENTIAL_ID, credentialId)
    }

    @Test
    fun `Fetching and saving credential for offer with one identifier and multiple matching configs returns a valid id`() = runTest {
        setupDefaultMocks(
            credentialOffer = oneIdentifierCredentialOffer,
            credentialInfo = multipleConfigCredentialInformation,
        )

        val result = useCase(oneIdentifierCredentialOffer)

        val credentialId = result.assertOk()
        assertEquals(CREDENTIAL_ID, credentialId)
    }

    @Test
    fun `Fetching and saving credential for offer with no matching identifier returns an error`() = runTest {
        setupDefaultMocks(
            credentialOffer = noMatchingIdentifierCredentialOffer,
            credentialInfo = multipleConfigCredentialInformation,
        )

        val result = useCase(noMatchingIdentifierCredentialOffer)

        result.assertErrorType(CredentialError.UnsupportedCredentialIdentifier::class)
    }

    @Test
    fun `Fetching and saving credential for offer with no identifier returns an error`() = runTest {
        setupDefaultMocks(
            credentialOffer = noIdentifierCredentialOffer,
            credentialInfo = multipleConfigCredentialInformation,
        )

        val result = useCase(noIdentifierCredentialOffer)

        result.assertErrorType(CredentialError.UnsupportedCredentialIdentifier::class)
    }

    @Test
    fun `Fetching and saving credential for information with no config returns an error`() = runTest {
        setupDefaultMocks(
            credentialOffer = multipleIdentifiersCredentialOffer,
            credentialInfo = noConfigCredentialInformation,
        )

        val result = useCase(multipleIdentifiersCredentialOffer)

        result.assertErrorType(CredentialError.UnsupportedCredentialIdentifier::class)
    }

    @Test
    fun `Fetching and saving credential maps errors from Fetching issuer credential information`() = runTest {
        val exception = IllegalStateException()
        coEvery {
            mockFetchRawAndParsedCredentialInfo(any())
        } returns Err(OpenIdCredentialOfferError.Unexpected(exception))

        val result = useCase(oneIdentifierCredentialOffer)

        val error = result.assertErrorType(CredentialError.Unexpected::class)
        assertEquals(exception, error.cause)
    }

    @Test
    fun `Fetching and saving credential maps errors from fetching verifiable credential params`() = runTest {
        coEvery {
            mockGetVerifiableCredentialParams(any(), any())
        } returns Err(OpenIdCredentialOfferError.UnsupportedProofType)

        useCase(oneIdentifierCredentialOffer).assertErrorType(CredentialError.UnsupportedProofType::class)
    }

    @Test
    fun `Fetching and saving credential generates a key pair for hardware bound credentials`() = runTest {
        setupDefaultMocks()

        useCase(oneIdentifierCredentialOffer).assertOk()

        coVerify(exactly = 1) {
            mockGenerateKeyPair(proofTypeConfigHardwareBinding)
        }
    }

    @Test
    fun `Fetching and saving credential generates a key pair for software bound credentials`() = runTest {
        setupDefaultMocks(verifiableCredentialParams = verifiableCredentialParamsSoftwareBinding)

        useCase(oneIdentifierCredentialOffer).assertOk()

        coVerify(exactly = 1) {
            mockGenerateKeyPair(proofTypeConfigSoftwareBinding)
        }
    }

    @Test
    fun `Fetching and saving credential does not generate a key pair for credentials without binding`() = runTest {
        setupDefaultMocks(verifiableCredentialParams = verifiableCredentialParamsNoBinding)

        val result = useCase(oneIdentifierCredentialOffer)

        result.assertOk()

        coVerify(exactly = 0) {
            mockGenerateKeyPair(any())
        }
    }

    @Test
    fun `Fetching and saving credential maps errors from generating the key pair`() = runTest {
        coEvery {
            mockGenerateKeyPair(any())
        } returns Err(KeyPairError.IncompatibleDeviceKeyStorage)

        useCase(oneIdentifierCredentialOffer).assertErrorType(CredentialError.IncompatibleDeviceKeyStorage::class)
    }

    @Test
    fun `Fetching and saving credential maps errors from Fetching and saving credential by config`() = runTest {
        val exception = IllegalStateException()
        coEvery {
            mockFetchCredentialByConfig(any(), any(), any())
        } returns Err(OpenIdCredentialOfferError.Unexpected(exception))

        val result = useCase(oneIdentifierCredentialOffer)

        val error = result.assertErrorType(CredentialError.Unexpected::class)
        assertEquals(exception, error.cause)
    }

    @Test
    fun `Fetching and saving credential maps errors from fetching the vc metadata`() = runTest {
        coEvery { mockFetchVcMetadataByFormat(mockVcSdJwtCredential) } returns Err(OcaError.InvalidOca)

        useCase(oneIdentifierCredentialOffer).assertErrorType(CredentialError.InvalidCredentialOffer::class)
    }

    @Test
    fun `Fetching and saving credential maps errors from credential displays generator`() = runTest {
        val exception = IllegalStateException()
        coEvery {
            mockGenerateAnyDisplays(any(), any(), any(), any())
        } returns Err(CredentialError.Unexpected(exception))

        useCase(oneIdentifierCredentialOffer).assertErrorType(CredentialError.Unexpected::class)
    }

    @Test
    fun `Fetching and saving credential maps errors from saving credential`() = runTest {
        val exception = IllegalStateException()
        coEvery {
            mockSaveCredential(any(), any(), any())
        } returns Err(CredentialError.Unexpected(exception))

        val result = useCase(oneIdentifierCredentialOffer)

        val error = result.assertErrorType(CredentialError.Unexpected::class)
        assertEquals(exception, error.cause)
    }

    private fun setupDefaultMocks(
        credentialOffer: CredentialOffer = oneIdentifierCredentialOffer,
        credentialInfo: IssuerCredentialInfo = oneConfigCredentialInformation,
        verifiableCredentialParams: VerifiableCredentialParams = verifiableCredentialParamsHardwareBinding,
    ) {
        every {
            mockVcSdJwtCredential.getClaimsForPresentation()
        } returns parseToJsonElement(CREDENTIAL_CLAIMS_FOR_PRESENTATION)

        coEvery { mockFetchRawAndParsedCredentialInfo(CREDENTIAL_ISSUER) } returns
            Ok(RawAndParsedIssuerCredentialInfo(issuerCredentialInfo = credentialInfo, rawIssuerCredentialInfo = ""))

        coEvery {
            mockGetVerifiableCredentialParams(credentialConfig, credentialOffer)
        } returns Ok(verifiableCredentialParams)

        coEvery { mockGenerateKeyPair(proofTypeConfigHardwareBinding) } returns Ok(validHardwareKeyPair)
        coEvery { mockGenerateKeyPair(proofTypeConfigSoftwareBinding) } returns Ok(validSoftwareKeyPair)

        coEvery {
            mockFetchCredentialByConfig(any(), any(), any())
        } returns Ok(mockVcSdJwtCredential)

        coEvery { mockFetchVcMetadataByFormat(mockVcSdJwtCredential) } returns Ok(vcMetadata)

        coEvery { mockOcaBundler(any()) } returns Ok(ocaBundle)

        coEvery {
            mockGenerateAnyDisplays(mockVcSdJwtCredential, credentialInfo, credentialConfig, ocaBundle)
        } returns Ok(anyDisplays)

        coEvery {
            mockSaveCredential(
                anyCredential = mockVcSdJwtCredential,
                anyDisplays = anyDisplays,
                rawCredentialData = any()
            )
        } returns Ok(CREDENTIAL_ID)
    }

    private companion object {
        const val CREDENTIAL_ID = 111L
        val CREDENTIAL_CLAIMS_FOR_PRESENTATION = """
            {
                "key":"value"
            }
        """.trimIndent()

        const val VC_SCHEMA = "schema"
        const val RAW_OCA_BUNDLE = "oca bundle"

        val vcMetadata = VcMetadata(vcSchema = VcSchema(VC_SCHEMA), rawOcaBundle = RawOcaBundle(RAW_OCA_BUNDLE))
        val ocaBundle = OcaBundle(emptyList(), emptyList())
        val anyDisplays = AnyDisplays(emptyList(), emptyList(), emptyList())
    }
}
