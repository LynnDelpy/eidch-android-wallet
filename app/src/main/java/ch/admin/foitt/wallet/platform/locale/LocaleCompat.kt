package ch.admin.foitt.wallet.platform.locale

import android.os.Build
import java.util.Locale

object LocaleCompat {
    fun of(language: String): Locale = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.BAKLAVA) {
        Locale.of(language)
    } else {
        Locale.Builder().setLanguage(language).build()
    }

    fun of(language: String, country: String): Locale = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.BAKLAVA) {
        Locale.of(language, country)
    } else {
        Locale.Builder().setLanguage(language).setRegion(country).build()
    }
}
