package ch.admin.foitt.openid4vc.domain.model

import ch.admin.foitt.openid4vc.domain.model.credentialoffer.Grant
import ch.admin.foitt.openid4vc.domain.model.credentialoffer.metadata.AnyCredentialConfiguration
import ch.admin.foitt.openid4vc.domain.model.credentialoffer.metadata.ProofTypeConfig

data class VerifiableCredentialParams(
    val proofTypeConfig: ProofTypeConfig?,
    val tokenEndpoint: String,
    val grants: Grant,
    val issuerEndpoint: String,
    val credentialEndpoint: String,
    val credentialConfiguration: AnyCredentialConfiguration,
)
