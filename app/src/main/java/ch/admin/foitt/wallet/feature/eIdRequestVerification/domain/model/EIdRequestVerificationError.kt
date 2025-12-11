package ch.admin.foitt.wallet.feature.eIdRequestVerification.domain.model

import ch.admin.foitt.wallet.platform.eIdApplicationProcess.domain.model.EIdRequestCaseRepositoryError
import ch.admin.foitt.wallet.platform.eIdApplicationProcess.domain.model.EIdRequestError
import ch.admin.foitt.wallet.platform.eIdApplicationProcess.domain.model.EIdRequestFileRepositoryError
import ch.admin.foitt.wallet.platform.eIdApplicationProcess.domain.model.EIdRequestStateRepositoryError

interface EIdRequestVerificationError {
    data class Unexpected(val cause: Throwable?) :
        SaveEIdRequestCaseError,
        SaveEIdRequestStateError,
        GetImportantMrzKeysError,
        SaveEIdRequestFileError,
        GetDocumentScanDataError
}

sealed interface SaveEIdRequestCaseError
sealed interface SaveEIdRequestStateError
sealed interface GetImportantMrzKeysError
sealed interface SaveEIdRequestFileError
sealed interface GetDocumentScanDataError

internal fun EIdRequestCaseRepositoryError.toSaveEIdRequestCaseError(): SaveEIdRequestCaseError = when (this) {
    is EIdRequestError.Unexpected -> EIdRequestVerificationError.Unexpected(cause)
}

internal fun EIdRequestStateRepositoryError.toSaveEIdRequestStateError(): SaveEIdRequestStateError = when (this) {
    is EIdRequestError.Unexpected -> EIdRequestVerificationError.Unexpected(cause)
}

internal fun EIdRequestFileRepositoryError.toSaveEIdRequestFileError(): SaveEIdRequestFileError = when (this) {
    is EIdRequestError.Unexpected -> EIdRequestVerificationError.Unexpected(cause)
}

internal fun EIdRequestFileRepositoryError.toGetDocumentScanDataError(): GetDocumentScanDataError = when (this) {
    is EIdRequestError.Unexpected -> EIdRequestVerificationError.Unexpected(cause)
}
