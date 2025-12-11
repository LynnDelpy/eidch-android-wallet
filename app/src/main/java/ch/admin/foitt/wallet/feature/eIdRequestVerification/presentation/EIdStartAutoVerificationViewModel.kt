package ch.admin.foitt.wallet.feature.eIdRequestVerification.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import ch.admin.foitt.wallet.feature.eIdApplicationProcess.presentation.model.StartAutoVerificationUiState
import ch.admin.foitt.wallet.platform.eIdApplicationProcess.domain.model.AutoVerificationResponse
import ch.admin.foitt.wallet.platform.eIdApplicationProcess.domain.model.EIdRequestError
import ch.admin.foitt.wallet.platform.eIdApplicationProcess.domain.model.StartAutoVerificationError
import ch.admin.foitt.wallet.platform.eIdApplicationProcess.domain.usecase.SetStartAutoVerificationResult
import ch.admin.foitt.wallet.platform.eIdApplicationProcess.domain.usecase.StartAutoVerification
import ch.admin.foitt.wallet.platform.navArgs.domain.model.EIdOnlineSessionNavArg
import ch.admin.foitt.wallet.platform.navigation.NavigationManager
import ch.admin.foitt.wallet.platform.scaffold.domain.model.TopBarState
import ch.admin.foitt.wallet.platform.scaffold.domain.usecase.SetTopBarState
import ch.admin.foitt.wallet.platform.scaffold.presentation.ScreenViewModel
import ch.admin.foitt.wallet.platform.utils.trackCompletion
import ch.admin.foitt.walletcomposedestinations.destinations.EIdDocumentRecordingScreenDestination
import ch.admin.foitt.walletcomposedestinations.destinations.EIdNfcScannerScreenDestination
import ch.admin.foitt.walletcomposedestinations.destinations.EIdStartAutoVerificationScreenDestination
import ch.admin.foitt.walletcomposedestinations.destinations.HomeScreenDestination
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.onSuccess
import com.github.michaelbull.result.unwrapError
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class EIdStartAutoVerificationViewModel @Inject constructor(
    private val startAutoVerification: StartAutoVerification,
    private val navManager: NavigationManager,
    private val setStartAutoVerificationResult: SetStartAutoVerificationResult,
    savedStateHandle: SavedStateHandle,
    setTopBarState: SetTopBarState,
) : ScreenViewModel(setTopBarState) {
    override val topBarState = TopBarState.Empty

    private val navArgs: EIdOnlineSessionNavArg = EIdStartAutoVerificationScreenDestination.argsFrom(savedStateHandle)

    private val isLoading = MutableStateFlow(false)
    private val startAutoVerificationResult =
        MutableStateFlow<Result<AutoVerificationResponse, StartAutoVerificationError>?>(null)

    val state: StateFlow<StartAutoVerificationUiState> = combine(
        isLoading,
        startAutoVerificationResult,
    ) { isLoading, startAutoVerificationResult ->
        when {
            isLoading -> StartAutoVerificationUiState.Loading
            startAutoVerificationResult == null -> StartAutoVerificationUiState.Info(
                onStart = ::onStart
            )
            startAutoVerificationResult.isOk -> StartAutoVerificationUiState.Started(
                onContinue = { onContinue(startAutoVerificationResult.value.useNfc) }
            )
            startAutoVerificationResult.unwrapError() is EIdRequestError.NetworkError ->
                StartAutoVerificationUiState.NetworkError(
                    onClose = ::onClose,
                    onRetry = ::onRetry
                )
            else -> StartAutoVerificationUiState.Unexpected(
                onClose = ::onClose,
                onRetry = ::onRetry,
            )
        }
    }.toStateFlow(StartAutoVerificationUiState.Loading)

    private suspend fun onStartAv() {
        startAutoVerification(caseId = navArgs.caseId)
            .also { result ->
                startAutoVerificationResult.value = result
            }
            .onSuccess {
                setStartAutoVerificationResult(it)
                onContinue(it.useNfc)
            }
    }

    private fun onStart() {
        if (isLoading.value) {
            return
        }
        viewModelScope.launch {
            onStartAv()
        }.trackCompletion(isLoading)
    }

    private fun onClose() = navManager.popBackStackTo(HomeScreenDestination, false)

    private fun onRetry() = onStart()

    private fun onContinue(useNfc: Boolean) = if (useNfc) {
        navManager.navigateTo(EIdNfcScannerScreenDestination(caseId = navArgs.caseId))
    } else {
        navManager.navigateTo(EIdDocumentRecordingScreenDestination(caseId = navArgs.caseId))
    }
}
