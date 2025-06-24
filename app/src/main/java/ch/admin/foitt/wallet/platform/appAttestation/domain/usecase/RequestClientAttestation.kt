package ch.admin.foitt.wallet.platform.appAttestation.domain.usecase

import ch.admin.foitt.openid4vc.domain.model.credentialoffer.metadata.SigningAlgorithm
import ch.admin.foitt.wallet.platform.appAttestation.domain.model.ClientAttestation
import ch.admin.foitt.wallet.platform.appAttestation.domain.model.RequestClientAttestationError
import com.github.michaelbull.result.Result

interface RequestClientAttestation {
    suspend operator fun invoke(
        signingAlgorithm: SigningAlgorithm = SigningAlgorithm.ES256,
    ): Result<ClientAttestation, RequestClientAttestationError>
}
