package ch.admin.foitt.wallet.platform.trustRegistry

import ch.admin.foitt.openid4vc.domain.model.vcSdJwt.VcSdJwt
import ch.admin.foitt.wallet.platform.environmentSetup.domain.repository.EnvironmentSetupRepository
import ch.admin.foitt.wallet.platform.trustRegistry.domain.model.GetTrustUrlFromDidError
import ch.admin.foitt.wallet.platform.trustRegistry.domain.model.TrustRegistryError
import ch.admin.foitt.wallet.platform.trustRegistry.domain.model.TrustStatementActor
import ch.admin.foitt.wallet.platform.trustRegistry.domain.model.VcSchemaTrustStatus
import ch.admin.foitt.wallet.platform.trustRegistry.domain.repository.TrustStatementRepository
import ch.admin.foitt.wallet.platform.trustRegistry.domain.usecase.FetchVcSchemaTrustStatus
import ch.admin.foitt.wallet.platform.trustRegistry.domain.usecase.GetTrustUrlFromDid
import ch.admin.foitt.wallet.platform.trustRegistry.domain.usecase.ValidateTrustStatement
import ch.admin.foitt.wallet.platform.trustRegistry.domain.usecase.implementation.FetchVcSchemaTrustStatusImpl
import ch.admin.foitt.wallet.util.SafeJsonTestInstance
import ch.admin.foitt.wallet.util.assertErrorType
import ch.admin.foitt.wallet.util.assertOk
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import io.mockk.unmockkAll
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.net.URL

class FetchVcSchemaTrustStatusImplTest {

    @MockK
    private lateinit var mockGetTrustUrlFromDid: GetTrustUrlFromDid

    @MockK
    private lateinit var mockTrustStatementRepository: TrustStatementRepository

    @MockK
    private lateinit var mockEnvironmentSetupRepository: EnvironmentSetupRepository

    @MockK
    private lateinit var mockValidateTrustStatement: ValidateTrustStatement

    private val safeJson = SafeJsonTestInstance.safeJson

    private lateinit var useCase: FetchVcSchemaTrustStatus

