package ch.admin.foitt.wallet.platform.appAttestation.domain.usecase

import androidx.annotation.CheckResult
import ch.admin.foitt.openid4vc.domain.model.credentialoffer.metadata.SigningAlgorithm
import ch.admin.foitt.wallet.platform.appAttestation.domain.model.KeyAttestation
import ch.admin.foitt.wallet.platform.appAttestation.domain.model.RequestKeyAttestationError
import com.github.michaelbull.result.Result

interface RequestKeyAttestation {
    @CheckResult
    suspend operator fun invoke(
        signingAlgorithm: SigningAlgorithm = SigningAlgorithm.ES256,
    ): Result<KeyAttestation, RequestKeyAttestationError>
}
