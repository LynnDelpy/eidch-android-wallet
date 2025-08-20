package ch.admin.foitt.wallet.platform.eIdApplicationProcess.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class MrzData(
    val displayName: String,
    val payload: ApplyRequest,
)
