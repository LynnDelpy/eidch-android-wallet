package ch.admin.foitt.wallet.platform.eIdApplicationProcess.data.repository

import ch.admin.foitt.wallet.platform.appAttestation.domain.model.ClientAttestation
import ch.admin.foitt.wallet.platform.appAttestation.domain.model.KeyAttestation
import ch.admin.foitt.wallet.platform.eIdApplicationProcess.domain.model.ApplyRequest
import ch.admin.foitt.wallet.platform.eIdApplicationProcess.domain.model.AttestationsValidationRequest
import ch.admin.foitt.wallet.platform.eIdApplicationProcess.domain.model.CaseResponse
import ch.admin.foitt.wallet.platform.eIdApplicationProcess.domain.model.GuardianVerificationResponse
import ch.admin.foitt.wallet.platform.eIdApplicationProcess.domain.model.SIdRepositoryError
import ch.admin.foitt.wallet.platform.eIdApplicationProcess.domain.model.StateResponse
import ch.admin.foitt.wallet.platform.eIdApplicationProcess.domain.model.toSIdRepositoryError
import ch.admin.foitt.wallet.platform.eIdApplicationProcess.domain.model.toValidateAttestationsError
import ch.admin.foitt.wallet.platform.eIdApplicationProcess.domain.repository.SIdRepository
import ch.admin.foitt.wallet.platform.environmentSetup.domain.repository.EnvironmentSetupRepository
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.coroutines.runSuspendCatching
import com.github.michaelbull.result.mapError
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import javax.inject.Inject

class SIdRepositoryImpl @Inject constructor(
    private val httpClient: HttpClient,
    private val environmentSetupRepo: EnvironmentSetupRepository,
) : SIdRepository {
    override suspend fun fetchSIdCase(applyRequest: ApplyRequest): Result<CaseResponse, SIdRepositoryError> =
        runSuspendCatching<CaseResponse> {
            httpClient.post(environmentSetupRepo.sidBackendUrl + "/eid/apply") {
                contentType(ContentType.Application.Json)
                setBody(applyRequest)
            }.body()
        }.mapError { throwable ->
            throwable.toSIdRepositoryError("fetchSIdCase error")
        }

    override suspend fun fetchSIdState(caseId: String): Result<StateResponse, SIdRepositoryError> =
        runSuspendCatching<StateResponse> {
            httpClient.get(environmentSetupRepo.sidBackendUrl + "/eid/$caseId/state") {
                contentType(ContentType.Application.Json)
            }.body()
        }.mapError { throwable ->
            throwable.toSIdRepositoryError("fetchSIdState error")
        }

    override suspend fun fetchSIdGuardianVerification(caseId: String): Result<GuardianVerificationResponse, SIdRepositoryError> =
        runSuspendCatching<GuardianVerificationResponse> {
            httpClient.get(environmentSetupRepo.sidBackendUrl + "/eid/$caseId/legal-representant-verification") {
                contentType(ContentType.Application.Json)
            }.body()
        }.mapError { throwable ->
            throwable.toSIdRepositoryError("fetchSIdGuardianVerification error")
        }

    override suspend fun validateAttestations(
        clientAttestation: ClientAttestation,
        keyAttestation: KeyAttestation,
    ) = runSuspendCatching<Unit> {
        val attestationsValidationRequest = AttestationsValidationRequest(
            clientAttestation = clientAttestation.attestation.rawJwt,
            keyAttestation = keyAttestation.attestation.rawJwt
        )
        httpClient.post(environmentSetupRepo.sidBackendUrl + "/attestations/validate") {
            contentType(ContentType.Application.Json)
            setBody(attestationsValidationRequest)
        }.body()
    }.mapError { throwable ->
        throwable.toValidateAttestationsError("validateAttestations error")
    }
}
