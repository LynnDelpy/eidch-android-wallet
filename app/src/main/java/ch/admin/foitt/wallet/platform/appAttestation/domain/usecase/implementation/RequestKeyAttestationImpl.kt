package ch.admin.foitt.wallet.platform.appAttestation.domain.usecase.implementation

import ch.admin.foitt.openid4vc.domain.model.CreateJWSKeyPairError
import ch.admin.foitt.openid4vc.domain.model.CreateJwkError
import ch.admin.foitt.openid4vc.domain.model.credentialoffer.JWSKeyPair
import ch.admin.foitt.openid4vc.domain.model.credentialoffer.metadata.SigningAlgorithm
import ch.admin.foitt.openid4vc.domain.model.keyBinding.Jwk
import ch.admin.foitt.openid4vc.domain.usecase.CreateJWSKeyPair
import ch.admin.foitt.openid4vc.domain.usecase.CreateJwk
import ch.admin.foitt.openid4vc.utils.Constants
import ch.admin.foitt.wallet.platform.appAttestation.domain.model.AppAttestationRepositoryError
import ch.admin.foitt.wallet.platform.appAttestation.domain.model.KeyAttestation
import ch.admin.foitt.wallet.platform.appAttestation.domain.model.RequestKeyAttestationError
import ch.admin.foitt.wallet.platform.appAttestation.domain.model.ValidateKeyAttestationError
import ch.admin.foitt.wallet.platform.appAttestation.domain.model.toRequestKeyAttestationError
import ch.admin.foitt.wallet.platform.appAttestation.domain.repository.AppAttestationRepository
import ch.admin.foitt.wallet.platform.appAttestation.domain.usecase.RequestKeyAttestation
import ch.admin.foitt.wallet.platform.appAttestation.domain.usecase.ValidateKeyAttestation
import ch.admin.foitt.wallet.platform.appAttestation.domain.util.getBase64CertificateChain
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.coroutines.coroutineBinding
import com.github.michaelbull.result.mapError
import javax.inject.Inject

class RequestKeyAttestationImpl @Inject constructor(
    private val attestationRepository: AppAttestationRepository,
    private val createJWSKeyPair: CreateJWSKeyPair,
    private val createJwk: CreateJwk,
    private val validateKeyAttestation: ValidateKeyAttestation,
) : RequestKeyAttestation {
    override suspend operator fun invoke(
        signingAlgorithm: SigningAlgorithm,
    ): Result<KeyAttestation, RequestKeyAttestationError> = coroutineBinding {
        val challengeResponse = attestationRepository.fetchChallenge()
            .mapError(AppAttestationRepositoryError::toRequestKeyAttestationError)
            .bind()

        val keyPair: JWSKeyPair = createJWSKeyPair(
            signingAlgorithm = signingAlgorithm,
            provider = Constants.ANDROID_KEY_STORE,
            attestationChallenge = challengeResponse.challenge.encodeToByteArray(),
        ).mapError(CreateJWSKeyPairError::toRequestKeyAttestationError).bind()

        val keyJwkString = createJwk(
            keyPair = keyPair.keyPair,
            algorithm = keyPair.algorithm,
            asDid = false,
        ).mapError(CreateJwkError::toRequestKeyAttestationError).bind()

        val certificateChainBase64 = keyPair.getBase64CertificateChain()
            .mapError { error ->
                error.toRequestKeyAttestationError("RequestKeyAttestation certificate chain failed")
            }.bind()

        val keyJwk = Jwk.fromEcKey(keyJwkString, certificateChainBase64)
            .mapError { error ->
                error.toRequestKeyAttestationError("RequestKeyAttestation Jwk creation failed")
            }.bind()

        val attestationJwt = attestationRepository.fetchKeyAttestation(
            publicKey = keyJwk,
        ).mapError(AppAttestationRepositoryError::toRequestKeyAttestationError).bind()

        validateKeyAttestation(
            keyStoreAlias = keyPair.keyId,
            originalJwk = keyJwk,
            keyAttestationJwt = attestationJwt.keyAttestation,
        ).mapError(ValidateKeyAttestationError::toRequestKeyAttestationError).bind()
    }
}
