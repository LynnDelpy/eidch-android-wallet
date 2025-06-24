package ch.admin.foitt.wallet.platform.credential.domain.usecase.implementation

import ch.admin.foitt.openid4vc.domain.model.anycredential.AnyCredential
import ch.admin.foitt.openid4vc.domain.model.credentialoffer.metadata.IssuerCredentialInfo
import ch.admin.foitt.openid4vc.domain.model.credentialoffer.metadata.OidIssuerDisplay
import ch.admin.foitt.openid4vc.domain.model.credentialoffer.metadata.VcSdJwtCredentialConfiguration
import ch.admin.foitt.wallet.platform.credential.domain.model.AnyCredentialDisplay
import ch.admin.foitt.wallet.platform.credential.domain.model.AnyIssuerDisplay
import ch.admin.foitt.wallet.platform.credential.domain.model.CredentialError
import ch.admin.foitt.wallet.platform.credential.domain.model.MetadataDisplays
import ch.admin.foitt.wallet.platform.credential.domain.usecase.GenerateAnyDisplays
import ch.admin.foitt.wallet.platform.credential.domain.usecase.GenerateMetadataDisplays
import ch.admin.foitt.wallet.platform.database.domain.model.Cluster
import ch.admin.foitt.wallet.platform.database.domain.model.DisplayConst
import ch.admin.foitt.wallet.platform.database.domain.model.DisplayLanguage
import ch.admin.foitt.wallet.platform.oca.domain.model.OcaBundle
import ch.admin.foitt.wallet.platform.oca.domain.model.OcaDisplays
import ch.admin.foitt.wallet.platform.oca.domain.model.OcaError
import ch.admin.foitt.wallet.platform.oca.domain.usecase.GenerateOcaDisplays
import ch.admin.foitt.wallet.util.SafeJsonTestInstance.safeJson
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
import kotlinx.serialization.json.JsonObject
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class GenerateAnyDisplaysImplTest {

    @MockK
    private lateinit var mockGenerateOcaDisplays: GenerateOcaDisplays

    @MockK
    private lateinit var mockGenerateMetadataDisplays: GenerateMetadataDisplays

    @MockK
    private lateinit var mockAnyCredential: AnyCredential

    @MockK
    private lateinit var mockIssuerInfo: IssuerCredentialInfo

    @MockK
    private lateinit var mockMetadata: VcSdJwtCredentialConfiguration

    @MockK
    private lateinit var mockOcaBundle: OcaBundle

    private val json = safeJson

    private lateinit var useCase: GenerateAnyDisplays

    @BeforeEach
    fun setup() {
        MockKAnnotations.init(this)

        useCase = GenerateAnyDisplaysImpl(
            generateOcaDisplays = mockGenerateOcaDisplays,
            generateMetadataDisplays = mockGenerateMetadataDisplays,
        )

        setupDefaultMocks()
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `Generating valid credential displays returns success`() = runTest {
        val result = useCase(mockAnyCredential, mockIssuerInfo, mockMetadata, mockOcaBundle).assertOk()

        val expectedIssuerDisplays = listOf(
            AnyIssuerDisplay(locale = "en", name = "firstname"),
            AnyIssuerDisplay(locale = DisplayLanguage.FALLBACK, DisplayConst.ISSUER_FALLBACK_NAME)
        )

        val expectedCredentialDisplays = emptyList<AnyCredentialDisplay>()
        val expectedClusters = emptyList<Cluster>()

        assertEquals(expectedIssuerDisplays, result.issuerDisplays)
        assertEquals(expectedCredentialDisplays, result.credentialDisplays)
        assertEquals(expectedClusters, result.clusters)
    }

    @Test
    fun `Generating credential displays adds fallback language if empty`() = runTest {
        every { mockIssuerInfo.display } returns null

        val result = useCase(mockAnyCredential, mockIssuerInfo, mockMetadata, mockOcaBundle).assertOk()

        val expectedIssuerDisplays = listOf(
            AnyIssuerDisplay(locale = DisplayLanguage.FALLBACK, DisplayConst.ISSUER_FALLBACK_NAME)
        )

        assertEquals(expectedIssuerDisplays, result.issuerDisplays)
    }

    @Test
    fun `Generating credential displays adds fallback language if not contained`() = runTest {
        val result = useCase(mockAnyCredential, mockIssuerInfo, mockMetadata, mockOcaBundle).assertOk()

        val expectedIssuerDisplays = listOf(
            AnyIssuerDisplay(locale = "en", name = "firstname"),
            AnyIssuerDisplay(locale = DisplayLanguage.FALLBACK, DisplayConst.ISSUER_FALLBACK_NAME)
        )

        assertEquals(expectedIssuerDisplays, result.issuerDisplays)
    }

    @Test
    fun `Generating credential displays maps errors from getting the credential claims`() = runTest {
        every { mockAnyCredential.getClaimsToSave() } throws IllegalStateException()

        useCase(mockAnyCredential, mockIssuerInfo, mockMetadata, mockOcaBundle).assertErrorType(CredentialError.Unexpected::class)
    }

    @Test
    fun `Generating credential displays gets the oca displays if oca bundle is provided`() = runTest {
        useCase(mockAnyCredential, mockIssuerInfo, mockMetadata, mockOcaBundle).assertOk()

        coVerify(exactly = 1) {
            mockGenerateOcaDisplays(any(), any())
        }
        coVerify(exactly = 0) {
            mockGenerateMetadataDisplays(any(), any())
        }
    }

    @Test
    fun `Generating credential displays gets the metadata displays if the oca bundle is not provided`() = runTest {
        useCase(mockAnyCredential, mockIssuerInfo, mockMetadata, null).assertOk()

        coVerify(exactly = 1) {
            mockGenerateMetadataDisplays(any(), any())
        }
        coVerify(exactly = 0) {
            mockGenerateOcaDisplays(any(), any())
        }
    }

    @Test
    fun `Generating credential displays maps errors from generating the oca displays`() = runTest {
        val exception = IllegalStateException()
        coEvery { mockGenerateOcaDisplays(any(), mockOcaBundle) } returns Err(OcaError.Unexpected(exception))

        useCase(mockAnyCredential, mockIssuerInfo, mockMetadata, mockOcaBundle).assertErrorType(CredentialError.Unexpected::class)
    }

    @Test
    fun `Generating credential displays maps errors from generating the metadata displays`() = runTest {
        val exception = IllegalStateException()
        coEvery { mockGenerateMetadataDisplays(any(), mockMetadata) } returns Err(CredentialError.Unexpected(exception))

        useCase(mockAnyCredential, mockIssuerInfo, mockMetadata, null).assertErrorType(CredentialError.Unexpected::class)
    }

    private fun setupDefaultMocks() {
        every { mockAnyCredential.claimsPath } returns "$"
        every { mockAnyCredential.getClaimsToSave() } returns json.safeDecodeStringTo<JsonObject>(DISCLOSABLE_CLAIMS_JSON).value

        every { mockIssuerInfo.display } returns listOf(issuerDisplay)

        coEvery { mockGenerateOcaDisplays(any(), mockOcaBundle) } returns Ok(ocaDisplays)
        coEvery { mockGenerateMetadataDisplays(any(), mockMetadata) } returns Ok(metadataDisplays)
    }

    private companion object {
        val issuerDisplay = OidIssuerDisplay(locale = "en", name = "firstname")

        const val DISCLOSABLE_CLAIMS_JSON = """
        {
            "claim":"value"
        }
        """

        val ocaDisplays = OcaDisplays(credentialDisplays = emptyList(), clusters = emptyList())
        val metadataDisplays = MetadataDisplays(credentialDisplays = emptyList(), clusters = emptyList())
    }
}
