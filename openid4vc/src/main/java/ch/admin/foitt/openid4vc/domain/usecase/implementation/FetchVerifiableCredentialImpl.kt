package ch.admin.foitt.openid4vc.domain.usecase.implementation

import ch.admin.foitt.openid4vc.domain.model.VerifiableCredential
import ch.admin.foitt.openid4vc.domain.model.VerifiableCredentialParams
import ch.admin.foitt.openid4vc.domain.model.credentialoffer.CredentialOfferError
import ch.admin.foitt.openid4vc.domain.model.credentialoffer.Grant
import ch.admin.foitt.openid4vc.domain.model.credentialoffer.JWSKeyPair
import ch.admin.foitt.openid4vc.domain.model.jwt.Jwt
import ch.admin.foitt.openid4vc.domain.model.keyBinding.KeyBinding
import ch.admin.foitt.openid4vc.domain.model.keyBinding.KeyBindingType
import ch.admin.foitt.openid4vc.domain.repository.CredentialOfferRepository
import ch.admin.foitt.openid4vc.domain.usecase.CreateCredentialRequestProofJwt
import ch.admin.foitt.openid4vc.domain.usecase.DeleteKeyPair
import ch.admin.foitt.openid4vc.domain.usecase.FetchVerifiableCredential
import ch.admin.foitt.openid4vc.utils.retryUseCase
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.coroutines.coroutineBinding
import com.github.michaelbull.result.onFailure
import javax.inject.Inject

internal class FetchVerifiableCredentialImpl @Inject constructor(
    private val credentialOfferRepository: CredentialOfferRepository,
    private val createCredentialRequestProofJwt: CreateCredentialRequestProofJwt,
    private val deleteKeyPair: DeleteKeyPair,
) : FetchVerifiableCredential {
    override suspend fun invoke(
        verifiableCredentialParams: VerifiableCredentialParams,
        keyPair: JWSKeyPair?,
        attestationJwt: Jwt?,
    ) = coroutineBinding {
        val tokenResponse = getToken(verifiableCredentialParams.tokenEndpoint, verifiableCredentialParams.grants)
            .onFailure {
                if (keyPair != null && keyPair.bindingType == KeyBindingType.HARDWARE) {
                    deleteKeyPair(keyPair.keyId)
                }
            }
            .bind()

        val credentialRequestJwt = getCredentialRequestProofJwt(
            keyPair = keyPair,
            attestationJwt = attestationJwt,
            issuerEndpoint = verifiableCredentialParams.issuerEndpoint,
            cNonce = tokenResponse.cNonce
        ).bind()

        val credentialResponse = credentialOfferRepository.fetchCredential(
            issuerEndpoint = verifiableCredentialParams.credentialEndpoint,
            credentialConfiguration = verifiableCredentialParams.credentialConfiguration,
            proof = credentialRequestJwt,
            accessToken = tokenResponse.accessToken,
        )
            .onFailure {
                if (keyPair != null && keyPair.bindingType == KeyBindingType.HARDWARE) {
                    deleteKeyPair(keyPair.keyId)
                }
            }
            .bind()

        VerifiableCredential(
            credential = credentialResponse.credential,
            format = verifiableCredentialParams.credentialConfiguration.format,
            keyBinding = getKeyBinding(keyPair),
        )
    }

    private suspend fun getToken(tokenEndpoint: String, grant: Grant) =
        if (grant.preAuthorizedCode != null) {
            credentialOfferRepository.fetchAccessToken(
                tokenEndpoint,
                grant.preAuthorizedCode.preAuthorizedCode
            )
        } else {
            Err(CredentialOfferError.UnsupportedGrantType)
        }

    private suspend fun getCredentialRequestProofJwt(
        keyPair: JWSKeyPair?,
        attestationJwt: Jwt?,
        issuerEndpoint: String,
        cNonce: String?
    ) = coroutineBinding {
        val credentialRequestJwt = if (keyPair != null) {
            retryUseCase {
                createCredentialRequestProofJwt(
                    keyPair = keyPair,
                    attestationJwt = attestationJwt,
                    issuer = issuerEndpoint,
                    cNonce = cNonce
                )
            }.onFailure {
                if (keyPair.bindingType == KeyBindingType.HARDWARE) {
                    deleteKeyPair(keyPair.keyId)
                }
            }.bind()
        } else {
            null
        }

        credentialRequestJwt
    }

    private fun getKeyBinding(
        keyPair: JWSKeyPair?,
    ): KeyBinding? = if (keyPair != null) {
        val publicKey = if (keyPair.bindingType == KeyBindingType.SOFTWARE) {
            keyPair.keyPair.public.encoded
        } else {
            null
        }

        val privateKey = if (keyPair.bindingType == KeyBindingType.SOFTWARE) {
            keyPair.keyPair.private.encoded
        } else {
            null
        }

        KeyBinding(
            identifier = keyPair.keyId,
            algorithm = keyPair.algorithm,
            bindingType = keyPair.bindingType,
            publicKey = publicKey,
            privateKey = privateKey
        )
    } else {
        null
    }
}
