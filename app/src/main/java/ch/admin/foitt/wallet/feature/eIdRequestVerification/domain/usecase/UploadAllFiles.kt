package ch.admin.foitt.wallet.feature.eIdRequestVerification.domain.usecase

import ch.admin.foitt.wallet.platform.eIdApplicationProcess.domain.model.AvUploadFilesError
import com.github.michaelbull.result.Result

interface UploadAllFiles {
    suspend operator fun invoke(caseId: String, accessToken: String): Result<Unit, AvUploadFilesError>
}
