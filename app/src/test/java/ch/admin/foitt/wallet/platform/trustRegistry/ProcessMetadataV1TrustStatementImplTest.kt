package ch.admin.foitt.wallet.platform.trustRegistry

import ch.admin.foitt.openid4vc.domain.model.vcSdJwt.VcSdJwt
import ch.admin.foitt.wallet.platform.trustRegistry.domain.model.GetTrustUrlFromDidError
import ch.admin.foitt.wallet.platform.trustRegistry.domain.model.MetadataV1TrustStatement
import ch.admin.foitt.wallet.platform.trustRegistry.domain.model.TrustRegistryError
import ch.admin.foitt.wallet.platform.trustRegistry.domain.model.TrustStatementType
import ch.admin.foitt.wallet.platform.trustRegistry.domain.repository.TrustStatementRepository
import ch.admin.foitt.wallet.platform.trustRegistry.domain.usecase.GetTrustUrlFromDid
import ch.admin.foitt.wallet.platform.trustRegistry.domain.usecase.ProcessMetadataV1TrustStatement
import ch.admin.foitt.wallet.platform.trustRegistry.domain.usecase.ValidateTrustStatement
import ch.admin.foitt.wallet.platform.trustRegistry.domain.usecase.implementation.ProcessMetadataV1TrustStatementImpl
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

class ProcessMetadataV1TrustStatementImplTest {
    @MockK
    private lateinit var mockGetTrustUrlFromDid: GetTrustUrlFromDid

    @MockK
    private lateinit var mockTrustStatementRepository: TrustStatementRepository

    @MockK
    private lateinit var mockValidateTrustStatement: ValidateTrustStatement

    private val safeJson = SafeJsonTestInstance.safeJson

    private lateinit var useCase: ProcessMetadataV1TrustStatement

