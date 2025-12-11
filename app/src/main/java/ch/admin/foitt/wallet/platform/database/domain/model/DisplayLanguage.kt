package ch.admin.foitt.wallet.platform.database.domain.model

object DisplayLanguage {
    const val DEFAULT = "en"
    const val DEFAULT_COUNTRY = "CH"
    const val FALLBACK = "fallback"
    const val UNKNOWN = "unknown"
    val PRIORITIES = listOf("de", "en", "unknown", "fallback")
}
