package ch.admin.foitt.wallet.platform.trustRegistry

import ch.admin.foitt.wallet.platform.environmentSetup.domain.repository.EnvironmentSetupRepository
import ch.admin.foitt.wallet.platform.trustRegistry.domain.model.GetTrustDomainFromDidError
import ch.admin.foitt.wallet.platform.trustRegistry.domain.usecase.GetTrustDomainFromDid
import ch.admin.foitt.wallet.platform.trustRegistry.domain.usecase.implementation.GetTrustDomainFromDidImpl
import ch.admin.foitt.wallet.util.assertErrorType
import ch.admin.foitt.wallet.util.assertOk
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import io.mockk.unmockkAll
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class GetTrustDomainFromDidImplTest {

    @MockK
    private lateinit var mockEnvironmentSetup: EnvironmentSetupRepository

    private lateinit var useCase: GetTrustDomainFromDid

    @BeforeEach
    fun setup() {
        MockKAnnotations.init(this)
        useCase = GetTrustDomainFromDidImpl(mockEnvironmentSetup)

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
    fun `A Did with supported base domain returns a valid Url`() {
        useCase(
            actorDid = inputWithDomain01,
        ).assertOk()
    }

    @Test
    fun `A Did with supported base domain returns the right mapping`() {
        val result = useCase(
            actorDid = inputWithDomain02,
        ).assertOk()
        assert(result == trustRegistryMappings[domain02])
    }

    @Test
    fun `A call uses the provided trust registry mapping`() {
        useCase(
            actorDid = inputWithDomain02,
        )

        coVerify(exactly = 1) {
            mockEnvironmentSetup.trustRegistryMapping
        }
    }

    @Test
    fun `A Did with unsupported base domain returns an error`() {
        useCase(
            actorDid = inputDidWithUnsupportedDomain,
        ).assertErrorType(GetTrustDomainFromDidError.NoTrustRegistryMapping::class)
    }

    @Test
    fun `An random string input returns an error`() {
        useCase(
            actorDid = inputRandom,
        ).assertErrorType(GetTrustDomainFromDidError.NoTrustRegistryMapping::class)
    }

    @Test
    fun `A non-did twd input returns an error`() {
        useCase(
            actorDid = inputNonDid,
        ).assertErrorType(GetTrustDomainFromDidError.NoTrustRegistryMapping::class)
    }

    @Test
    fun `A did input with unsupported method returns an error`() {
        useCase(
            actorDid = inputDidWithUnsupportedMethod,
        ).assertErrorType(GetTrustDomainFromDidError.NoTrustRegistryMapping::class)
    }

    @Test
    fun `An empty input returns an error`() {
        useCase(
            actorDid = " ",
        ).assertErrorType(GetTrustDomainFromDidError.NoTrustRegistryMapping::class)
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
