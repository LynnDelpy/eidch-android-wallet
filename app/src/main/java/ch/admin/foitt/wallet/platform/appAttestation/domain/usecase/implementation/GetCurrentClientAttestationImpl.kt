package ch.admin.foitt.wallet.platform.appAttestation.domain.usecase.implementation

import ch.admin.foitt.wallet.platform.appAttestation.domain.model.ClientAttestation
import ch.admin.foitt.wallet.platform.appAttestation.domain.repository.CurrentClientAttestationRepository
import ch.admin.foitt.wallet.platform.appAttestation.domain.usecase.GetCurrentClientAttestation
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

internal class GetCurrentClientAttestationImpl @Inject constructor(
    private val currentClientAttestationRepository: CurrentClientAttestationRepository,
) : GetCurrentClientAttestation {
    override fun invoke(): Flow<ClientAttestation?> = currentClientAttestationRepository.getFlow()
}
