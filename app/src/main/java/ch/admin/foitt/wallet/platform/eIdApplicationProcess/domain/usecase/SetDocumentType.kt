package ch.admin.foitt.wallet.platform.eIdApplicationProcess.domain.usecase

import ch.admin.foitt.wallet.platform.eIdApplicationProcess.domain.model.EIdDocumentType

fun interface SetDocumentType {
    operator fun invoke(
        eIdDocumentType: EIdDocumentType,
    )
}
