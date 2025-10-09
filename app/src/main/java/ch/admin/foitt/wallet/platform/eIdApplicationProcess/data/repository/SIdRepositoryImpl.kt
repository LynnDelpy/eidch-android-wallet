package ch.admin.foitt.wallet.platform.eIdApplicationProcess.data.repository

import ch.admin.foitt.wallet.platform.appAttestation.domain.model.ClientAttestation
import ch.admin.foitt.wallet.platform.appAttestation.domain.model.ClientAttestationPoP
import ch.admin.foitt.wallet.platform.appAttestation.domain.model.KeyAttestation
import ch.admin.foitt.wallet.platform.eIdApplicationProcess.domain.model.ApplyRequest
import ch.admin.foitt.wallet.platform.eIdApplicationProcess.domain.model.AttestationsValidationRequest
import ch.admin.foitt.wallet.platform.eIdApplicationProcess.domain.model.AutoVerificationResponse
import ch.admin.foitt.wallet.platform.eIdApplicationProcess.domain.model.CaseResponse
import ch.admin.foitt.wallet.platform.eIdApplicationProcess.domain.model.EIdStartAutoVerificationType
import ch.admin.foitt.wallet.platform.eIdApplicationProcess.domain.model.GuardianVerificationResponse
import ch.admin.foitt.wallet.platform.eIdApplicationProcess.domain.model.PairWalletResponse
import ch.admin.foitt.wallet.platform.eIdApplicationProcess.domain.model.SIdChallengeResponse
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
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import javax.inject.Inject

class SIdRepositoryImpl @Inject constructor(
    private val httpClient: HttpClient,
    private val environmentSetupRepo: EnvironmentSetupRepository,
) : SIdRepository {

    override suspend fun requestSIdCase(
        clientAttestation: ClientAttestation,
        clientAttestationPoP: ClientAttestationPoP,
        applyRequest: ApplyRequest,
    ): Result<CaseResponse, SIdRepositoryError> =
        runSuspendCatching<CaseResponse> {
            httpClient.post(environmentSetupRepo.sidBackendUrl + REST_API + "eid/apply") {
                header(ClientAttestation.REQUEST_HEADER, clientAttestation.attestation.rawJwt)
                header(ClientAttestationPoP.REQUEST_HEADER, clientAttestationPoP.value)
                contentType(ContentType.Application.Json)
                setBody(applyRequest)
            }.body()
        }.mapError { throwable ->
            throwable.toSIdRepositoryError("fetchSIdCase error")
        }

    override suspend fun fetchSIdState(
        caseId: String,
        clientAttestation: ClientAttestation,
    ): Result<StateResponse, SIdRepositoryError> =
        runSuspendCatching<StateResponse> {
            httpClient.get(environmentSetupRepo.sidBackendUrl + REST_API + "eid/$caseId/state") {
                header(ClientAttestation.REQUEST_HEADER, clientAttestation.attestation.rawJwt)
                contentType(ContentType.Application.Json)
            }.body()
        }.mapError { throwable ->
            throwable.toSIdRepositoryError("fetchSIdState error")
        }

    override suspend fun fetchSIdGuardianVerification(
        caseId: String,
        clientAttestation: ClientAttestation,
    ): Result<GuardianVerificationResponse, SIdRepositoryError> =
        runSuspendCatching<GuardianVerificationResponse> {
            httpClient.get(environmentSetupRepo.sidBackendUrl + REST_API + "eid/$caseId/legal-representant-verification") {
                header(ClientAttestation.REQUEST_HEADER, clientAttestation.attestation.rawJwt)
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
        httpClient.post(environmentSetupRepo.sidBackendUrl + REST_API + "attestations/validate") {
            contentType(ContentType.Application.Json)
            setBody(attestationsValidationRequest)
        }.body()
    }.mapError { throwable ->
        throwable.toValidateAttestationsError("validateAttestations error")
    }

    override suspend fun fetchChallenge() = runSuspendCatching<SIdChallengeResponse> {
        httpClient.get(environmentSetupRepo.sidBackendUrl + REST_API + "eid/challenge") {
            contentType(ContentType.Application.Json)
        }.body()
    }.mapError { throwable ->
        throwable.toSIdRepositoryError("fetchChallenge error")
    }

    override suspend fun startOnlineSession(
        caseId: String,
        clientAttestation: ClientAttestation,
        clientAttestationPoP: ClientAttestationPoP,
    ) = runSuspendCatching<Unit> {
        httpClient.put(environmentSetupRepo.sidBackendUrl + REST_API + "eid/$caseId/start-online-session") {
            header(ClientAttestation.REQUEST_HEADER, clientAttestation.attestation.rawJwt)
            header(ClientAttestationPoP.REQUEST_HEADER, clientAttestationPoP.value)
        }.body()
    }.mapError { throwable ->
        throwable.toSIdRepositoryError("startOnlineSession error")
    }

    override suspend fun pairWallet(
        caseId: String,
        clientAttestation: ClientAttestation,
        clientAttestationPoP: ClientAttestationPoP,
    ) = runSuspendCatching<PairWalletResponse> {
        httpClient.put(environmentSetupRepo.sidBackendUrl + REST_API + "eid/$caseId/pair-wallet") {
            header(ClientAttestation.REQUEST_HEADER, clientAttestation.attestation.rawJwt)
            header(ClientAttestationPoP.REQUEST_HEADER, clientAttestationPoP.value)
        }.body()
    }.mapError { throwable ->
        throwable.toSIdRepositoryError("pairWallet error")
    }

    override suspend fun startAutoVerification(
        caseId: String,
        autoVerificationType: EIdStartAutoVerificationType,
        clientAttestation: ClientAttestation,
        clientAttestationPoP: ClientAttestationPoP
    ): Result<AutoVerificationResponse, SIdRepositoryError> =
        runSuspendCatching<AutoVerificationResponse> {
            httpClient.put(
                environmentSetupRepo.sidBackendUrl + REST_API +
                    "eid/$caseId/start-auto-verification/$autoVerificationType"
            ) {
                header(ClientAttestation.REQUEST_HEADER, clientAttestation.attestation.rawJwt)
                header(ClientAttestationPoP.REQUEST_HEADER, clientAttestationPoP.value)
            }.body()
        }.mapError { throwable ->
            throwable.toSIdRepositoryError("startAutoVerification error")
        }

    companion object {
        private const val REST_API = "api/rest/"
    }
}
