package ch.admin.foitt.wallet.platform.appAttestation.domain.usecase.implementation

import ch.admin.foitt.openid4vc.domain.model.CreateJWSKeyPairError
import ch.admin.foitt.openid4vc.domain.model.CreateJwkError
import ch.admin.foitt.openid4vc.domain.model.credentialoffer.JWSKeyPair
import ch.admin.foitt.openid4vc.domain.model.credentialoffer.metadata.SigningAlgorithm
import ch.admin.foitt.openid4vc.domain.model.credentialoffer.metadata.toSignatureName
import ch.admin.foitt.openid4vc.domain.model.keyBinding.Jwk
import ch.admin.foitt.openid4vc.domain.usecase.CreateJWSKeyPair
import ch.admin.foitt.openid4vc.domain.usecase.CreateJwk
import ch.admin.foitt.openid4vc.utils.Constants
import ch.admin.foitt.wallet.platform.appAttestation.domain.model.AppAttestationRepositoryError
import ch.admin.foitt.wallet.platform.appAttestation.domain.model.AppIntegrityRepositoryError
import ch.admin.foitt.wallet.platform.appAttestation.domain.model.ClientAttestation
import ch.admin.foitt.wallet.platform.appAttestation.domain.model.RequestClientAttestationError
import ch.admin.foitt.wallet.platform.appAttestation.domain.model.ValidateClientAttestationError
import ch.admin.foitt.wallet.platform.appAttestation.domain.model.toRequestClientAttestationError
import ch.admin.foitt.wallet.platform.appAttestation.domain.repository.AppAttestationRepository
import ch.admin.foitt.wallet.platform.appAttestation.domain.repository.AppIntegrityRepository
import ch.admin.foitt.wallet.platform.appAttestation.domain.usecase.RequestClientAttestation
import ch.admin.foitt.wallet.platform.appAttestation.domain.usecase.ValidateClientAttestation
import ch.admin.foitt.wallet.platform.appAttestation.domain.util.getBase64CertificateChain
import ch.admin.foitt.wallet.platform.utils.toBase64StringUrlEncodedWithoutPadding
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.coroutines.coroutineBinding
import com.github.michaelbull.result.coroutines.runSuspendCatching
import com.github.michaelbull.result.mapError
import java.security.KeyPair
import java.security.Signature
import javax.inject.Inject

class RequestClientAttestationImpl @Inject constructor(
    private val appAttestationRepository: AppAttestationRepository,
    private val appIntegrityRepository: AppIntegrityRepository,
    private val validateClientAttestation: ValidateClientAttestation,
    private val createJWSKeyPair: CreateJWSKeyPair,
    private val createJwk: CreateJwk,
) : RequestClientAttestation {
    override suspend operator fun invoke(
        signingAlgorithm: SigningAlgorithm,
    ): Result<ClientAttestation, RequestClientAttestationError> = coroutineBinding {
        val challengeResponse = appAttestationRepository
            .fetchChallenge()
            .mapError(AppAttestationRepositoryError::toRequestClientAttestationError)
            .bind()

        val keyPair: JWSKeyPair = createJWSKeyPair(
            signingAlgorithm = signingAlgorithm,
            provider = Constants.ANDROID_KEY_STORE,
            attestationChallenge = challengeResponse.challenge.encodeToByteArray(),
        ).mapError(CreateJWSKeyPairError::toRequestClientAttestationError).bind()

        val keyJwkString = createJwk(
            keyPair = keyPair.keyPair,
            algorithm = keyPair.algorithm,
            asDid = false,
        ).mapError(CreateJwkError::toRequestClientAttestationError).bind()

        val certificateChainBase64 = keyPair.getBase64CertificateChain()
            .mapError { throwable ->
                throwable.toRequestClientAttestationError("RequestClientAttestation certificate chain failed")
            }.bind()

        val keyJwk = Jwk.fromEcKey(keyJwkString, certificateChainBase64)
            .mapError { throwable ->
                throwable.toRequestClientAttestationError("RequestClientAttestation Jwk creation failed")
            }.bind()

        val signedChallengeBase64 = signData(
            data = challengeResponse.challenge,
            keyPair = keyPair.keyPair,
            signingAlgorithm = signingAlgorithm,
        ).mapError { throwable ->
            throwable.toRequestClientAttestationError("RequestClientAttestation challenge signature failed")
        }.bind()

        val integrityTokenResponse = appIntegrityRepository.fetchIntegrityToken(signedChallengeBase64)
            .mapError(AppIntegrityRepositoryError::toRequestClientAttestationError).bind()

        val clientAttestationJwt = appAttestationRepository.fetchClientAttestation(
            integrityToken = integrityTokenResponse,
            publicKey = keyJwk,
        ).mapError(AppAttestationRepositoryError::toRequestClientAttestationError).bind()

        val clientAttestation = validateClientAttestation(
            keyStoreAlias = keyPair.keyId,
            originalJwk = keyJwk,
            clientAttestationResponse = clientAttestationJwt,
        ).mapError(ValidateClientAttestationError::toRequestClientAttestationError).bind()

        appAttestationRepository.saveClientAttestation(clientAttestation)
            .mapError(AppAttestationRepositoryError::toRequestClientAttestationError).bind()

        clientAttestation
    }

    private fun signData(
        data: String,
        keyPair: KeyPair,
        signingAlgorithm: SigningAlgorithm,
    ): Result<String, Throwable> = runSuspendCatching {
        val challengeBytes = data.encodeToByteArray()
        val signatureInstance = Signature.getInstance(signingAlgorithm.toSignatureName()).apply {
            initSign(keyPair.private)
            update(challengeBytes)
        }
        val signature = signatureInstance.sign()
        signature.toBase64StringUrlEncodedWithoutPadding()
    }
}
