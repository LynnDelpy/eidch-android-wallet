package ch.admin.foitt.wallet.platform.eIdApplicationProcess.domain.usecase.implementation

import ch.admin.foitt.wallet.platform.appAttestation.domain.repository.CurrentClientAttestationRepository
import ch.admin.foitt.wallet.platform.eIdApplicationProcess.domain.model.EIdRequestError
import ch.admin.foitt.wallet.platform.eIdApplicationProcess.domain.model.GuardianVerificationError
import ch.admin.foitt.wallet.platform.eIdApplicationProcess.domain.model.GuardianVerificationResponse
import ch.admin.foitt.wallet.platform.eIdApplicationProcess.domain.model.SIdRepositoryError
import ch.admin.foitt.wallet.platform.eIdApplicationProcess.domain.model.toGuardianVerificationError
import ch.admin.foitt.wallet.platform.eIdApplicationProcess.domain.repository.SIdRepository
import ch.admin.foitt.wallet.platform.eIdApplicationProcess.domain.usecase.FetchGuardianVerification
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.coroutines.coroutineBinding
import com.github.michaelbull.result.mapError
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject

class FetchGuardianVerificationImpl @Inject constructor(
    private val sIdRepository: SIdRepository,
    private val currentClientAttestationRepository: CurrentClientAttestationRepository,
) : FetchGuardianVerification {
    override suspend operator fun invoke(
        caseId: String,
    ): Result<GuardianVerificationResponse, GuardianVerificationError> = coroutineBinding {
        val clientAttestation = currentClientAttestationRepository.getFlow().firstOrNull()
            ?: Err(EIdRequestError.Unexpected(IllegalStateException("client attestation flow was null"))).bind()

        sIdRepository.fetchSIdGuardianVerification(
            caseId = caseId,
            clientAttestation = clientAttestation,
        ).mapError(SIdRepositoryError::toGuardianVerificationError).bind()
    }
}
