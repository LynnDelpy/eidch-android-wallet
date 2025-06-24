package ch.admin.foitt.wallet.platform.appAttestation.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class ClientAttestationRequest(
    @SerialName("integrity_token")
    val integrityToken: String,
    @SerialName("cnf")
    val cnf: Confirmation,
)
