package ch.admin.foitt.wallet.platform.holderBinding.domain.model

import ch.admin.foitt.openid4vc.domain.model.credentialoffer.JWSKeyPair
import ch.admin.foitt.openid4vc.domain.model.jwt.Jwt

data class BindingKeyPair(
    val keyPair: JWSKeyPair,
    val attestationJwt: Jwt?,
)
