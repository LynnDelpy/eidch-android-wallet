package ch.admin.foitt.wallet.feature.eIdRequestVerification.domain.usecase.implementation

import ch.admin.foitt.wallet.feature.eIdRequestVerification.domain.usecase.UploadAllFiles
import ch.admin.foitt.wallet.platform.database.domain.model.EIdRequestFile
import ch.admin.foitt.wallet.platform.di.IoDispatcher
import ch.admin.foitt.wallet.platform.eIdApplicationProcess.domain.model.AvUploadFilesError
import ch.admin.foitt.wallet.platform.eIdApplicationProcess.domain.model.EIdRequestError
import ch.admin.foitt.wallet.platform.eIdApplicationProcess.domain.model.UploadFileRequest
import ch.admin.foitt.wallet.platform.eIdApplicationProcess.domain.model.toAvUploadFilesError
import ch.admin.foitt.wallet.platform.eIdApplicationProcess.domain.repository.EIdRequestFileRepository
import ch.admin.foitt.wallet.platform.eIdApplicationProcess.domain.usecase.GetStartAutoVerificationResult
import ch.admin.foitt.wallet.platform.eIdApplicationProcess.domain.usecase.UploadFileToCase
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.andThen
import com.github.michaelbull.result.coroutines.coroutineBinding
import com.github.michaelbull.result.getError
import com.github.michaelbull.result.mapError
import com.github.michaelbull.result.onFailure
import com.github.michaelbull.result.onSuccess
import io.ktor.http.ContentType
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import javax.inject.Inject

class UploadAllFilesImpl @Inject constructor(
    private val getStartAutoVerificationResult: GetStartAutoVerificationResult,
    private val eIdRequestFileRepository: EIdRequestFileRepository,
    private val uploadFileToCase: UploadFileToCase,
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : UploadAllFiles {
    override suspend fun invoke(caseId: String): Result<Unit, AvUploadFilesError> = withContext(ioDispatcher) {
        coroutineBinding {
            val filesToUploadWithContentType: HashMap<String, ContentType> = hashMapOf(
                FIRST_PAGE to ContentType.Image.PNG,
                SECOND_PAGE to ContentType.Image.PNG,
                VIDEO to ContentType.Video.MP4,
                METADATA to ContentType.Application.OctetStream, // gyroscope data
                DOCUMENT to ContentType.Video.MP4, // optional unless docVideoRequired was true during case creation,
                MOBILE_RESULT_XML to ContentType.Application.Xml,
                MOBILE_RESULT_JSON to ContentType.Application.Json
            )

            getStartAutoVerificationResult().value?.let {
                filesToUploadWithContentType.map { (fileName, contentType) ->
                    async {
                        getEIdRequestFile(caseId, fileName).onSuccess { file ->
                            if (file != null) {
                                retryWithLimit(NUMBER_RETRIES) {
                                    uploadFileToCase(
                                        UploadFileRequest(
                                            caseId = caseId,
                                            accessToken = it.jwt,
                                            fileName = fileName,
                                            document = file.data,
                                            mime = contentType
                                        )
                                    ).mapError { it.toAvUploadFilesError() }
                                }.bind()
                            }
                        }.onFailure {
                            Err(EIdRequestError.FileNotFound(fileName))
                        }.bind()
                    }
                }.awaitAll()
            } ?: Err(EIdRequestError.MissingJwt).bind()
        }
    }

    private suspend fun getEIdRequestFile(
        caseId: String,
        fileName: String
    ): Result<EIdRequestFile?, AvUploadFilesError> {
        return eIdRequestFileRepository.getEIdRequestFileByCaseIdAndFileName(caseId, fileName)
            .mapError { it.toAvUploadFilesError() }
            .andThen { file ->
                file?.let { Ok(it) } ?: Err(EIdRequestError.FileNotFound(fileName))
            }
    }

    private suspend fun <T> retryWithLimit(
        times: Int = 3,
        block: suspend () -> Result<T, AvUploadFilesError>
    ): Result<T, AvUploadFilesError> {
        var lastError: AvUploadFilesError? = null
        repeat(times) { attempt ->
            val result = block()
            if (result.isOk) {
                return result
            } else {
                lastError = result.getError()
                if (attempt < times - 1) delay(DELAY_BEFORE_UPLOAD_AGAIN)
            }
        }
        return Err(lastError ?: EIdRequestError.Unexpected(null))
    }

    companion object {
        private const val FIRST_PAGE = "fullFrameFirstPage.png"
        private const val SECOND_PAGE = "fullFrameSecondPage.png"
        private const val VIDEO = "video.mp4"
        private const val METADATA = "metadata.bin"
        private const val DOCUMENT = "document.mp4"
        private const val MOBILE_RESULT_JSON = "mobile-result.json"
        private const val MOBILE_RESULT_XML = "mobile-result.xml"
        private const val NUMBER_RETRIES = 3
        private const val DELAY_BEFORE_UPLOAD_AGAIN = 500L
    }
}
