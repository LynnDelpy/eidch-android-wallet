package ch.admin.foitt.wallet.feature.walletPairing.presentation

import android.os.Build
import androidx.lifecycle.SavedStateHandle
import ch.admin.foitt.wallet.platform.navArgs.domain.model.EIdOnlineSessionNavArg
import ch.admin.foitt.wallet.platform.navigation.NavigationManager
import ch.admin.foitt.wallet.platform.scaffold.domain.model.TopBarState
import ch.admin.foitt.wallet.platform.scaffold.domain.usecase.SetTopBarState
import ch.admin.foitt.wallet.platform.scaffold.presentation.ScreenViewModel
import ch.admin.foitt.walletcomposedestinations.destinations.EIdIntroScreenDestination
import ch.admin.foitt.walletcomposedestinations.destinations.EIdPairingOverviewScreenDestination
import ch.admin.foitt.walletcomposedestinations.destinations.EIdWalletPairingQrCodeScreenDestination
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class EIdPairingOverviewViewModel @Inject constructor(
    private val navManager: NavigationManager,
    savedStateHandle: SavedStateHandle,
    setTopBarState: SetTopBarState,
) : ScreenViewModel(setTopBarState) {
    private val navArgs: EIdOnlineSessionNavArg = EIdPairingOverviewScreenDestination.argsFrom(savedStateHandle)

    override val topBarState = TopBarState.DetailsWithCloseButton(
        titleId = null,
        onUp = navManager::popBackStack,
        onClose = {
            navManager.navigateBackToHome(EIdIntroScreenDestination)
        }
    )

    private val deviceModel = Build.MODEL
    private val deviceManufacturer = Build.MANUFACTURER
    val deviceName = "$deviceManufacturer $deviceModel"

    fun onThisDeviceClick() {
    }

    fun onAdditionalDevicesClick() {
        navManager.navigateTo(EIdWalletPairingQrCodeScreenDestination(caseId = navArgs.caseId))
    }

    fun onContinue() {
    }
}
