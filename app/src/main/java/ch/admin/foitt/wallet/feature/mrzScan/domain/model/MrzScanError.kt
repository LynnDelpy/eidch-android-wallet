package ch.admin.foitt.wallet.feature.mrzScan.domain.model

import ch.admin.foitt.wallet.platform.eIdApplicationProcess.domain.model.EIdRequestCaseRepositoryError
import ch.admin.foitt.wallet.platform.eIdApplicationProcess.domain.model.EIdRequestError
import ch.admin.foitt.wallet.platform.eIdApplicationProcess.domain.model.EIdRequestStateRepositoryError
import timber.log.Timber

interface MrzScanError {
    data class Unexpected(val cause: Throwable?) :
        SaveEIdRequestCaseError,
        SaveEIdRequestStateError,
        GetImportantMrzKeysError
}

sealed interface SaveEIdRequestCaseError
sealed interface SaveEIdRequestStateError
sealed interface GetImportantMrzKeysError

internal fun EIdRequestCaseRepositoryError.toSaveEIdRequestCaseError(): SaveEIdRequestCaseError = when (this) {
    is EIdRequestError.Unexpected -> MrzScanError.Unexpected(cause)
}

internal fun EIdRequestStateRepositoryError.toSaveEIdRequestStateError(): SaveEIdRequestStateError = when (this) {
    is EIdRequestError.Unexpected -> MrzScanError.Unexpected(cause)
}

internal fun Throwable.toGetImportantMrzKeysError(message: String): GetImportantMrzKeysError {
    Timber.e(t = this, message = message)
    return MrzScanError.Unexpected(this)
}
