package ch.admin.foitt.wallet.feature.eIdApplicationProcess.presentation

import androidx.lifecycle.SavedStateHandle
import ch.admin.foitt.avwrapper.AVBeam
import ch.admin.foitt.wallet.platform.navArgs.domain.model.EIdOnlineSessionNavArg
import ch.admin.foitt.wallet.platform.navigation.NavigationManager
import ch.admin.foitt.wallet.platform.scaffold.domain.model.TopBarState
import ch.admin.foitt.wallet.platform.scaffold.domain.usecase.SetTopBarState
import ch.admin.foitt.wallet.platform.scaffold.presentation.ScreenViewModel
import ch.admin.foitt.walletcomposedestinations.destinations.EIdStartAvSessionScreenDestination
import ch.admin.foitt.walletcomposedestinations.destinations.EIdStartSelfieVideoScreenDestination
import ch.admin.foitt.walletcomposedestinations.destinations.SDKFaceScannerScreenDestination
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class EIdStartSelfieVideoViewModel @Inject constructor(
    private val navManager: NavigationManager,
    savedStateHandle: SavedStateHandle,
    private val avBeam: AVBeam,
    setTopBarState: SetTopBarState,
) : ScreenViewModel(setTopBarState) {
    override val topBarState = TopBarState.EmptyWithCloseButton(
        onClose = ::onClose
    )

    private val navArgs: EIdOnlineSessionNavArg = EIdStartSelfieVideoScreenDestination.argsFrom(savedStateHandle)

    fun onStart() {
        navManager.navigateTo(
            SDKFaceScannerScreenDestination(
                caseId = navArgs.caseId
            )
        )
    }

    fun shutDownLibrary() {
        avBeam.shutDown()
    }

    private fun onClose() {
        shutDownLibrary()
        navManager.navigateBackToHome(
            popUntil = EIdStartAvSessionScreenDestination
        )
    }
}
