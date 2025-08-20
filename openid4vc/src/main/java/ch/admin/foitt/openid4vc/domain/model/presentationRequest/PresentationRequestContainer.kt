package ch.admin.foitt.openid4vc.domain.model.presentationRequest

import ch.admin.foitt.openid4vc.domain.model.Invitation
import kotlinx.serialization.json.JsonElement

sealed interface PresentationRequestContainer : Invitation {
    val clientId: String?

    class Json(
        val json: JsonElement,
        override val clientId: String? = null
    ) : PresentationRequestContainer

    class Jwt(
        override val clientId: String? = null,
        val jwt: ch.admin.foitt.openid4vc.domain.model.jwt.Jwt,
    ) : PresentationRequestContainer
}
