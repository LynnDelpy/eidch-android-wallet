package ch.admin.foitt.openid4vc.domain.usecase.vcSdJwt

import androidx.annotation.CheckResult
import ch.admin.foitt.openid4vc.domain.model.VerifiableCredentialParams
import ch.admin.foitt.openid4vc.domain.model.credentialoffer.FetchCredentialError
import ch.admin.foitt.openid4vc.domain.model.credentialoffer.JWSKeyPair
import ch.admin.foitt.openid4vc.domain.model.jwt.Jwt
import ch.admin.foitt.openid4vc.domain.model.vcSdJwt.VcSdJwtCredential
import com.github.michaelbull.result.Result

internal interface FetchVcSdJwtCredential {
    @CheckResult
    suspend operator fun invoke(
        verifiableCredentialParams: VerifiableCredentialParams,
        keyPair: JWSKeyPair?,
        attestationJwt: Jwt?,
    ): Result<VcSdJwtCredential, FetchCredentialError>
}
