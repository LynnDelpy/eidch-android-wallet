package ch.admin.foitt.wallet.platform.appAttestation.domain.model

import ch.admin.foitt.openid4vc.domain.model.jwt.Jwt

data class ClientAttestation(
    val keyStoreAlias: String,
    val attestation: Jwt,
)
