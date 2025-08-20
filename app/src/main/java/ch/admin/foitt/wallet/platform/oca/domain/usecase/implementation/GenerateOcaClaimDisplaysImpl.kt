package ch.admin.foitt.wallet.platform.oca.domain.usecase.implementation

import ch.admin.foitt.wallet.platform.credential.domain.model.AnyClaimDisplay
import ch.admin.foitt.wallet.platform.credential.domain.util.addFallbackLanguageIfNecessary
import ch.admin.foitt.wallet.platform.database.domain.model.CredentialClaim
import ch.admin.foitt.wallet.platform.database.domain.model.DisplayLanguage
import ch.admin.foitt.wallet.platform.oca.domain.model.AttributeType
import ch.admin.foitt.wallet.platform.oca.domain.model.DateTimePattern
import ch.admin.foitt.wallet.platform.oca.domain.model.EntryCode
import ch.admin.foitt.wallet.platform.oca.domain.model.Locale
import ch.admin.foitt.wallet.platform.oca.domain.model.OcaClaimData
import ch.admin.foitt.wallet.platform.oca.domain.model.overlays.CharacterEncoding
import ch.admin.foitt.wallet.platform.oca.domain.model.overlays.Standard
import ch.admin.foitt.wallet.platform.oca.domain.usecase.GenerateOcaClaimDisplays
import ch.admin.foitt.wallet.platform.oca.domain.util.parseIso8601
import ch.admin.foitt.wallet.platform.oca.domain.util.parseUnixTime
import ch.admin.foitt.wallet.platform.ssi.domain.model.ImageType
import ch.admin.foitt.wallet.platform.ssi.domain.model.ValueType
import javax.inject.Inject

class GenerateOcaClaimDisplaysImpl @Inject constructor() : GenerateOcaClaimDisplays {

    override fun invoke(
        key: String,
        value: String?,
        ocaClaimData: OcaClaimData,
    ): Pair<CredentialClaim, List<AnyClaimDisplay>> {
        var formattedValue = value
        var valueType: String? = null
        var valueDisplayInfo: String? = null
        if (ocaClaimData.standard == Standard.DataUrl) {
            value?.parseDataUrlImageType()?.let { imageType ->
                formattedValue = value.split(";base64,").last()
                valueType = imageType.mimeType
            }
        } else if (ocaClaimData.attributeType == AttributeType.DateTime) {
            valueType = ValueType.DATETIME.value
            value?.let {
                val (dateTime, format) = parseDateTime(value, ocaClaimData)
                formattedValue = dateTime
                valueDisplayInfo = format
            }
        }

        val claim = CredentialClaim(
            clusterId = UNKNOWN_CLUSTER_ID,
            key = key,
            value = formattedValue,
            valueType = valueType ?: getValueType(ocaClaimData),
            valueDisplayInfo = valueDisplayInfo,
            order = ocaClaimData.order ?: -1
        )
        val claimDisplays =
            createClaimDisplays(ocaClaim = ocaClaimData, value = formattedValue, defaultClaimKey = key)

        return claim to claimDisplays
    }

    private fun String.parseDataUrlImageType(): ImageType? = ImageType.entries.firstOrNull {
        this.startsWith("data:${it.mimeType};base64,")
    }

    private fun parseDateTime(value: String, ocaClaimData: OcaClaimData): Pair<String, String?> {
        return if (ocaClaimData.standard == Standard.UnixTime) {
            val dateTime = parseUnixTime(value) ?: return (value to null)
            (dateTime to DateTimePattern.DATE_TIME_TIMEZONE.name)
        } else {
            parseIso8601(value)
        }
    }

    private fun getValueType(claim: OcaClaimData): String =
        when (claim.attributeType) {
            AttributeType.Binary -> getValueTypeForBinary(claim)
            AttributeType.Boolean -> ValueType.BOOLEAN.value
            AttributeType.DateTime -> ValueType.DATETIME.value
            AttributeType.Numeric -> ValueType.NUMERIC.value
            AttributeType.Text, is AttributeType.Reference, is AttributeType.Array -> ValueType.STRING.value
        }

    private fun getValueTypeForBinary(claim: OcaClaimData): String {
        if (claim.characterEncoding != CharacterEncoding.Base64) {
            return ValueType.STRING.value
        }
        val imageType = ImageType.entries.firstOrNull { it.mimeType == claim.format } ?: return ValueType.STRING.value
        return imageType.mimeType
    }

    private fun createClaimDisplays(
        ocaClaim: OcaClaimData,
        value: String?,
        defaultClaimKey: String,
    ): List<AnyClaimDisplay> {
        val displays = ocaClaim.labels.map { (locale, label) ->
            val entries = ocaClaim.entryMappings.entries.find { it.key == locale }?.value ?: emptyMap()
            val localizedValue = entries.entries.find { it.key == value }?.value
            AnyClaimDisplay(locale = locale, name = label, value = localizedValue)
        }.addFallbackLanguageIfNecessary {
            AnyClaimDisplay(locale = DisplayLanguage.FALLBACK, name = defaultClaimKey)
        }
        if (value == null) return displays

        val locales = displays.map { it.locale }
        val missedEntries = ocaClaim.entryMappings.filterNot { it.key in locales }
        val entriesOnlyDisplays = createClaimDisplays(missedEntries, value, defaultClaimKey)
        return displays + entriesOnlyDisplays
    }

    private fun createClaimDisplays(
        entryMapping: Map<Locale, Map<EntryCode, String>>,
        value: String,
        defaultClaimKey: String,
    ): List<AnyClaimDisplay> = entryMapping.mapNotNull { (locale, entryMapping) ->
        val entry = entryMapping.entries.find { it.key == value } ?: return@mapNotNull null
        AnyClaimDisplay(locale = locale, name = defaultClaimKey, value = entry.value)
    }

    companion object {
        private const val UNKNOWN_CLUSTER_ID = -1L
    }
}
