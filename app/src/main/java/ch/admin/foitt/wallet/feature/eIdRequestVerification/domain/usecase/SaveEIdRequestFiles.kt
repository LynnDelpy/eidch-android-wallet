package ch.admin.foitt.wallet.feature.eIdRequestVerification.domain.usecase

import ch.admin.foitt.avwrapper.AVBeamFilesDataList
import ch.admin.foitt.wallet.feature.eIdRequestVerification.domain.model.SaveEIdRequestFileError
import com.github.michaelbull.result.Result

fun interface SaveEIdRequestFiles {
    suspend operator fun invoke(
        sIdCaseId: String,
        filesDataList: AVBeamFilesDataList?,
    ): Result<Unit, SaveEIdRequestFileError>
}
