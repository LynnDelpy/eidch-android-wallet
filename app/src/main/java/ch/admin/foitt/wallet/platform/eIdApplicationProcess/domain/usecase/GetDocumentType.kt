package ch.admin.foitt.wallet.platform.eIdApplicationProcess.domain.usecase

import ch.admin.foitt.wallet.platform.eIdApplicationProcess.domain.model.EIdDocumentType
import kotlinx.coroutines.flow.StateFlow

fun interface GetDocumentType {
    operator fun invoke(): StateFlow<EIdDocumentType>
}
