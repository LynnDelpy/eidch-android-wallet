package ch.admin.foitt.wallet.platform.appAttestation.domain.usecase

import ch.admin.foitt.openid4vc.domain.model.keyBinding.Jwk
import ch.admin.foitt.wallet.platform.appAttestation.domain.model.KeyAttestation
import ch.admin.foitt.wallet.platform.appAttestation.domain.model.KeyAttestationJwt
import ch.admin.foitt.wallet.platform.appAttestation.domain.model.ValidateKeyAttestationError
import com.github.michaelbull.result.Result

fun interface ValidateKeyAttestation {
    suspend operator fun invoke(
        keyStoreAlias: String,
        originalJwk: Jwk,
        keyAttestationJwt: KeyAttestationJwt,
    ): Result<KeyAttestation, ValidateKeyAttestationError>
}
