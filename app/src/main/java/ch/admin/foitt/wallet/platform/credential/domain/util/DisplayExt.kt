package ch.admin.foitt.wallet.platform.credential.domain.util

import ch.admin.foitt.wallet.platform.credential.domain.model.AnyDisplay
import ch.admin.foitt.wallet.platform.database.domain.model.DisplayLanguage

fun <T : AnyDisplay> List<T>?.addFallbackLanguageIfNecessary(
    fallbackValue: () -> T
): List<T> {
    if (this == null) {
        return listOf(fallbackValue())
    }

    return if (none { it.locale == DisplayLanguage.FALLBACK }) {
        this + fallbackValue()
    } else {
        this
    }
}
