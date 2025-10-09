package ch.admin.foitt.wallet.feature.eIdRequestVerification.domain.usecase.implementation

import ch.admin.foitt.avwrapper.AVBeamFileData
import ch.admin.foitt.avwrapper.AVBeamFilesDataList
import ch.admin.foitt.wallet.feature.eIdRequestVerification.domain.model.EIdRequestVerificationError
import ch.admin.foitt.wallet.feature.eIdRequestVerification.domain.model.SaveEIdRequestFileError
import ch.admin.foitt.wallet.feature.eIdRequestVerification.domain.model.toSaveEIdRequestFileError
import ch.admin.foitt.wallet.feature.eIdRequestVerification.domain.usecase.SaveEIdRequestFiles
import ch.admin.foitt.wallet.platform.database.domain.model.EIdRequestFile
import ch.admin.foitt.wallet.platform.database.domain.model.EIdRequestFileCategory
import ch.admin.foitt.wallet.platform.eIdApplicationProcess.domain.repository.EIdRequestFileRepository
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.coroutines.coroutineBinding
import com.github.michaelbull.result.mapError
import javax.inject.Inject

class SaveEIdRequestFilesImpl @Inject constructor(
    private val eIdRequestFileRepository: EIdRequestFileRepository,
) : SaveEIdRequestFiles {
    override suspend operator fun invoke(
        sIdCaseId: String,
        filesDataList: AVBeamFilesDataList?,
    ): Result<Unit, SaveEIdRequestFileError> = coroutineBinding {
        val files = filesDataList ?: Err<SaveEIdRequestFileError>(
            EIdRequestVerificationError.Unexpected(Exception("No files"))
        ).bind()

        val scanResultFiles = files.value.map { file ->
            file.toDBFile(sIdCaseId)
        }

        eIdRequestFileRepository.saveEIdRequestFiles(scanResultFiles)
            .mapError {
                it.toSaveEIdRequestFileError()
            }.bind()
    }

    private fun AVBeamFileData.toDBFile(eIdRequestCaseId: String): EIdRequestFile = EIdRequestFile(
        eIdRequestCaseId = eIdRequestCaseId,
        fileName = fileDescription,
        mime = fileType.toMimeType(),
        category = EIdRequestFileCategory.DOCUMENT_SCAN,
        data = fileData
    )
}
