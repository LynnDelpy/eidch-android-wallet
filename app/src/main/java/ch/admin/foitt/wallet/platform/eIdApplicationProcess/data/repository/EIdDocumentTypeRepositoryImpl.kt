package ch.admin.foitt.wallet.platform.eIdApplicationProcess.data.repository

import ch.admin.foitt.wallet.platform.eIdApplicationProcess.domain.model.EIdDocumentType
import ch.admin.foitt.wallet.platform.eIdApplicationProcess.domain.repository.EidDocumentTypeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

class EIdDocumentTypeRepositoryImpl @Inject constructor() : EidDocumentTypeRepository {
    private val _documentType = MutableStateFlow(EIdDocumentType.IDENTITY_CARD)
    override val documentType = _documentType.asStateFlow()

    override fun setDocumentType(eIdDocumentType: EIdDocumentType) {
        _documentType.value = eIdDocumentType
    }
}
