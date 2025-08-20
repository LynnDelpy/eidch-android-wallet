package ch.admin.foitt.openid4vc.domain.usecase

import ch.admin.foitt.openid4vc.domain.model.VerifiableCredentialParams
import ch.admin.foitt.openid4vc.domain.model.anycredential.AnyCredential
import ch.admin.foitt.openid4vc.domain.model.credentialoffer.FetchCredentialByConfigError
import ch.admin.foitt.openid4vc.domain.model.credentialoffer.JWSKeyPair
import ch.admin.foitt.openid4vc.domain.model.jwt.Jwt
import com.github.michaelbull.result.Result

fun interface FetchCredentialByConfig {
    suspend operator fun invoke(
        verifiableCredentialParams: VerifiableCredentialParams,
        keyPair: JWSKeyPair?,
        attestationJwt: Jwt?,
    ): Result<AnyCredential, FetchCredentialByConfigError>
}
