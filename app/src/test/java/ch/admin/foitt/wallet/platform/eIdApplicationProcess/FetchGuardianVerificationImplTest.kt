package ch.admin.foitt.wallet.platform.eIdApplicationProcess

import ch.admin.foitt.wallet.platform.appAttestation.domain.model.ClientAttestation
import ch.admin.foitt.wallet.platform.appAttestation.domain.repository.CurrentClientAttestationRepository
import ch.admin.foitt.wallet.platform.eIdApplicationProcess.domain.model.EIdRequestError
import ch.admin.foitt.wallet.platform.eIdApplicationProcess.domain.model.GuardianVerificationResponse
import ch.admin.foitt.wallet.platform.eIdApplicationProcess.domain.repository.SIdRepository
import ch.admin.foitt.wallet.platform.eIdApplicationProcess.domain.usecase.implementation.FetchGuardianVerificationImpl
import ch.admin.foitt.wallet.util.assertErrorType
import ch.admin.foitt.wallet.util.assertOk
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import io.mockk.unmockkAll
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class FetchGuardianVerificationImplTest {
    @MockK
    private lateinit var mockSIdRepository: SIdRepository

    @MockK
    private lateinit var mockClientAttestationRepository: CurrentClientAttestationRepository

    @MockK
    private lateinit var mockGuardianVerificationResponse: GuardianVerificationResponse

    @MockK
    private lateinit var mockClientAttestation: ClientAttestation

    private lateinit var testAttestationFlow: Flow<ClientAttestation?>

    private lateinit var useCase: FetchGuardianVerificationImpl

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)

        useCase = FetchGuardianVerificationImpl(
            sIdRepository = mockSIdRepository,
            currentClientAttestationRepository = mockClientAttestationRepository,
        )

        testAttestationFlow = flowOf(mockClientAttestation)

        coEvery { mockSIdRepository.fetchSIdGuardianVerification(any(), any()) } returns Ok(mockGuardianVerificationResponse)
        coEvery { mockClientAttestationRepository.getFlow() } returns testAttestationFlow
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `A success returns a guardian verification response`() = runTest {
        val result = useCase("caseId")
        val response = result.assertOk()
        assertEquals(mockGuardianVerificationResponse, response)
    }

    @Test
    fun `A missing client attestation results in an error`() = runTest {
        coEvery { mockClientAttestationRepository.getFlow() } returns flowOf(null)

        val result = useCase("caseId")
        result.assertErrorType(EIdRequestError.Unexpected::class)
    }

    @Test
    fun `A SId repository error is propagated`() = runTest {
        coEvery { mockSIdRepository.fetchSIdGuardianVerification(any(), any()) } returns Err(EIdRequestError.NetworkError)

        val result = useCase("caseId")
        result.assertErrorType(EIdRequestError.NetworkError::class)
    }
}
