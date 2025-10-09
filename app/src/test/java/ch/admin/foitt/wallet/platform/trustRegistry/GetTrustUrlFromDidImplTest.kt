package ch.admin.foitt.wallet.platform.trustRegistry

import ch.admin.foitt.wallet.platform.environmentSetup.domain.repository.EnvironmentSetupRepository
import ch.admin.foitt.wallet.platform.trustRegistry.domain.model.GetTrustUrlFromDidError
import ch.admin.foitt.wallet.platform.trustRegistry.domain.model.TrustStatementType
import ch.admin.foitt.wallet.platform.trustRegistry.domain.usecase.implementation.GetTrustUrlFromDidImpl
import ch.admin.foitt.wallet.util.assertErrorType
import ch.admin.foitt.wallet.util.assertOk
import io.ktor.utils.io.charsets.name
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import io.mockk.unmockkAll
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.net.URLEncoder

class GetTrustUrlFromDidImplTest {

    @MockK
    private lateinit var mockEnvironmentSetup: EnvironmentSetupRepository

    private lateinit var useCase: GetTrustUrlFromDidImpl

    @BeforeEach
    fun setup() {
        MockKAnnotations.init(this)
        useCase = GetTrustUrlFromDidImpl(mockEnvironmentSetup)

        coEvery { mockEnvironmentSetup.trustRegistryMapping } returns trustRegistryMappings
        coEvery { mockEnvironmentSetup.baseTrustDomainRegex } returns Regex(
            "^did:tdw:[^:]+:([^:]+\\.swiyu(-int)?\\.admin\\.ch):[^:]+",
            setOf(RegexOption.MULTILINE)
        )
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `A did for a metadata trust statement is built correctly`() = runTest {
        val result = useCase(
            trustStatementType = TrustStatementType.METADATA,
            actorDid = inputWithDomain01,
            vcSchemaId = null
        ).assertOk()

        val didUrlEncoded = URLEncoder.encode(inputWithDomain01, Charsets.UTF_8.name)
        val expected = "https://example.org/api/v1/truststatements/$didUrlEncoded"

        assertEquals(expected, result.toString())
    }

    @Test
    fun `A did for a identity trust statement is built correctly`() = runTest {
        val result = useCase(
            trustStatementType = TrustStatementType.IDENTITY,
            actorDid = inputWithDomain01,
            vcSchemaId = null
        ).assertOk()

        val didUrlEncoded = URLEncoder.encode(inputWithDomain01, Charsets.UTF_8.name)
        val expected = "https://example.org/api/v1/truststatements/identity/$didUrlEncoded"

        assertEquals(expected, result.toString())
    }

    @Test
    fun `A did for a issuance trust statement is built correctly`() = runTest {
        val vcSchemaId = "vcSchemaId"
        val result = useCase(
            trustStatementType = TrustStatementType.ISSUANCE,
            actorDid = inputWithDomain01,
            vcSchemaId = vcSchemaId
        ).assertOk()

        val vcSchemaIdUrlEncoded = URLEncoder.encode(vcSchemaId, Charsets.UTF_8.name)
        val expected = "https://example.org/api/v1/truststatements/issuance/?vcSchemaId=$vcSchemaIdUrlEncoded"

        assertEquals(expected, result.toString())
    }

    @Test
    fun `A did for a verification trust statement is built correctly`() = runTest {
        val vcSchemaId = "vcSchemaId"
        val result = useCase(
            trustStatementType = TrustStatementType.VERIFICATION,
            actorDid = inputWithDomain01,
            vcSchemaId = vcSchemaId
        ).assertOk()

        val vcSchemaIdUrlEncoded = URLEncoder.encode(vcSchemaId, Charsets.UTF_8.name)
        val expected = "https://example.org/api/v1/truststatements/verification/?vcSchemaId=$vcSchemaIdUrlEncoded"

        assertEquals(expected, result.toString())
    }

    @Test
    fun `A Did with supported base domain returns a valid Url`() {
        useCase(
            trustStatementType = TrustStatementType.METADATA,
            actorDid = inputWithDomain01,
            vcSchemaId = null
        ).assertOk()
    }

    @Test
    fun `A Did with supported base domain returns the right mapping`() {
        val result = useCase(
            trustStatementType = TrustStatementType.METADATA,
            actorDid = inputWithDomain02,
            vcSchemaId = null
        ).assertOk()
        assert(result.host == trustRegistryMappings[domain02])
    }

    @Test
    fun `A call uses the provided trust registry mapping`() {
        useCase(
            trustStatementType = TrustStatementType.METADATA,
            actorDid = inputWithDomain02,
            vcSchemaId = null
        )

        coVerify(exactly = 1) {
            mockEnvironmentSetup.trustRegistryMapping
        }
    }

    @Test
    fun `A Did with unsupported base domain returns an error`() {
        useCase(
            trustStatementType = TrustStatementType.METADATA,
            actorDid = inputDidWithUnsupportedDomain,
            vcSchemaId = null
        ).assertErrorType(GetTrustUrlFromDidError.NoTrustRegistryMapping::class)
    }

    @Test
    fun `An random string input returns an error`() {
        useCase(
            trustStatementType = TrustStatementType.METADATA,
            actorDid = inputRandom,
            vcSchemaId = null
        ).assertErrorType(GetTrustUrlFromDidError.NoTrustRegistryMapping::class)
    }

    @Test
    fun `A non-did twd input returns an error`() {
        useCase(
            trustStatementType = TrustStatementType.METADATA,
            actorDid = inputNonDid,
            vcSchemaId = null
        ).assertErrorType(GetTrustUrlFromDidError.NoTrustRegistryMapping::class)
    }

    @Test
    fun `A did input with unsupported method returns an error`() {
        useCase(
            trustStatementType = TrustStatementType.METADATA,
            actorDid = inputDidWithUnsupportedMethod,
            vcSchemaId = null
        ).assertErrorType(GetTrustUrlFromDidError.NoTrustRegistryMapping::class)
    }

    @Test
    fun `An empty input returns an error`() {
        useCase(
            trustStatementType = TrustStatementType.METADATA,
            actorDid = " ",
            vcSchemaId = null
        ).assertErrorType(GetTrustUrlFromDidError.NoTrustRegistryMapping::class)
    }

    private val domain01 = "some.domain.swiyu.admin.ch"
    private val domain02 = "dev.other.domain.swiyu.admin.ch"

    private val inputWithDomain01 = "did:tdw:randomid=:$domain01:api:v1:did:randomuuid"
    private val inputWithDomain02 = "did:tdw:randomid=:$domain02:api:v1:did:randomuuid2"
    private val inputDidWithUnsupportedDomain = "did:tdw:randomid=:wrong.domain.admin.ch:api:v1:did:randomuuid"
    private val inputDidWithUnsupportedMethod = "did:web:randomid=:some.domain.bit.admin.ch:api:v1:did:randomuuid"
    private val inputNonDid = "https//twd:randomid=:some.domain.bit.admin.ch:api:v1:did:randomuuid"
    private val inputRandom = "1e4ddcb5e7670:9e8ae97e6e8/fd7df"

    private val trustRegistryMappings = mapOf(
        domain01 to "example.org",
        domain02 to "dev.org",
    )
}
