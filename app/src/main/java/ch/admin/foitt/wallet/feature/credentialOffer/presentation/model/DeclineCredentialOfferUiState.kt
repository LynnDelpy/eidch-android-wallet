package ch.admin.foitt.wallet.feature.credentialOffer.presentation.model

import ch.admin.foitt.wallet.platform.actorMetadata.domain.model.ActorType
import ch.admin.foitt.wallet.platform.actorMetadata.presentation.model.ActorUiState
import ch.admin.foitt.wallet.platform.nonCompliance.domain.model.NonComplianceState
import ch.admin.foitt.wallet.platform.trustRegistry.domain.model.TrustStatus
import ch.admin.foitt.wallet.platform.trustRegistry.domain.model.VcSchemaTrustStatus

data class DeclineCredentialOfferUiState(
    val issuer: ActorUiState,
) {
    companion object {
        val EMPTY = DeclineCredentialOfferUiState(
            issuer = ActorUiState(
                name = "",
                painter = null,
                trustStatus = TrustStatus.UNKNOWN,
                vcSchemaTrustStatus = VcSchemaTrustStatus.UNPROTECTED,
                actorType = ActorType.ISSUER,
                nonComplianceState = NonComplianceState.UNKNOWN,
                nonComplianceReason = null,
            ),
        )
    }
}
