package ch.admin.foitt.wallet.feature.settings.presentation.language

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ch.admin.foitt.wallet.R
import ch.admin.foitt.wallet.feature.settings.presentation.composables.LanguageSettingsItem
import ch.admin.foitt.wallet.feature.settings.presentation.composables.SettingsCard
import ch.admin.foitt.wallet.feature.settings.presentation.composables.SettingsDivider
import ch.admin.foitt.wallet.platform.composables.presentation.addTopScaffoldPadding
import ch.admin.foitt.wallet.platform.composables.presentation.bottomSafeDrawing
import ch.admin.foitt.wallet.platform.composables.presentation.horizontalSafeDrawing
import ch.admin.foitt.wallet.platform.preview.WalletAllScreenPreview
import ch.admin.foitt.wallet.theme.Sizes
import ch.admin.foitt.wallet.theme.WalletListItems
import ch.admin.foitt.wallet.theme.WalletTheme
import com.ramcosta.composedestinations.annotation.Destination
import java.util.Locale

@Destination
@Composable
fun LanguageScreen(
    viewModel: LanguageViewModel
) {
    viewModel.checkLocaleChangedInSettings()

    LanguageScreenContent(
        isSystemLanguage = viewModel.isSystemLocale.collectAsStateWithLifecycle().value,
        language = viewModel.selectedLocale.collectAsStateWithLifecycle().value.displayLanguage,
        supportedLocales = viewModel.supportedLocales.collectAsStateWithLifecycle().value,
        onUpdateLanguage = viewModel::onUpdateLocale,
        onUseSystemDefaultLanguage = viewModel::useSystemDefaultLocale,
    )
}

@Composable
private fun LanguageScreenContent(
    isSystemLanguage: Boolean,
    language: String,
    supportedLocales: List<Locale>,
    onUpdateLanguage: (Locale) -> Unit,
    onUseSystemDefaultLanguage: () -> Unit,
) = Box(
    modifier = Modifier
        .fillMaxSize()
        .background(WalletTheme.colorScheme.surfaceContainerLow)
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .addTopScaffoldPadding()
            .verticalScroll(state = rememberScrollState())
            .horizontalSafeDrawing()
            .bottomSafeDrawing()
            .padding(
                bottom = Sizes.s04,
            )
    ) {
        SettingsCard {
            supportedLocales.forEach { locale ->
                WalletListItems.LanguageSettingsItem(
                    title = locale.displayLanguage,
                    isChecked = locale.displayLanguage == language && !isSystemLanguage,
                    onLanguageClick = { onUpdateLanguage(locale) }
                )
                WalletListItems.SettingsDivider()
            }
            WalletListItems.LanguageSettingsItem(
                title = stringResource(id = R.string.tk_settings_language_device),
                isChecked = isSystemLanguage,
                onLanguageClick = onUseSystemDefaultLanguage
            )
        }
    }
}

@WalletAllScreenPreview
@Composable
private fun SettingsScreenPreview() {
    WalletTheme {
        LanguageScreenContent(
            isSystemLanguage = false,
            language = "English",
            supportedLocales = listOf(Locale("en"), Locale("de")),
            onUpdateLanguage = {},
            onUseSystemDefaultLanguage = {},
        )
    }
}
