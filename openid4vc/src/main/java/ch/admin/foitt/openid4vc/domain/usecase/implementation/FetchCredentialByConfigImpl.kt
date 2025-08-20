package ch.admin.foitt.openid4vc.domain.usecase.implementation

import ch.admin.foitt.openid4vc.domain.model.VerifiableCredentialParams
import ch.admin.foitt.openid4vc.domain.model.anycredential.AnyCredential
import ch.admin.foitt.openid4vc.domain.model.credentialoffer.CredentialOfferError
import ch.admin.foitt.openid4vc.domain.model.credentialoffer.FetchCredentialByConfigError
import ch.admin.foitt.openid4vc.domain.model.credentialoffer.FetchCredentialError
import ch.admin.foitt.openid4vc.domain.model.credentialoffer.JWSKeyPair
import ch.admin.foitt.openid4vc.domain.model.credentialoffer.metadata.UnknownCredentialConfiguration
import ch.admin.foitt.openid4vc.domain.model.credentialoffer.metadata.VcSdJwtCredentialConfiguration
import ch.admin.foitt.openid4vc.domain.model.credentialoffer.toFetchCredentialByConfigError
import ch.admin.foitt.openid4vc.domain.model.jwt.Jwt
import ch.admin.foitt.openid4vc.domain.usecase.FetchCredentialByConfig
import ch.admin.foitt.openid4vc.domain.usecase.vcSdJwt.FetchVcSdJwtCredential
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.mapError
import javax.inject.Inject

internal class FetchCredentialByConfigImpl @Inject constructor(
    private val fetchVcSdJwtCredential: FetchVcSdJwtCredential,
) : FetchCredentialByConfig {
    override suspend fun invoke(
        verifiableCredentialParams: VerifiableCredentialParams,
        keyPair: JWSKeyPair?,
        attestationJwt: Jwt?,
    ): Result<AnyCredential, FetchCredentialByConfigError> =
        when (verifiableCredentialParams.credentialConfiguration) {
            is VcSdJwtCredentialConfiguration -> {
                fetchVcSdJwtCredential(
                    verifiableCredentialParams = verifiableCredentialParams,
                    keyPair = keyPair,
                    attestationJwt = attestationJwt,
                ).mapError(FetchCredentialError::toFetchCredentialByConfigError)
            }
            is UnknownCredentialConfiguration -> Err(CredentialOfferError.UnsupportedCredentialFormat)
        }
}
