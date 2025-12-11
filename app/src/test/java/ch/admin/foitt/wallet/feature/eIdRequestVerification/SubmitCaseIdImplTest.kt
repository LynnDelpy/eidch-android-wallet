package ch.admin.foitt.wallet.feature.eIdRequestVerification

import ch.admin.foitt.wallet.feature.eIdRequestVerification.domain.usecase.SubmitCaseId
import ch.admin.foitt.wallet.feature.eIdRequestVerification.domain.usecase.implementation.SubmitCaseIdImpl
import ch.admin.foitt.wallet.platform.eIdApplicationProcess.domain.model.EIdRequestError
import ch.admin.foitt.wallet.platform.eIdApplicationProcess.domain.repository.EIdAvRepository
import ch.admin.foitt.wallet.util.assertErrorType
import ch.admin.foitt.wallet.util.assertOk
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import io.mockk.unmockkAll
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class SubmitCaseIdImplTest {
    @MockK
    private lateinit var mockEIdAvRepository: EIdAvRepository

    private val testDispatcher = StandardTestDispatcher()

    lateinit var submitCaseId: SubmitCaseId

    @BeforeEach
    fun setup() {
        MockKAnnotations.init(this)

        submitCaseId = SubmitCaseIdImpl(
            eIdAvRepository = mockEIdAvRepository
        )
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `Successfully submit a case returns an Ok`() = runTest(testDispatcher) {
        coEvery {
            mockEIdAvRepository.submitCase(any(), any())
        } returns Ok(Unit)

        submitCaseId(caseId = "", accessToken = "").assertOk()
    }

    @Test
    fun `Error when submitting a case from the repository is propagated`() = runTest(testDispatcher) {
        val exception = Exception("error in db")
        coEvery {
            mockEIdAvRepository.submitCase(any(), any())
        } returns Err(EIdRequestError.Unexpected(exception))

        submitCaseId(caseId = "", accessToken = "").assertErrorType(EIdRequestError.Unexpected::class)
    }
}
