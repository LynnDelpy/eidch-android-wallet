package ch.admin.foitt.wallet.feature.eIdApplicationProcess.presentation

import ch.admin.foitt.wallet.platform.navigation.NavigationManager
import ch.admin.foitt.wallet.platform.scaffold.domain.model.TopBarState
import ch.admin.foitt.wallet.platform.scaffold.domain.usecase.SetTopBarState
import ch.admin.foitt.wallet.platform.scaffold.extension.navigateUpOrToRoot
import ch.admin.foitt.wallet.platform.scaffold.presentation.ScreenViewModel
import ch.admin.foitt.walletcomposedestinations.destinations.EIdStartSelfieVideoScreenDestination
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class EIdStartDocumentRecordingViewModel @Inject constructor(
    private val navManager: NavigationManager,
    setTopBarState: SetTopBarState
) : ScreenViewModel(setTopBarState) {
    override val topBarState: TopBarState = TopBarState.EmptyWithCloseButton(
        onClose = navManager::navigateUpOrToRoot,
    )

    fun onStart() = navManager.navigateToAndClearCurrent(EIdStartSelfieVideoScreenDestination)
}
