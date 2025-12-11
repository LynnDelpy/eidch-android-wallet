package ch.admin.foitt.wallet.feature.walletPairing.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import ch.admin.foitt.wallet.feature.eIdApplicationProcess.presentation.model.QrBoxUiState
import ch.admin.foitt.wallet.platform.eIdApplicationProcess.domain.model.PairWalletError
import ch.admin.foitt.wallet.platform.eIdApplicationProcess.domain.model.PairWalletResponse
import ch.admin.foitt.wallet.platform.eIdApplicationProcess.domain.usecase.PairWallet
import ch.admin.foitt.wallet.platform.navArgs.domain.model.EIdOnlineSessionNavArg
import ch.admin.foitt.wallet.platform.navigation.NavigationManager
import ch.admin.foitt.wallet.platform.scaffold.domain.model.TopBarState
import ch.admin.foitt.wallet.platform.scaffold.domain.usecase.SetTopBarState
import ch.admin.foitt.wallet.platform.scaffold.presentation.ScreenViewModel
import ch.admin.foitt.wallet.platform.utils.generateQRBitmap
import ch.admin.foitt.wallet.platform.utils.trackCompletion
import ch.admin.foitt.walletcomposedestinations.destinations.EIdIntroScreenDestination
import ch.admin.foitt.walletcomposedestinations.destinations.EIdPairingOverviewScreenDestination
import com.github.michaelbull.result.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class EIdWalletPairingQrCodeViewModel @Inject constructor(
    private val pairWallet: PairWallet,
    savedStateHandle: SavedStateHandle,
    private val navManager: NavigationManager,
    setTopBarState: SetTopBarState
) : ScreenViewModel(setTopBarState) {
    override val topBarState: TopBarState = TopBarState.EmptyWithCloseButton(
        onClose = { navManager.navigateBackToHome(popUntil = EIdIntroScreenDestination) },
    )
    private val navArgs: EIdOnlineSessionNavArg = EIdPairingOverviewScreenDestination.argsFrom(savedStateHandle)

    private val _isWaitingForDevicePairing = MutableStateFlow(false)
    val isWaitingForDevicePairing = _isWaitingForDevicePairing.asStateFlow()
    private val _isRequestLoading = MutableStateFlow(false)
    val isRequestLoading = _isRequestLoading.asStateFlow()

    private val _isRequestStatusLoading = MutableStateFlow(false)
    val isRequestStatusLoading = _isRequestStatusLoading.asStateFlow()

    private val _pairWalletResponse =
        MutableStateFlow<Result<PairWalletResponse, PairWalletError>?>(null)

    val qrBoxState: StateFlow<QrBoxUiState> = combine(isRequestLoading, _pairWalletResponse) { isRequestLoading, response ->
        when {
            isRequestLoading || response == null -> {
                _isWaitingForDevicePairing.value = false
                QrBoxUiState.Loading
            }
            response.isOk -> {
                _isWaitingForDevicePairing.value = true
                QrBoxUiState.Success(
                    qrBitmap = response.value.credentialOfferLink.generateQRBitmap()
                )
            }
            else -> {
                _isWaitingForDevicePairing.value = false
                QrBoxUiState.Failure
            }
        }
    }.toStateFlow(QrBoxUiState.Loading)

    fun onRefreshRequest() {
        viewModelScope.launch {
            refreshRequest()
        }.trackCompletion(_isRequestLoading)
    }

    private suspend fun refreshRequest() {
        _pairWalletResponse.update { pairWallet(navArgs.caseId) }
    }

    fun onClose() {
        navManager.navigateBackToHome(popUntil = EIdIntroScreenDestination)
    }
}
