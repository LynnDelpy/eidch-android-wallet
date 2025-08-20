package ch.admin.foitt.openid4vc.domain.usecase.vcSdJwt.implementation

import ch.admin.foitt.openid4vc.domain.model.VerifiableCredentialParams
import ch.admin.foitt.openid4vc.domain.model.credentialoffer.CredentialOfferError
import ch.admin.foitt.openid4vc.domain.model.credentialoffer.FetchCredentialError
import ch.admin.foitt.openid4vc.domain.model.credentialoffer.FetchVerifiableCredentialError
import ch.admin.foitt.openid4vc.domain.model.credentialoffer.JWSKeyPair
import ch.admin.foitt.openid4vc.domain.model.credentialoffer.toFetchCredentialError
import ch.admin.foitt.openid4vc.domain.model.jwt.Jwt
import ch.admin.foitt.openid4vc.domain.model.vcSdJwt.VcSdJwtCredential
import ch.admin.foitt.openid4vc.domain.model.vcSdJwt.VerifyJwtError
import ch.admin.foitt.openid4vc.domain.usecase.FetchVerifiableCredential
import ch.admin.foitt.openid4vc.domain.usecase.VerifyJwtSignature
import ch.admin.foitt.openid4vc.domain.usecase.vcSdJwt.FetchVcSdJwtCredential
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.coroutines.coroutineBinding
import com.github.michaelbull.result.coroutines.runSuspendCatching
import com.github.michaelbull.result.mapError
import javax.inject.Inject

internal class FetchVcSdJwtCredentialImpl @Inject constructor(
    private val fetchVerifiableCredential: FetchVerifiableCredential,
    private val verifyJwtSignature: VerifyJwtSignature,
) : FetchVcSdJwtCredential {
    override suspend fun invoke(
        verifiableCredentialParams: VerifiableCredentialParams,
        keyPair: JWSKeyPair?,
        attestationJwt: Jwt?,
    ): Result<VcSdJwtCredential, FetchCredentialError> = coroutineBinding {
        val verifiableCredential = fetchVerifiableCredential(
            verifiableCredentialParams = verifiableCredentialParams,
            keyPair = keyPair,
            attestationJwt = attestationJwt,
        ).mapError(FetchVerifiableCredentialError::toFetchCredentialError)
            .bind()

        val credential = runSuspendCatching {
            VcSdJwtCredential(
                keyBinding = verifiableCredential.keyBinding,
                payload = verifiableCredential.credential,
            )
        }.mapError { throwable ->
            throwable.toFetchCredentialError("FetchVcSdJwtCredential credential creation error")
        }.bind()

        if (credential.hasNonDisclosableClaims()) {
            Err(CredentialOfferError.InvalidCredentialOffer).bind<FetchCredentialError>()
        }

        val issuerDid = runSuspendCatching {
            credential.issuer
        }.mapError { throwable ->
            throwable.toFetchCredentialError("FetchVcSdJwtCredential credential issuer error")
        }.bind()
        val keyId = runSuspendCatching {
            credential.kid
        }.mapError { throwable ->
            throwable.toFetchCredentialError("FetchVcSdJwtCredential credential kid error")
        }.bind()

        verifyJwtSignature(
            did = issuerDid,
            kid = keyId,
            jwt = credential,
        ).mapError(VerifyJwtError::toFetchCredentialError)
            .bind()

        credential
    }
}
