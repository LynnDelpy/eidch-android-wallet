package ch.admin.foitt.wallet.platform.eIdApplicationProcess.domain.usecase.implementation

import ch.admin.foitt.wallet.platform.appAttestation.domain.repository.CurrentClientAttestationRepository
import ch.admin.foitt.wallet.platform.eIdApplicationProcess.domain.model.EIdRequestError
import ch.admin.foitt.wallet.platform.eIdApplicationProcess.domain.model.SIdRepositoryError
import ch.admin.foitt.wallet.platform.eIdApplicationProcess.domain.model.StateRequestError
import ch.admin.foitt.wallet.platform.eIdApplicationProcess.domain.model.StateResponse
import ch.admin.foitt.wallet.platform.eIdApplicationProcess.domain.model.toStateRequestError
import ch.admin.foitt.wallet.platform.eIdApplicationProcess.domain.repository.SIdRepository
import ch.admin.foitt.wallet.platform.eIdApplicationProcess.domain.usecase.FetchSIdStatus
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.coroutines.coroutineBinding
import com.github.michaelbull.result.mapError
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject

class FetchSIdStatusImpl @Inject constructor(
    private val sIdRepository: SIdRepository,
    private val currentClientAttestationRepository: CurrentClientAttestationRepository,
) : FetchSIdStatus {
    override suspend fun invoke(caseId: String): Result<StateResponse, StateRequestError> = coroutineBinding {
        val clientAttestation = currentClientAttestationRepository.getFlow().firstOrNull()
            ?: Err(EIdRequestError.Unexpected(IllegalStateException("client attestation flow was null"))).bind()

        sIdRepository.fetchSIdState(
            caseId = caseId,
            clientAttestation = clientAttestation,
        ).mapError(SIdRepositoryError::toStateRequestError).bind()
    }
}
