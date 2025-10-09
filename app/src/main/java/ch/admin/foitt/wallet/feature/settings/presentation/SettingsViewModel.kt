package ch.admin.foitt.wallet.feature.settings.presentation

import android.content.Context
import ch.admin.foitt.wallet.R
import ch.admin.foitt.wallet.platform.navigation.NavigationManager
import ch.admin.foitt.wallet.platform.scaffold.domain.model.TopBarState
import ch.admin.foitt.wallet.platform.scaffold.domain.usecase.SetTopBarState
import ch.admin.foitt.wallet.platform.scaffold.presentation.ScreenViewModel
import ch.admin.foitt.wallet.platform.utils.openLink
import ch.admin.foitt.walletcomposedestinations.destinations.ImpressumScreenDestination
import ch.admin.foitt.walletcomposedestinations.destinations.LanguageScreenDestination
import ch.admin.foitt.walletcomposedestinations.destinations.LicencesScreenDestination
import ch.admin.foitt.walletcomposedestinations.destinations.SecuritySettingsScreenDestination
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val navManager: NavigationManager,
    @param:ApplicationContext private val appContext: Context,
    setTopBarState: SetTopBarState,
) : ScreenViewModel(setTopBarState) {
    override val topBarState = TopBarState.Details(navManager::navigateUp, R.string.tk_settings_title)

    fun onSecurityAndPrivacy() = navManager.navigateTo(SecuritySettingsScreenDestination)

    fun onLanguage() = navManager.navigateTo(LanguageScreenDestination)

    fun onHelp() = appContext.openLink(R.string.tk_settings_general_help_link_value)

    fun onFeedback() = appContext.openLink(R.string.tk_settings_general_feedback_link_value)

    fun onLicenses() = navManager.navigateTo(LicencesScreenDestination)

    fun onImprint() = navManager.navigateTo(ImpressumScreenDestination)
}