    @BeforeEach
    fun setup() {
        MockKAnnotations.init(this)

        useCase = FetchVcSchemaTrustStatusImpl(
            getTrustUrlFromDid = mockGetTrustUrlFromDid,
            trustStatementRepository = mockTrustStatementRepository,
            environmentSetupRepo = mockEnvironmentSetupRepository,
            validateTrustStatement = mockValidateTrustStatement,
            safeJson = safeJson
        )

        setupDefaultMocks()
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `vc schema trust status for issuance is fetched correctly`() = runTest {
        val result = useCase(
            trustStatementActor = TrustStatementActor.ISSUER,
            actorDid = actorDid,
            vcSchemaId = vcSchemaId,
        ).assertOk()

        assertEquals(VcSchemaTrustStatus.TRUSTED, result)
    }

    @Test
    fun `vc schema trust status for verification is fetched correctly`() = runTest {
        coEvery {
            mockTrustStatementRepository.fetchTrustStatements(trustUrl)
        } returns Ok(listOf(validVerificationTrustStatement))

        coEvery {
            mockValidateTrustStatement(any(), any())
        } returns Ok(VcSdJwt(validVerificationTrustStatement))

        val result = useCase(
            trustStatementActor = TrustStatementActor.VERIFIER,
            actorDid = actorDid,
            vcSchemaId = vcSchemaId,
        ).assertOk()

        assertEquals(VcSchemaTrustStatus.TRUSTED, result)
    }

    @Test
    fun `fetching vc schema trust maps errors from getting the trust url`() = runTest {
        coEvery {
            mockGetTrustUrlFromDid(any(), actorDid, vcSchemaId)
        } returns Err(GetTrustUrlFromDidError.NoTrustRegistryMapping("invalid mapping"))

        useCase(
            trustStatementActor = TrustStatementActor.ISSUER,
            actorDid = actorDid,
            vcSchemaId = vcSchemaId,
        ).assertErrorType(TrustRegistryError.Unexpected::class)
    }

    @Test
    fun `fetching vc schema trust maps errors from the trust repo`() = runTest {
        coEvery {
            mockTrustStatementRepository.fetchTrustStatements(trustUrl)
        } returns Err(TrustRegistryError.Unexpected(null))

        useCase(
            trustStatementActor = TrustStatementActor.ISSUER,
            actorDid = actorDid,
            vcSchemaId = vcSchemaId,
        ).assertErrorType(TrustRegistryError.Unexpected::class)
    }

    @Test
    fun `fetching vc schema trust where a trust statement is a invalid VcSdJwt returns an error`() = runTest {
        coEvery {
            mockTrustStatementRepository.fetchTrustStatements(trustUrl)
        } returns Ok(listOf(invalidVcSdJwtTrustStatement))

        useCase(
            trustStatementActor = TrustStatementActor.ISSUER,
            actorDid = actorDid,
            vcSchemaId = vcSchemaId,
        ).assertErrorType(TrustRegistryError.Unexpected::class)
    }

    @Test
    fun `fetching vc schema trust where no trust statements with the correct vct exist returns unprotected`() = runTest {
        coEvery {
            mockTrustStatementRepository.fetchTrustStatements(trustUrl)
        } returns Ok(listOf(trustStatementOtherVct))

        val result = useCase(
            trustStatementActor = TrustStatementActor.VERIFIER,
            actorDid = actorDid,
            vcSchemaId = vcSchemaId,
        ).assertOk()

        assertEquals(VcSchemaTrustStatus.UNPROTECTED, result)
    }

    @Test
    fun `fetching vc schema trust where no trust statements with trusted did exist returns unprotected`() = runTest {
        coEvery { mockEnvironmentSetupRepository.trustRegistryTrustedDids } returns listOf("other issuer")

        val result = useCase(
            trustStatementActor = TrustStatementActor.VERIFIER,
            actorDid = actorDid,
            vcSchemaId = vcSchemaId,
        ).assertOk()

        assertEquals(VcSchemaTrustStatus.UNPROTECTED, result)
    }

    @Test
    fun `fetching vc schema trust where no valid trust statements exist returns not trusted`() = runTest {
        coEvery {
            mockValidateTrustStatement(any(), any())
        } returns Err(TrustRegistryError.Unexpected(null))

        val result = useCase(
            trustStatementActor = TrustStatementActor.ISSUER,
            actorDid = actorDid,
            vcSchemaId = vcSchemaId,
        ).assertOk()

        assertEquals(VcSchemaTrustStatus.NOT_TRUSTED, result)
    }

    @Test
    fun `fetching vc schema trust where multiple valid trust statements exist returns not_trusted`() = runTest {
        coEvery {
            mockTrustStatementRepository.fetchTrustStatements(trustUrl)
        } returns Ok(listOf(validIssuanceTrustStatement, validIssuanceTrustStatement2))

        coEvery {
            mockValidateTrustStatement(any(), any())
        } returnsMany listOf(
            Ok(VcSdJwt(validIssuanceTrustStatement)),
            Ok(VcSdJwt(validIssuanceTrustStatement2))
        )

        val result = useCase(
            trustStatementActor = TrustStatementActor.ISSUER,
            actorDid = actorDid,
            vcSchemaId = vcSchemaId,
        ).assertOk()

        assertEquals(VcSchemaTrustStatus.NOT_TRUSTED, result)
    }

    @Test
    fun `fetching vc schema trust where trust statement is valid but with incorrect vcSchemaId returns not trusted`() = runTest {
        coEvery {
            mockTrustStatementRepository.fetchTrustStatements(trustUrl)
        } returns Ok(listOf(trustStatementOtherVcSchemaId))

        coEvery {
            mockValidateTrustStatement(any(), any())
        } returns Ok(VcSdJwt(trustStatementOtherVcSchemaId))

        val result = useCase(
            trustStatementActor = TrustStatementActor.ISSUER,
            actorDid = actorDid,
            vcSchemaId = vcSchemaId,
        ).assertOk()

        assertEquals(VcSchemaTrustStatus.NOT_TRUSTED, result)
    }

    private fun setupDefaultMocks() {
        coEvery { mockGetTrustUrlFromDid(any(), actorDid, vcSchemaId) } returns Ok(trustUrl)

        coEvery {
            mockTrustStatementRepository.fetchTrustStatements(trustUrl)
        } returns Ok(listOf(validIssuanceTrustStatement))

        coEvery { mockEnvironmentSetupRepository.trustRegistryTrustedDids } returns trustedIssuers

        coEvery {
            mockValidateTrustStatement(any(), any())
        } returns Ok(VcSdJwt(validIssuanceTrustStatement))
    }

    private val actorDid = "actorDid"
    private val vcSchemaId = "vcSchemaId"
    private val trustUrl = URL("https://example.org/trust")

    private val trustedIssuers = listOf("issuer", "issuer2")

    private val validIssuanceTrustStatement =
        "eyJ0eXAiOiJ2YytzZC1qd3QiLCJhbGciOiJFUzI1NiIsImtpZCI6ImRpZDpleGFtcGxlOmlzc3VlciNrZXktMSJ9.eyJ2Y3QiOiJUcnVzdFN0YXRlbWVudElzc3VhbmNlVjEiLCJpc3MiOiJpc3N1ZXIiLCJzdWIiOiJzdWJqZWN0IiwiaWF0IjoxNzQyNDUzMjExLCJzdGF0dXMiOnsic3RhdHVzX2xpc3QiOnsidXJpIjoic3RhdHVzX2xpc3RfdXJpIiwiaWR4IjozMH19LCJuYmYiOjE3NDI0NTMyMTAsImV4cCI6MjIwOTAxNDAwMCwiY2FuSXNzdWUiOiJ2Y1NjaGVtYUlkIn0.n_ST8tt_S676jYBYMA_0orNIKdcGvj3SKjOcQg09NsuZrYpBUA8i8Gxq_QlB1_ddreTMNAdKGmEk9aTNVchQDA~"

    private val validIssuanceTrustStatement2 =
        "eyJ0eXAiOiJ2YytzZC1qd3QiLCJhbGciOiJFUzI1NiIsImtpZCI6ImRpZDpleGFtcGxlOmlzc3VlciNrZXktMSJ9.eyJ2Y3QiOiJUcnVzdFN0YXRlbWVudElzc3VhbmNlVjEiLCJpc3MiOiJpc3N1ZXIyIiwic3ViIjoic3ViamVjdCIsImlhdCI6MTc0MjQ1MzIxMSwic3RhdHVzIjp7InN0YXR1c19saXN0Ijp7InVyaSI6InN0YXR1c19saXN0X3VyaSIsImlkeCI6MzB9fSwibmJmIjoxNzQyNDUzMjEwLCJleHAiOjIyMDkwMTQwMDAsImNhbklzc3VlIjoidmNTY2hlbWFJZCJ9.HH59RrDSeD0WKBX7Xpdhs8-pM95VC3i6VJbyX0ytMs0HUavktWGl05wZbmgqHLkZuzmfhA6dbj317ReglhzgCg~"

    private val validVerificationTrustStatement =
        "eyJ0eXAiOiJ2YytzZC1qd3QiLCJhbGciOiJFUzI1NiIsImtpZCI6ImRpZDpleGFtcGxlOmlzc3VlciNrZXktMSJ9.eyJ2Y3QiOiJUcnVzdFN0YXRlbWVudFZlcmlmaWNhdGlvblYxIiwiaXNzIjoiaXNzdWVyIiwic3ViIjoic3ViamVjdCIsImlhdCI6MTc0MjQ1MzIxMSwic3RhdHVzIjp7InN0YXR1c19saXN0Ijp7InVyaSI6InN0YXR1c19saXN0X3VyaSIsImlkeCI6MzB9fSwibmJmIjoxNzQyNDUzMjEwLCJleHAiOjIyMDkwMTQwMDAsImNhblZlcmlmeSI6InZjU2NoZW1hSWQifQ.iMN2txeA4yTrLN6g4CiPFSpCMKkbA2T0KkmIeBStvrf104P1Xxvmp7jYo89ukOZGsAcxhhmi8yhUFOciyRkhbw~"

    private val trustStatementOtherVct =
        "eyJ0eXAiOiJ2YytzZC1qd3QiLCJhbGciOiJFUzI1NiIsImtpZCI6ImRpZDpleGFtcGxlOmlzc3VlciNrZXktMSJ9.eyJ2Y3QiOiJvdGhlclZjdCIsImlzcyI6Imlzc3VlciIsInN1YiI6InN1YmplY3QiLCJpYXQiOjE3NDI0NTMyMTEsInN0YXR1cyI6eyJzdGF0dXNfbGlzdCI6eyJ1cmkiOiJzdGF0dXNfbGlzdF91cmkiLCJpZHgiOjMwfX0sIm5iZiI6MTc0MjQ1MzIxMCwiZXhwIjoyMjA5MDE0MDAwLCJjYW5Jc3N1ZSI6InZjU2NoZW1hSWQifQ.UU62ikzjW6bsOU6f4Hz8tti_6lkXwBckxRxtxY6OfcFLgRWRd6ZkkafjP27wSyZY-o3VRnVtSHdgZNXkc_LLzA~"

    private val invalidVcSdJwtTrustStatement =
        "eyJ0eXAiOiJ2YytzZC1qd3QiLCJhbGciOiJFUzI1NiIsImtpZCI6ImRpZDpleGFtcGxlOmlzc3VlciNrZXktMSJ9.eyJ2Y3QiOiJUcnVzdFN0YXRlbWVudElzc3VhbmNlVjEiLCJzdWIiOiJzdWJqZWN0IiwiaWF0IjoxNzQyNDUzMjExLCJzdGF0dXMiOnsic3RhdHVzX2xpc3QiOnsidXJpIjoic3RhdHVzX2xpc3RfdXJpIiwiaWR4IjozMH19LCJuYmYiOjE3NDI0NTMyMTAsImV4cCI6MjIwOTAxNDAwMCwiY2FuSXNzdWUiOiJ2Y1NjaGVtYUlkIn0.TsnxBDj0uWXkXCjRb1YxpkUq7RD_tyrEg1gy6G0Cj-J7hLUeOLwI5PZFsuCIOugZO47qfgZYQVFti2roGGrKXw~"

    private val trustStatementOtherVcSchemaId =
        "eyJ0eXAiOiJ2YytzZC1qd3QiLCJhbGciOiJFUzI1NiIsImtpZCI6ImRpZDpleGFtcGxlOmlzc3VlciNrZXktMSJ9.eyJ2Y3QiOiJUcnVzdFN0YXRlbWVudElzc3VhbmNlVjEiLCJpc3MiOiJpc3N1ZXIiLCJzdWIiOiJzdWJqZWN0IiwiaWF0IjoxNzQyNDUzMjExLCJzdGF0dXMiOnsic3RhdHVzX2xpc3QiOnsidXJpIjoic3RhdHVzX2xpc3RfdXJpIiwiaWR4IjozMH19LCJuYmYiOjE3NDI0NTMyMTAsImV4cCI6MjIwOTAxNDAwMCwiY2FuSXNzdWUiOiJvdGhlclZjU2NoZW1hSWQifQ.NngPV3MhpW0guhTRrirWPE3Y4le98UGW8GfSrDsJPVBaIFn2Glr47YO-b03aWPsSnFpCoFWl23WUdj5LAzOqew~"
}
