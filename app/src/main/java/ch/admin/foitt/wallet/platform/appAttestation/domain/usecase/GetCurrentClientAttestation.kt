package ch.admin.foitt.wallet.platform.appAttestation.domain.usecase

import ch.admin.foitt.wallet.platform.appAttestation.domain.model.ClientAttestation
import kotlinx.coroutines.flow.Flow

interface GetCurrentClientAttestation {
    operator fun invoke(): Flow<ClientAttestation?>
}
