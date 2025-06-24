package ch.admin.foitt.wallet.platform.credential.domain.usecase.implementation

import ch.admin.foitt.openid4vc.domain.model.credentialoffer.metadata.OidCredentialDisplay
import ch.admin.foitt.openid4vc.domain.model.credentialoffer.metadata.VcSdJwtCredentialConfiguration
import ch.admin.foitt.wallet.platform.credential.domain.model.AnyClaimDisplay
import ch.admin.foitt.wallet.platform.credential.domain.model.AnyCredentialDisplay
import ch.admin.foitt.wallet.platform.credential.domain.model.CredentialError
import ch.admin.foitt.wallet.platform.credential.domain.usecase.GenerateMetadataDisplays
import ch.admin.foitt.wallet.platform.database.domain.model.CredentialClaim
import ch.admin.foitt.wallet.platform.database.domain.model.DisplayLanguage
import ch.admin.foitt.wallet.util.SafeJsonTestInstance.safeJson
import ch.admin.foitt.wallet.util.assertErrorType
import ch.admin.foitt.wallet.util.assertOk
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.unmockkAll
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class GenerateMetadataDisplaysImplTest {

    @MockK
    private lateinit var mockMetadata: VcSdJwtCredentialConfiguration

    private val json = safeJson

    private lateinit var useCase: GenerateMetadataDisplays

    @BeforeEach
    fun setup() {
        MockKAnnotations.init(this)

        useCase = GenerateMetadataDisplaysImpl(
            safeJson = json
        )

        setupDefaultMocks()
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `Generating valid metadata displays returns success`() = runTest {
        val result = useCase(credentialClaims, mockMetadata).assertOk()

        val expectedCredentialDisplays = listOf(
            AnyCredentialDisplay(locale = LANGUAGE_EN, name = "credential"),
            AnyCredentialDisplay(locale = DisplayLanguage.FALLBACK, name = IDENTIFIER)
        )

        val expectedClaim = CredentialClaim(
            clusterId = -1,
            key = CLAIM_KEY,
            value = CLAIM_VALUE,
            valueType = "string",
            order = 0,
        )
        val expectedClaimDisplays = listOf(
            AnyClaimDisplay(locale = LANGUAGE_EN, name = CLAIM_VALUE_EN),
            AnyClaimDisplay(locale = DisplayLanguage.FALLBACK, name = CLAIM_KEY)
        )
        val expectedClaims = mapOf(expectedClaim to expectedClaimDisplays)

        assertEquals(expectedCredentialDisplays, result.credentialDisplays)
        assertEquals(1, result.clusters.size)
        assertEquals(expectedClaims, result.clusters.first().claims)
    }

    @Test
    fun `Generating metadata displays adds fallback language if empty`() = runTest {
        every { mockMetadata.claims } returns METADATA_CLAIMS_NO_DISPLAYS

        val result = useCase(credentialClaims, mockMetadata).assertOk()

        val expectedCredentialDisplays = listOf(
            AnyCredentialDisplay(locale = LANGUAGE_EN, name = "credential"),
            AnyCredentialDisplay(locale = DisplayLanguage.FALLBACK, name = IDENTIFIER)
        )

        val expectedClaim = CredentialClaim(
            clusterId = -1,
            key = CLAIM_KEY,
            value = CLAIM_VALUE,
            valueType = "string",
            order = 0,
        )
        val expectedClaimDisplays = listOf(
            AnyClaimDisplay(locale = DisplayLanguage.FALLBACK, name = CLAIM_KEY)
        )
        val expectedClaims = mapOf(expectedClaim to expectedClaimDisplays)

        assertEquals(expectedCredentialDisplays, result.credentialDisplays)
        assertEquals(1, result.clusters.size)
        assertEquals(expectedClaims, result.clusters.first().claims)
    }

    @Test
    fun `Generating metadata displays adds fallback language if not contained`() = runTest {
        val result = useCase(credentialClaims, mockMetadata).assertOk()

        val expectedCredentialDisplays = listOf(
            AnyCredentialDisplay(locale = LANGUAGE_EN, name = "credential"),
            AnyCredentialDisplay(locale = DisplayLanguage.FALLBACK, name = IDENTIFIER)
        )

        val expectedClaim = CredentialClaim(
            clusterId = -1,
            key = CLAIM_KEY,
            value = CLAIM_VALUE,
            valueType = "string",
            order = 0,
        )
        val expectedClaimDisplays = listOf(
            AnyClaimDisplay(locale = LANGUAGE_EN, name = CLAIM_VALUE_EN),
            AnyClaimDisplay(locale = DisplayLanguage.FALLBACK, name = CLAIM_KEY)
        )
        val expectedClaims = mapOf(expectedClaim to expectedClaimDisplays)

        assertEquals(expectedCredentialDisplays, result.credentialDisplays)
        assertEquals(1, result.clusters.size)
        assertEquals(expectedClaims, result.clusters.first().claims)
    }

    @Test
    fun `Generating metadata displays returns error if metadata does not contain claims`() = runTest {
        every { mockMetadata.claims } returns null

        useCase(credentialClaims, mockMetadata).assertErrorType(CredentialError.InvalidGenerateMetadataClaims::class)
    }

    @Test
    fun `Generating metadata displays maps error from parsing metadata claims`() = runTest {
        every { mockMetadata.claims } returns "invalid metadata claims json"

        useCase(credentialClaims, mockMetadata).assertErrorType(CredentialError.Unexpected::class)
    }

    private fun setupDefaultMocks() {
        every { mockMetadata.identifier } returns IDENTIFIER
        every { mockMetadata.order } returns orderList
        every { mockMetadata.display } returns credentialDisplays
        every { mockMetadata.claims } returns METADATA_CLAIMS
    }

    private companion object {
        const val IDENTIFIER = "identifier"
        const val CLAIM_KEY = "claim_key"
        const val CLAIM_VALUE = "claim_value"
        const val LANGUAGE_EN = "en"
        const val CLAIM_VALUE_EN = "claim value en"
        val orderList = listOf(CLAIM_KEY)

        val credentialDisplay = OidCredentialDisplay(name = "credential", locale = LANGUAGE_EN)
        val fallbackCredentialDisplay = OidCredentialDisplay(name = IDENTIFIER, locale = DisplayLanguage.FALLBACK)
        val credentialDisplays = listOf(credentialDisplay, fallbackCredentialDisplay)

        val credentialClaims = mapOf(CLAIM_KEY to CLAIM_VALUE)

        const val METADATA_CLAIMS = """
        {
           "$CLAIM_KEY":{
              "mandatory":true,
              "value_type":"string",
              "display":[
                 {
                    "locale":"$LANGUAGE_EN",
                    "name":"$CLAIM_VALUE_EN"
                 }
              ]
           }
        }
        """

        const val METADATA_CLAIMS_NO_DISPLAYS = """
        {
           "$CLAIM_KEY":{
              "mandatory":true,
              "value_type":"string"
           }
        }
        """
    }
}
