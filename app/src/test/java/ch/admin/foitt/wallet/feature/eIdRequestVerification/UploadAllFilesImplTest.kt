package ch.admin.foitt.wallet.feature.eIdRequestVerification

import ch.admin.foitt.wallet.feature.eIdRequestVerification.domain.usecase.UploadAllFiles
import ch.admin.foitt.wallet.feature.eIdRequestVerification.domain.usecase.implementation.UploadAllFilesImpl
import ch.admin.foitt.wallet.platform.database.domain.model.EIdRequestFile
import ch.admin.foitt.wallet.platform.eIdApplicationProcess.domain.model.EIdRequestError
import ch.admin.foitt.wallet.platform.eIdApplicationProcess.domain.repository.EIdRequestFileRepository
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

    private val testDispatcher = StandardTestDispatcher()

    lateinit var uploadAllFiles: UploadAllFiles

    @BeforeEach
    fun setup() {
        MockKAnnotations.init(this)

        uploadAllFiles = UploadAllFilesImpl(
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
        coEvery { mockEIdRequestFile.fileName } returns ("")
        coEvery { mockEIdRequestFile.data } returns (byteArrayOf())

        coEvery {
            mockUploadFileToCase.invoke(any())
        } returns Ok(Unit)

        uploadAllFiles(caseId = "", accessToken = "").assertOk()
    }

    @Test
    fun `Error when getting a file from the repository is propagated`() = runTest(testDispatcher) {
        val exception = Exception("error in db")
        coEvery {
            mockEIdRequestFileRepository.getEIdRequestFileByCaseIdAndFileName(any(), any())
        } returns Err(EIdRequestError.Unexpected(exception))

        uploadAllFiles(caseId = "", accessToken = "").assertErrorType(EIdRequestError.FileNotFound::class)
    }
}
