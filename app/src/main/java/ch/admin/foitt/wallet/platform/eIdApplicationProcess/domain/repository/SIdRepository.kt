package ch.admin.foitt.wallet.platform.eIdApplicationProcess.domain.repository

import ch.admin.foitt.wallet.platform.appAttestation.domain.model.ClientAttestation
import ch.admin.foitt.wallet.platform.appAttestation.domain.model.KeyAttestation
import ch.admin.foitt.wallet.platform.eIdApplicationProcess.domain.model.ApplyRequest
import ch.admin.foitt.wallet.platform.eIdApplicationProcess.domain.model.CaseResponse
import ch.admin.foitt.wallet.platform.eIdApplicationProcess.domain.model.GuardianVerificationResponse
import ch.admin.foitt.wallet.platform.eIdApplicationProcess.domain.model.SIdRepositoryError
import ch.admin.foitt.wallet.platform.eIdApplicationProcess.domain.model.StateResponse
import ch.admin.foitt.wallet.platform.eIdApplicationProcess.domain.model.ValidateAttestationsError
import com.github.michaelbull.result.Result

interface SIdRepository {
    suspend fun fetchSIdCase(applyRequest: ApplyRequest): Result<CaseResponse, SIdRepositoryError>
    suspend fun fetchSIdState(caseId: String): Result<StateResponse, SIdRepositoryError>
    suspend fun fetchSIdGuardianVerification(caseId: String): Result<GuardianVerificationResponse, SIdRepositoryError>
    suspend fun validateAttestations(
        clientAttestation: ClientAttestation,
        keyAttestation: KeyAttestation,
    ): Result<Unit, ValidateAttestationsError>
}
