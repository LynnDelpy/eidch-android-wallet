package ch.admin.foitt.openid4vc.domain.usecase

import androidx.annotation.CheckResult
import ch.admin.foitt.openid4vc.domain.model.credentialoffer.CredentialRequestProofJwt
import ch.admin.foitt.openid4vc.domain.model.credentialoffer.FetchVerifiableCredentialError
import ch.admin.foitt.openid4vc.domain.model.credentialoffer.JWSKeyPair
import ch.admin.foitt.openid4vc.domain.model.jwt.Jwt
import com.github.michaelbull.result.Result

internal interface CreateCredentialRequestProofJwt {
    @CheckResult
    suspend operator fun invoke(
        keyPair: JWSKeyPair,
        attestationJwt: Jwt?,
        issuer: String,
        cNonce: String?,
    ): Result<CredentialRequestProofJwt, FetchVerifiableCredentialError>
}
