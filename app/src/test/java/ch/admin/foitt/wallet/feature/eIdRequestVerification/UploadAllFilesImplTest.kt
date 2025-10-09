package ch.admin.foitt.wallet.feature.eIdRequestVerification

import ch.admin.foitt.wallet.feature.eIdRequestVerification.domain.usecase.UploadAllFiles
import ch.admin.foitt.wallet.feature.eIdRequestVerification.domain.usecase.implementation.UploadAllFilesImpl
import ch.admin.foitt.wallet.platform.database.domain.model.EIdRequestFile
import ch.admin.foitt.wallet.platform.eIdApplicationProcess.domain.model.AutoVerificationResponse
import ch.admin.foitt.wallet.platform.eIdApplicationProcess.domain.model.EIdRequestError
import ch.admin.foitt.wallet.platform.eIdApplicationProcess.domain.repository.EIdRequestFileRepository
import ch.admin.foitt.wallet.platform.eIdApplicationProcess.domain.usecase.GetStartAutoVerificationResult
import ch.admin.foitt.wallet.platform.eIdApplicationProcess.domain.usecase.UploadFileToCase
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

class UploadAllFilesImplTest {

    @MockK
    private lateinit var mockEIdRequestFile: EIdRequestFile

    @MockK
    private lateinit var mockEIdRequestFileRepository: EIdRequestFileRepository

    @MockK
    private lateinit var mockUploadFileToCase: UploadFileToCase

    @MockK
    private lateinit var mockGetStartAutoVerificationResult: GetStartAutoVerificationResult

    private val testDispatcher = StandardTestDispatcher()

    lateinit var uploadAllFiles: UploadAllFiles

    private val autoVerificationResponse: AutoVerificationResponse = AutoVerificationResponse(
        jwt = "",
        useNfc = false,
        scanDocument = false,
        recordDocumentVideo = false
    )

    @BeforeEach
    fun setup() {
        MockKAnnotations.init(this)

        uploadAllFiles = UploadAllFilesImpl(
            mockGetStartAutoVerificationResult,
            mockEIdRequestFileRepository,
            mockUploadFileToCase,
            testDispatcher
        )
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `Successfully getting a file by caseId returns an Ok`() = runTest(testDispatcher) {
        coEvery {
            mockEIdRequestFileRepository.getEIdRequestFileByCaseIdAndFileName(any(), any())
        } returns Ok(mockEIdRequestFile)
        coEvery { mockGetStartAutoVerificationResult().value } returns (autoVerificationResponse)
        coEvery { mockEIdRequestFile.fileName } returns ("")
        coEvery { mockEIdRequestFile.data } returns (byteArrayOf())

        coEvery {
            mockUploadFileToCase.invoke(any())
        } returns Ok(Unit)

        uploadAllFiles(caseId = "").assertOk()
    }

    @Test
    fun `Getting an file maps error from the repo`() = runTest(testDispatcher) {
        val exception = IllegalStateException("error in db")
        coEvery { mockGetStartAutoVerificationResult().value } returns (autoVerificationResponse)
        coEvery {
            mockEIdRequestFileRepository.getEIdRequestFileByCaseIdAndFileName(any(), any())
        } returns Err(EIdRequestError.Unexpected(exception))

        uploadAllFiles(caseId = "").assertErrorType(EIdRequestError.Unexpected::class)
    }

    @Test
    fun `Getting an file maps error when not finding the file`() = runTest(testDispatcher) {
        coEvery { mockGetStartAutoVerificationResult().value } returns (autoVerificationResponse)
        coEvery {
            mockEIdRequestFileRepository.getEIdRequestFileByCaseIdAndFileName(any(), any())
        } returns Ok(null)

        uploadAllFiles(caseId = "").assertErrorType(EIdRequestError.FileNotFound::class)
    }
}
