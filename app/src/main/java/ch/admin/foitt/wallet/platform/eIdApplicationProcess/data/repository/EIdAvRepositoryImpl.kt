package ch.admin.foitt.wallet.platform.eIdApplicationProcess.data.repository

import ch.admin.foitt.wallet.platform.eIdApplicationProcess.domain.model.AvRepositoryError
import ch.admin.foitt.wallet.platform.eIdApplicationProcess.domain.model.UploadFileRequest
import ch.admin.foitt.wallet.platform.eIdApplicationProcess.domain.model.toAvRepositoryError
import ch.admin.foitt.wallet.platform.eIdApplicationProcess.domain.repository.EIdAvRepository
import ch.admin.foitt.wallet.platform.environmentSetup.domain.repository.EnvironmentSetupRepository
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.coroutines.runSuspendCatching
import com.github.michaelbull.result.mapError
import io.ktor.client.HttpClient
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentDisposition
import io.ktor.http.contentLength
import io.ktor.http.contentType
import javax.inject.Inject

class EIdAvRepositoryImpl @Inject constructor(
    private val httpClient: HttpClient,
    private val environmentSetupRepository: EnvironmentSetupRepository
) : EIdAvRepository {
    override suspend fun uploadFileToCase(
        file: UploadFileRequest
    ): Result<Unit, AvRepositoryError> = runSuspendCatching<Unit> {
        httpClient.post(environmentSetupRepository.avBackendUrl + "cases/v1/$file.caseId/files") {
            header("Authorization", "BEARER $file.accessToken")
            header(ContentDisposition.Attachment.disposition, "fileName=$file.fileName")
            contentLength()
            contentType(file.mime)
            setBody(file.document)
        }
    }.mapError { throwable ->
        throwable.toAvRepositoryError("uploadFileToCase error")
    }
}
