package ch.admin.foitt.wallet.feature.eIdApplicationProcess.presentation

import androidx.lifecycle.SavedStateHandle
import ch.admin.foitt.wallet.platform.navArgs.domain.model.EIdOnlineSessionNavArg
import ch.admin.foitt.wallet.platform.navigation.NavigationManager
import ch.admin.foitt.wallet.platform.scaffold.domain.model.TopBarState
import ch.admin.foitt.wallet.platform.scaffold.domain.usecase.SetTopBarState
import ch.admin.foitt.wallet.platform.scaffold.extension.navigateUpOrToRoot
import ch.admin.foitt.wallet.platform.scaffold.presentation.ScreenViewModel
import ch.admin.foitt.walletcomposedestinations.destinations.EIdStartAvSessionScreenDestination
import ch.admin.foitt.walletcomposedestinations.destinations.EIdWalletPairingScreenDestination
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class EIdStartAvSessionViewModel @Inject constructor(
    private val navManager: NavigationManager,
    savedStateHandle: SavedStateHandle,
    setTopBarState: SetTopBarState,
) : ScreenViewModel(setTopBarState) {

    private val navArgs: EIdOnlineSessionNavArg = EIdStartAvSessionScreenDestination.argsFrom(savedStateHandle)

    override val topBarState = TopBarState.Details(
        titleId = null,
        onUp = ::onClose,
    )

    fun onStart() = navManager.navigateTo(
        EIdWalletPairingScreenDestination(
            caseId = navArgs.caseId,
        )
    )

    private fun onClose() = navManager.navigateUpOrToRoot()
}
