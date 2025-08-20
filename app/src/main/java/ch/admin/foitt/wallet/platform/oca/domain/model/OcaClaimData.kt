package ch.admin.foitt.wallet.platform.oca.domain.model

import ch.admin.foitt.wallet.platform.oca.domain.model.overlays.CharacterEncoding
import ch.admin.foitt.wallet.platform.oca.domain.model.overlays.Standard
import kotlinx.serialization.Serializable

/**
 * OcaClaimData, containing data from capture base and overlays that is used for displaying claims
 */
@Serializable
data class OcaClaimData(
    // capture base
    val attributeType: AttributeType,
    val captureBaseDigest: String,
    val flagged: Boolean = false,
    val name: String,
    // overlays
    val characterEncoding: CharacterEncoding? = null,
    val dataSources: Map<DataSourceFormat, JsonPath> = emptyMap(),
    val entryMappings: Map<Locale, Map<EntryCode, String>> = emptyMap(),
    val format: String? = null,
    val labels: Map<Locale, String> = emptyMap(),
    val standard: Standard? = null,
    val order: Int? = null,
)
