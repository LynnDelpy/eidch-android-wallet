package ch.admin.foitt.wallet.platform.eIdApplicationProcess.domain.repository

import ch.admin.foitt.wallet.platform.eIdApplicationProcess.domain.model.EIdDocumentType
import kotlinx.coroutines.flow.StateFlow

interface EidDocumentTypeRepository {
    val documentType: StateFlow<EIdDocumentType>
    fun setDocumentType(eIdDocumentType: EIdDocumentType)
}
