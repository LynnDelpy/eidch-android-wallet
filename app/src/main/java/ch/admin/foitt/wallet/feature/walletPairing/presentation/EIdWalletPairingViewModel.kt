package ch.admin.foitt.wallet.feature.walletPairing.presentation

import ch.admin.foitt.wallet.platform.navigation.NavigationManager
import ch.admin.foitt.wallet.platform.scaffold.domain.model.TopBarState
import ch.admin.foitt.wallet.platform.scaffold.domain.usecase.SetTopBarState
import ch.admin.foitt.wallet.platform.scaffold.extension.navigateUpOrToRoot
import ch.admin.foitt.wallet.platform.scaffold.presentation.ScreenViewModel
import ch.admin.foitt.walletcomposedestinations.destinations.EIdStartAvSessionScreenDestination
import ch.admin.foitt.walletcomposedestinations.destinations.EIdStartDocumentRecordingScreenDestination
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class EIdWalletPairingViewModel @Inject constructor(
    private val navManager: NavigationManager,
    setTopBarState: SetTopBarState,
) : ScreenViewModel(setTopBarState) {
    override val topBarState = TopBarState.DetailsWithCloseButton(
        onUp = navManager::navigateUpOrToRoot,
        onClose = { navManager.navigateBackToHome(popUntil = EIdStartAvSessionScreenDestination) },
        titleId = null
    )

    fun onSingleDeviceFlow() = navManager.navigateToAndClearCurrent(EIdStartDocumentRecordingScreenDestination)

    fun onMultiDeviceFlow() = navManager.navigateBackToHome(EIdStartAvSessionScreenDestination)
}
