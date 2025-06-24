package ch.admin.foitt.wallet.platform.appAttestation.data.repository

import ch.admin.foitt.openid4vc.domain.model.keyBinding.Jwk
import ch.admin.foitt.wallet.platform.appAttestation.domain.model.AppAttestationRepositoryError
import ch.admin.foitt.wallet.platform.appAttestation.domain.model.AttestationChallengeResponse
import ch.admin.foitt.wallet.platform.appAttestation.domain.model.AttestationError
import ch.admin.foitt.wallet.platform.appAttestation.domain.model.ClientAttestation
import ch.admin.foitt.wallet.platform.appAttestation.domain.model.ClientAttestationRequest
import ch.admin.foitt.wallet.platform.appAttestation.domain.model.ClientAttestationResponse
import ch.admin.foitt.wallet.platform.appAttestation.domain.model.Confirmation
import ch.admin.foitt.wallet.platform.appAttestation.domain.model.IntegrityToken
import ch.admin.foitt.wallet.platform.appAttestation.domain.model.KeyAttestationRequest
import ch.admin.foitt.wallet.platform.appAttestation.domain.model.KeyAttestationResponse
import ch.admin.foitt.wallet.platform.appAttestation.domain.model.toAppAttestationRepositoryError
import ch.admin.foitt.wallet.platform.appAttestation.domain.repository.AppAttestationRepository
import ch.admin.foitt.wallet.platform.database.data.dao.ClientAttestationDao
import ch.admin.foitt.wallet.platform.database.data.dao.DaoProvider
import ch.admin.foitt.wallet.platform.environmentSetup.domain.repository.EnvironmentSetupRepository
import ch.admin.foitt.wallet.platform.utils.suspendUntilNonNull
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
import ch.admin.foitt.wallet.platform.database.domain.model.ClientAttestation as DBClientAttestation

class AppAttestationRepositoryImpl @Inject constructor(
    private val httpClient: HttpClient,
    private val environmentSetupRepo: EnvironmentSetupRepository,
    daoProvider: DaoProvider,
) : AppAttestationRepository {

    override suspend fun fetchChallenge() = runSuspendCatching<AttestationChallengeResponse> {
        httpClient.get(environmentSetupRepo.attestationsServiceUrl + "/api/attestations/challenge") {
            contentType(ContentType.Application.Json)
        }.body()
    }.mapError { throwable ->
        AttestationError.Unexpected(throwable)
    }

    override suspend fun fetchClientAttestation(
        integrityToken: IntegrityToken,
        publicKey: Jwk,
    ): Result<ClientAttestationResponse, AppAttestationRepositoryError> = runSuspendCatching<ClientAttestationResponse> {
        val body = ClientAttestationRequest(
            integrityToken = integrityToken.value,
            cnf = Confirmation(publicKey),
        )
        httpClient.post(environmentSetupRepo.attestationsServiceUrl + "/api/attestations/android/client-attestations") {
            contentType(ContentType.Application.Json)
            setBody(body)
        }.body()
    }.mapError { throwable ->
        AttestationError.Unexpected(throwable)
    }

    override suspend fun saveClientAttestation(
        clientAttestation: ClientAttestation,
    ) = runSuspendCatching {
        val dbClientAttestation = DBClientAttestation(
            id = clientAttestation.keyStoreAlias,
            attestation = clientAttestation.attestation.rawJwt
        )
        dao().insert(dbClientAttestation)
    }.mapError { throwable ->
        AttestationError.Unexpected(throwable)
    }

    override suspend fun fetchKeyAttestation(publicKey: Jwk) = runSuspendCatching<KeyAttestationResponse> {
        val body = KeyAttestationRequest(
            cnf = Confirmation(publicKey),
        )

        httpClient.post(environmentSetupRepo.attestationsServiceUrl + "/api/attestations/android/key-attestations") {
            contentType(ContentType.Application.Json)
            setBody(body)
        }.body()
    }.mapError { throwable ->
        throwable.toAppAttestationRepositoryError("Fetch key attestation failed")
    }

    private suspend fun dao(): ClientAttestationDao = suspendUntilNonNull { daoFlow.value }
    private val daoFlow = daoProvider.clientAttestationDaoFlow
}
