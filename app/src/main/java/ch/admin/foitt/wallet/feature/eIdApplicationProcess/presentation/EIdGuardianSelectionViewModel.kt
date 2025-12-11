package ch.admin.foitt.wallet.feature.eIdApplicationProcess.presentation

import androidx.lifecycle.SavedStateHandle
import ch.admin.foitt.wallet.platform.navArgs.domain.model.EIdRequestNavArg
import ch.admin.foitt.wallet.platform.navigation.NavigationManager
import ch.admin.foitt.wallet.platform.scaffold.domain.model.TopBarState
import ch.admin.foitt.wallet.platform.scaffold.domain.usecase.SetTopBarState
import ch.admin.foitt.wallet.platform.scaffold.presentation.ScreenViewModel
import ch.admin.foitt.walletcomposedestinations.destinations.EIdGuardianConsentScreenDestination
import ch.admin.foitt.walletcomposedestinations.destinations.EIdGuardianSelectionScreenDestination
import ch.admin.foitt.walletcomposedestinations.destinations.EIdGuardianVerificationScreenDestination
import ch.admin.foitt.walletcomposedestinations.destinations.EIdIntroScreenDestination
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class EIdGuardianSelectionViewModel @Inject constructor(
    private val navManager: NavigationManager,
    savedStateHandle: SavedStateHandle,
    setTopBarState: SetTopBarState,
) : ScreenViewModel(setTopBarState) {
    override val topBarState = TopBarState.DetailsWithCloseButton(
        titleId = null,
        onUp = navManager::popBackStack,
        onClose = { navManager.navigateBackToHome(EIdIntroScreenDestination) }
    )

    private val navArgs: EIdRequestNavArg = EIdGuardianSelectionScreenDestination.argsFrom(savedStateHandle)

    fun onObtainConsent() = navManager.navigateTo(
        EIdGuardianConsentScreenDestination(
            caseId = navArgs.caseId,
        )
    )

    fun onContinueAsGuardian() = navManager.navigateTo(
        EIdGuardianVerificationScreenDestination(
            caseId = navArgs.caseId,
        )
    )
}
