package ch.admin.foitt.wallet.platform.locale.domain.usecase.implementation

import ch.admin.foitt.wallet.platform.database.domain.model.CredentialDisplay
import ch.admin.foitt.wallet.platform.database.domain.model.DisplayLanguage
import ch.admin.foitt.wallet.platform.locale.domain.usecase.GetCurrentAppLocale
import ch.admin.foitt.wallet.platform.locale.domain.usecase.GetLocalizedAndThemedDisplay
import ch.admin.foitt.wallet.platform.oca.domain.model.overlays.BrandingOverlay
import ch.admin.foitt.wallet.platform.theme.domain.model.Theme
import javax.inject.Inject

class GetLocalizedAndThemedDisplayImpl @Inject constructor(
    private val getCurrentAppLocale: GetCurrentAppLocale,
) : GetLocalizedAndThemedDisplay {
    override fun invoke(
        credentialDisplays: List<CredentialDisplay>,
        preferredLocale: String?,
        preferredTheme: Theme,
    ): CredentialDisplay? {
        val appLocale = getCurrentAppLocale()
        val theme = preferredTheme.value
        val language = appLocale.language // e.g. "de"
        val country = appLocale.country // e.g. "CH"

        val perfectMatchingDisplay = credentialDisplays.getPerfectMatch(
            language = language,
            country = country,
            theme = theme
        )

        val almostPerfectMatchingDisplay = credentialDisplays.getWithLocale(language = language, country = country)

        val bestMatchingDisplay = credentialDisplays.bestMatchingLocaleAndTheme(
            language = language,
            preferredLocale = preferredLocale,
            preferredTheme = theme
        )

        return perfectMatchingDisplay ?: almostPerfectMatchingDisplay ?: bestMatchingDisplay ?: credentialDisplays.firstOrNull()
    }

    // display contains "de-CH" and theme
    private fun List<CredentialDisplay>.getPerfectMatch(
        language: String,
        country: String,
        theme: String,
    ) = firstOrNull { display ->
        display.locale.formatLocale().equals("$language-$country", ignoreCase = true) && display.theme == theme
    }

    // display contains "de-CH" but not correct theme
    private fun List<CredentialDisplay>.getWithLocale(
        language: String,
        country: String,
    ) = firstOrNull { display ->
        display.locale.formatLocale().equals("$language-$country", ignoreCase = true)
    }

    private fun String.formatLocale() = replace("_", "-")

    private fun List<CredentialDisplay>.bestMatchingLocaleAndTheme(
        language: String,
        preferredLocale: String?,
        preferredTheme: String
    ): CredentialDisplay? {
        // Create a map of preferred languages with the provided language in the first place.
        // The map value indicates the preference order (lower index => higher priority)
        val preferredLanguages = setOf(language)
            .plus(DisplayLanguage.PRIORITIES)
            .mapIndexed { index: Int, s: String -> s to index }
            .toMap()

        val bestMatchingLanguage = this.minByOrNull { display ->
            preferredLanguages.getOrDefault(
                display.locale.language(),
                Int.MAX_VALUE
            )
        }?.locale?.language()

        val displaysWithBestLanguage = this.filter { it.locale.language() == bestMatchingLanguage }
        val themedDisplayWithBestLanguage = displaysWithBestLanguage.firstOrNull { it.theme == preferredTheme }
            ?: displaysWithBestLanguage.firstOrNull()

        val displaysWithLanguage = this.filter { display ->
            display.locale.language().equals(preferredLocale?.language(), ignoreCase = true)
        }
        val themedDisplayWithLanguage = displaysWithLanguage.firstOrNull {
            it.theme == preferredTheme
        } ?: displaysWithLanguage.firstOrNull {
            it.theme == BrandingOverlay.DEFAULT_THEME
        }

        return themedDisplayWithBestLanguage ?: themedDisplayWithLanguage
    }

    private fun String.language() = split("-", "_").first()
}
