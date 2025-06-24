package ch.admin.foitt.wallet.platform.ssi.domain.usecase.implementation

import ch.admin.foitt.wallet.platform.database.domain.model.CredentialClaimWithDisplays
import ch.admin.foitt.wallet.platform.locale.domain.usecase.GetCurrentAppLocale
import ch.admin.foitt.wallet.platform.locale.domain.usecase.GetLocalizedDisplay
import ch.admin.foitt.wallet.platform.oca.domain.model.DateTimePattern
import ch.admin.foitt.wallet.platform.ssi.domain.model.CredentialClaimData
import ch.admin.foitt.wallet.platform.ssi.domain.model.CredentialClaimImage
import ch.admin.foitt.wallet.platform.ssi.domain.model.CredentialClaimText
import ch.admin.foitt.wallet.platform.ssi.domain.model.MapToCredentialClaimDataError
import ch.admin.foitt.wallet.platform.ssi.domain.model.ValueType
import ch.admin.foitt.wallet.platform.ssi.domain.model.toMapToCredentialClaimDataError
import ch.admin.foitt.wallet.platform.ssi.domain.usecase.MapToCredentialClaimData
import ch.admin.foitt.wallet.platform.utils.asBestLocalizedForPattern
import ch.admin.foitt.wallet.platform.utils.base64NonUrlStringToByteArray
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.coroutines.runSuspendCatching
import com.github.michaelbull.result.get
import com.github.michaelbull.result.mapError
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.util.Locale
import javax.inject.Inject

class MapToCredentialClaimDataImpl @Inject constructor(
    private val getLocalizedDisplay: GetLocalizedDisplay,
    private val getCurrentAppLocale: GetCurrentAppLocale
) : MapToCredentialClaimData {
    override suspend fun invoke(
        claimWithDisplays: CredentialClaimWithDisplays,
    ): Result<CredentialClaimData, MapToCredentialClaimDataError> =
        runSuspendCatching {
            val displays = claimWithDisplays.displays
            val claim = claimWithDisplays.claim
            val locale = getCurrentAppLocale()
            getLocalizedDisplay(displays)?.let { display ->
                when (ValueType.getByType(claim.valueType)) {
                    ValueType.BOOLEAN,
                    ValueType.NUMERIC,
                    ValueType.STRING -> CredentialClaimText(localizedKey = display.name, value = claim.value)

                    ValueType.DATETIME -> CredentialClaimText(
                        localizedKey = display.name,
                        value = localizeDateTime(value = claim.value, pattern = claim.valueDisplayInfo, locale = locale)
                    )

                    ValueType.IMAGE -> {
                        val byteArray = base64NonUrlStringToByteArray(claim.value)
                        CredentialClaimImage(localizedKey = display.name, imageData = byteArray)
                    }

                    ValueType.UNSUPPORTED -> error("Unsupported value type '${claim.valueType}' found for claim '${claim.key}'")
                }
            } ?: error("No localized display found")
        }.mapError { throwable ->
            throwable.toMapToCredentialClaimDataError("MapToCredentialClaimData error")
        }

    private fun localizeDateTime(value: String, pattern: String?, locale: Locale): String = runSuspendCatching {
        when (val dateTimePattern = DateTimePattern.getPatternFor(pattern)) {
            null -> value

            DateTimePattern.DATE_TIME_TIMEZONE_SECONDS_FRACTION,
            DateTimePattern.DATE_TIME_TIMEZONE_SECONDS,
            DateTimePattern.DATE_TIME_TIMEZONE,
            DateTimePattern.DATE_TIMEZONE,
            DateTimePattern.TIME_TIMEZONE_SECONDS_FRACTION,
            DateTimePattern.TIME_TIMEZONE_SECONDS,
            DateTimePattern.TIME_TIMEZONE -> {
                // output date in local timezone
                ZonedDateTime.parse(value).withZoneSameInstant(ZoneOffset.systemDefault()).asBestLocalizedForPattern(
                    locale = locale,
                    pattern = dateTimePattern.pattern
                )
            }

            DateTimePattern.DATE_TIME_SECONDS_FRACTION,
            DateTimePattern.DATE_TIME_SECONDS,
            DateTimePattern.DATE_TIME,
            DateTimePattern.DATE,
            DateTimePattern.TIME_SECONDS_FRACTION,
            DateTimePattern.TIME_SECONDS,
            DateTimePattern.TIME,
            DateTimePattern.YEAR_MONTH,
            DateTimePattern.YEAR -> {
                // output date as is
                ZonedDateTime.parse(value).withZoneSameInstant(ZoneOffset.UTC).asBestLocalizedForPattern(
                    locale = locale,
                    pattern = dateTimePattern.pattern
                )
            }
        }
    }.get() ?: value
}