    @BeforeEach
    fun setup() {
        MockKAnnotations.init(this)
        useCase = ProcessMetadataV1TrustStatementImpl(
            getTrustUrlFromDid = mockGetTrustUrlFromDid,
            trustStatementRepository = mockTrustStatementRepository,
            validateTrustStatement = mockValidateTrustStatement,
            safeJson = safeJson,
        )

        setupDefaultMocks()
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `A metadataV1 trust statement is correctly processed`() = runTest {
        val result = useCase(issuerDid).assertOk()

        val expected = safeJson.safeDecodeElementTo<MetadataV1TrustStatement>(validTrustStatement.sdJwtJson).value

        assertEquals(expected, result)
    }

    @Test
    fun `Processing metadataV1 trust statements for multiple trust statements takes the first valid one`() = runTest {
        coEvery {
            mockTrustStatementRepository.fetchTrustStatements(any())
        } returns Ok(listOf(trustStatementRaw2, trustStatementRaw))

        val exception = IllegalStateException("no valid trust statements")
        coEvery {
            mockValidateTrustStatement(any(), issuerDid)
        } returnsMany listOf(Err(TrustRegistryError.Unexpected(exception)), Ok(validTrustStatement))

        val result = useCase(issuerDid).assertOk()

        val expected = safeJson.safeDecodeElementTo<MetadataV1TrustStatement>(validTrustStatement.sdJwtJson).value

        assertEquals(expected, result)
    }

    @Test
    fun `Processing metadataV1 trust statements maps errors from getting trust domain`() = runTest {
        coEvery {
            mockGetTrustUrlFromDid(trustStatementType = TrustStatementType.METADATA, actorDid = issuerDid, vcSchemaId = null)
        } returns Err(GetTrustUrlFromDidError.NoTrustRegistryMapping("invalid mapping"))

        useCase(issuerDid).assertErrorType(TrustRegistryError.Unexpected::class)
    }

    @Test
    fun `Processing metadataV1 trust statements maps errors from vc sd jwt creation`() = runTest {
        coEvery { mockTrustStatementRepository.fetchTrustStatements(any()) } returns Ok(listOf(trustStatementRawNoIss))

        useCase(issuerDid).assertErrorType(TrustRegistryError.Unexpected::class)
    }

    @Test
    fun `Processing metadataV1 trust statements where the statements contain no metadataV1 statement returns an error`() = runTest {
        coEvery { mockTrustStatementRepository.fetchTrustStatements(any()) } returns Ok(listOf(trustStatementRawOtherVct))

        useCase(issuerDid).assertErrorType(TrustRegistryError.Unexpected::class)
    }

    @Test
    fun `Processing metadataV1 trust statements where all statements are invalid returns an error`() = runTest {
        val exception = IllegalStateException("no valid trust statements")
        coEvery { mockValidateTrustStatement(any(), any()) } returns Err(TrustRegistryError.Unexpected(exception))

        useCase(issuerDid).assertErrorType(TrustRegistryError.Unexpected::class)
    }

    @Test
    fun `Processing metadataV1 trust statements maps errors from parsing to metadataV1`() = runTest {
        coEvery { mockTrustStatementRepository.fetchTrustStatements(any()) } returns Ok(listOf(trustStatementRawNoOrgName))
        coEvery { mockValidateTrustStatement(any(), issuerDid) } returns Ok(validTrustStatementNoOrgName)

        useCase(issuerDid).assertErrorType(TrustRegistryError.Unexpected::class)
    }

    private fun setupDefaultMocks() {
        coEvery {
            mockGetTrustUrlFromDid(trustStatementType = TrustStatementType.METADATA, actorDid = issuerDid, vcSchemaId = null)
        } returns Ok(trustUrl)

        coEvery { mockTrustStatementRepository.fetchTrustStatements(any()) } returns Ok(listOf(trustStatementRaw))

        coEvery { mockValidateTrustStatement(any(), issuerDid) } returns Ok(validTrustStatement)
    }

    private val issuerDid = "issuer did"
    private val trustUrl = URL("https://example.org/trust")
    private val trustStatementRaw = "eyJ2ZXIiOiIxLjAiLCJ0eXAiOiJ2YytzZC1qd3QiLCJhbGciOiJFUzI1NiIsImtpZCI6ImRpZDp0ZHc6a2lkIn0.eyJ2Y3QiOiJUcnVzdFN0YXRlbWVudE1ldGFkYXRhVjEiLCJpc3MiOiJkaWQ6dGR3OmlzcyIsInN1YiI6ImRpZDp0ZHc6c3ViIiwiaWF0IjowLCJzdGF0dXMiOnsic3RhdHVzX2xpc3QiOnsidHlwZSI6IlN3aXNzVG9rZW5TdGF0dXNMaXN0LTEuMCIsInVyaSI6Imh0dHBzOi8vZXhhbXBsZS5vcmcvYXBpL3YxL3N0YXR1c2xpc3QvaWQuand0IiwiaWR4IjoxfX0sImV4cCI6MTkxNDIxNjUyMSwibmJmIjoxLCJvcmdOYW1lIjp7ImRlLUNIIjoib3JnTmFtZSAoZGUtQ0gpIiwiZW4iOiJvcmdOYW1lIChlbikifSwicHJlZkxhbmciOiJkZS1DSCIsIl9zZF9hbGciOiJzaGEtMjU2In0.UUUOCU8lT_gOwmJwcfc3Kw8wZLhFlmxDJAPX0oq53fWx80UkLxirtkUmWNRWKWqQRg13Qw-wqhDbcBmufdqO2w"
    private val trustStatementRaw2 = "eyJ2ZXIiOiIxLjAiLCJ0eXAiOiJ2YytzZC1qd3QiLCJhbGciOiJFUzI1NiIsImtpZCI6ImRpZDp0ZHc6a2lkIn0.eyJ2Y3QiOiJUcnVzdFN0YXRlbWVudE1ldGFkYXRhVjEiLCJpc3MiOiJkaWQ6dGR3OmlzcyIsInN1YiI6ImRpZDp0ZHc6c3ViIiwiaWF0IjowLCJzdGF0dXMiOnsic3RhdHVzX2xpc3QiOnsidHlwZSI6IlN3aXNzVG9rZW5TdGF0dXNMaXN0LTEuMCIsInVyaSI6Imh0dHBzOi8vZXhhbXBsZS5vcmcvYXBpL3YxL3N0YXR1c2xpc3QvaWQuand0IiwiaWR4IjoyfX0sImV4cCI6MTkxNDIxNjUyMSwibmJmIjoxLCJvcmdOYW1lIjp7ImRlLUNIIjoib3JnTmFtZSAoZGUtQ0gpIiwiZW4iOiJvcmdOYW1lIChlbikifSwicHJlZkxhbmciOiJkZS1DSCIsIl9zZF9hbGciOiJzaGEtMjU2In0.09ASSButmwXD0hiXVZj4yczubhrulO3tNLpXnYFKix1ZLV_vHylLRxU9EMwa4nq6gQQA6mjEKj-U3y5NOFsG-g"
    private val trustStatementRawNoIss = "eyJ2ZXIiOiIxLjAiLCJ0eXAiOiJ2YytzZC1qd3QiLCJhbGciOiJFUzI1NiIsImtpZCI6ImRpZDp0ZHc6a2lkIn0.eyJ2Y3QiOiJUcnVzdFN0YXRlbWVudE1ldGFkYXRhVjEiLCJzdWIiOiJkaWQ6dGR3OnN1YiIsImlhdCI6MCwic3RhdHVzIjp7InN0YXR1c19saXN0Ijp7InR5cGUiOiJTd2lzc1Rva2VuU3RhdHVzTGlzdC0xLjAiLCJ1cmkiOiJodHRwczovL2V4YW1wbGUub3JnL2FwaS92MS9zdGF0dXNsaXN0L2lkLmp3dCIsImlkeCI6MX19LCJleHAiOjE5MTQyMTY1MjEsIm5iZiI6MSwib3JnTmFtZSI6eyJkZS1DSCI6Im9yZ05hbWUgKGRlLUNIKSIsImVuIjoib3JnTmFtZSAoZW4pIn0sInByZWZMYW5nIjoiZGUtQ0giLCJfc2RfYWxnIjoic2hhLTI1NiJ9.RB7GE3rJkwCsXefyu5tnViw4XMTelbJL2zxJ7JTs0DC0rxp3t13eRs6Ch3xjpIzF0cHQdXJ5hHJAsWP7pMEOfQ"
    private val trustStatementRawOtherVct = "eyJ2ZXIiOiIxLjAiLCJ0eXAiOiJ2YytzZC1qd3QiLCJhbGciOiJFUzI1NiIsImtpZCI6ImRpZDp0ZHc6a2lkIn0.eyJ2Y3QiOiJvdGhlclZjdCIsImlzcyI6ImRpZDp0ZHc6aXNzIiwic3ViIjoiZGlkOnRkdzpzdWIiLCJpYXQiOjAsInN0YXR1cyI6eyJzdGF0dXNfbGlzdCI6eyJ0eXBlIjoiU3dpc3NUb2tlblN0YXR1c0xpc3QtMS4wIiwidXJpIjoiaHR0cHM6Ly9leGFtcGxlLm9yZy9hcGkvdjEvc3RhdHVzbGlzdC9pZC5qd3QiLCJpZHgiOjF9fSwiZXhwIjoxOTE0MjE2NTIxLCJuYmYiOjEsIm9yZ05hbWUiOnsiZGUtQ0giOiJvcmdOYW1lIChkZS1DSCkiLCJlbiI6Im9yZ05hbWUgKGVuKSJ9LCJwcmVmTGFuZyI6ImRlLUNIIiwiX3NkX2FsZyI6InNoYS0yNTYifQ.ELG-K5zwnmOmPhlPXl-wMEWoJs10yAGYjFfu9uUOdAWLslXqSzXb1s76XSeqCJi4h6U3Q7VleYMS4KPgfyK0RA"
    private val trustStatementRawNoOrgName = "eyJ2ZXIiOiIxLjAiLCJ0eXAiOiJ2YytzZC1qd3QiLCJhbGciOiJFUzI1NiIsImtpZCI6ImRpZDp0ZHc6a2lkIn0.eyJ2Y3QiOiJvdGhlclZjdCIsImlzcyI6ImRpZDp0ZHc6aXNzIiwic3ViIjoiZGlkOnRkdzpzdWIiLCJpYXQiOjAsInN0YXR1cyI6eyJzdGF0dXNfbGlzdCI6eyJ0eXBlIjoiU3dpc3NUb2tlblN0YXR1c0xpc3QtMS4wIiwidXJpIjoiaHR0cHM6Ly9leGFtcGxlLm9yZy9hcGkvdjEvc3RhdHVzbGlzdC9pZC5qd3QiLCJpZHgiOjF9fSwiZXhwIjoxOTE0MjE2NTIxLCJuYmYiOjEsInByZWZMYW5nIjoiZGUtQ0giLCJfc2RfYWxnIjoic2hhLTI1NiJ9.f9WtAUaZtHxqPEP-hKfkDwLULFYsTmge8s5tr8ouV95gXdWopKw6TOeNex7nVQedMIOO1xYM7Y-rzWc3VYgaMA"
    private val validTrustStatement = VcSdJwt(trustStatementRaw)
    private val validTrustStatementNoOrgName = VcSdJwt(trustStatementRawNoOrgName)
}
