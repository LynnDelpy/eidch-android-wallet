package ch.admin.foitt.wallet.feature.eIdRequestVerification.presentation

import android.content.Context
import ch.admin.foitt.wallet.R
import ch.admin.foitt.wallet.feature.eIdRequestVerification.presentation.model.EIdNfcScannerUiState
import ch.admin.foitt.wallet.platform.navigation.NavigationManager
import ch.admin.foitt.wallet.platform.scaffold.domain.model.TopBarState
import ch.admin.foitt.wallet.platform.scaffold.domain.usecase.SetTopBarState
import ch.admin.foitt.wallet.platform.scaffold.extension.navigateUpOrToRoot
import ch.admin.foitt.wallet.platform.scaffold.presentation.ScreenViewModel
import ch.admin.foitt.wallet.platform.utils.openLink
import ch.admin.foitt.walletcomposedestinations.destinations.EIdNfcScannerScreenDestination
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class EIdNfcScannerViewModel @Inject constructor(
    private val navManager: NavigationManager,
    @param:ApplicationContext private val context: Context,
    setTopBarState: SetTopBarState,
) : ScreenViewModel(setTopBarState) {

    override val topBarState: TopBarState = TopBarState.EmptyWithCloseButton(
        onClose = navManager::navigateUpOrToRoot,
    )

    private val _uiState = MutableStateFlow<EIdNfcScannerUiState>(EIdNfcScannerUiState.Initializing)
    val uiState = _uiState.asStateFlow()

    init {
        _uiState.update {
            EIdNfcScannerUiState.Info(
                onStart = ::onStart,
                onTips = ::onTips,
            )
        }
    }

    fun onStart() = navManager.navigateBackToHome(EIdNfcScannerScreenDestination)

    fun onTips() = context.openLink(context.getString(R.string.tk_eidRequest_nfcScan_helpLink))
}
