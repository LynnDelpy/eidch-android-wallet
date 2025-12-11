package ch.admin.foitt.wallet.feature.walletPairing.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import ch.admin.foitt.wallet.platform.eIdApplicationProcess.domain.usecase.PairWallet
import ch.admin.foitt.wallet.platform.eIdApplicationProcess.domain.usecase.StartOnlineSession
import ch.admin.foitt.wallet.platform.navArgs.domain.model.EIdOnlineSessionNavArg
import ch.admin.foitt.wallet.platform.navigation.NavigationManager
import ch.admin.foitt.wallet.platform.scaffold.domain.model.TopBarState
import ch.admin.foitt.wallet.platform.scaffold.domain.usecase.SetTopBarState
import ch.admin.foitt.wallet.platform.scaffold.extension.navigateUpOrToRoot
import ch.admin.foitt.wallet.platform.scaffold.presentation.ScreenViewModel
import ch.admin.foitt.wallet.platform.utils.trackCompletion
import ch.admin.foitt.walletcomposedestinations.destinations.EIdPairingOverviewScreenDestination
import ch.admin.foitt.walletcomposedestinations.destinations.EIdStartAutoVerificationScreenDestination
import ch.admin.foitt.walletcomposedestinations.destinations.EIdStartAvSessionScreenDestination
import ch.admin.foitt.walletcomposedestinations.destinations.EIdWalletPairingScreenDestination
import com.github.michaelbull.result.onFailure
import com.github.michaelbull.result.onSuccess
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EIdWalletPairingViewModel @Inject constructor(
    private val startOnlineSession: StartOnlineSession,
    private val pairWallet: PairWallet,
    private val navManager: NavigationManager,
    savedStateHandle: SavedStateHandle,
    setTopBarState: SetTopBarState,
) : ScreenViewModel(setTopBarState) {

    private val navArgs: EIdOnlineSessionNavArg = EIdWalletPairingScreenDestination.argsFrom(savedStateHandle)

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    override val topBarState = TopBarState.DetailsWithCloseButton(
        onUp = navManager::navigateUpOrToRoot,
        onClose = { navManager.navigateBackToHome(popUntil = EIdStartAvSessionScreenDestination) },
        titleId = null
    )

    fun onSingleDeviceFlow() {
        viewModelScope.launch {
            startOnlineSession(caseId = navArgs.caseId)
                .onSuccess {
                    pairWallet()
                }.onFailure {
                }
        }.trackCompletion(_isLoading)
    }

    fun onMultiDeviceFlow() {
        viewModelScope.launch {
            startOnlineSession(caseId = navArgs.caseId)
                .onSuccess {
                    navManager.navigateTo(EIdPairingOverviewScreenDestination(caseId = navArgs.caseId))
                }.onFailure {
                }
        }.trackCompletion(_isLoading)
    }

    private suspend fun pairWallet() {
        pairWallet(caseId = navArgs.caseId)
            .onSuccess {
                navManager.navigateToAndClearCurrent(
                    EIdStartAutoVerificationScreenDestination(
                        caseId = navArgs.caseId
                    )
                )
            }.onFailure {
            }
    }
}
