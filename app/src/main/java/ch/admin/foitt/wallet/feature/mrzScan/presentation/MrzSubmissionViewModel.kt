package ch.admin.foitt.wallet.feature.mrzScan.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import ch.admin.foitt.wallet.feature.mrzScan.domain.usecase.FetchSIdCase
import ch.admin.foitt.wallet.feature.mrzScan.domain.usecase.SaveEIdRequestCase
import ch.admin.foitt.wallet.feature.mrzScan.domain.usecase.SaveEIdRequestState
import ch.admin.foitt.wallet.feature.mrzScan.presentation.model.ApplyUiState
import ch.admin.foitt.wallet.platform.database.domain.model.EIdRequestCase
import ch.admin.foitt.wallet.platform.database.domain.model.EIdRequestState
import ch.admin.foitt.wallet.platform.eIdApplicationProcess.domain.model.ApplyRequestError
import ch.admin.foitt.wallet.platform.eIdApplicationProcess.domain.model.CaseResponse
import ch.admin.foitt.wallet.platform.eIdApplicationProcess.domain.model.EIdRequestError
import ch.admin.foitt.wallet.platform.eIdApplicationProcess.domain.model.EIdRequestQueueState
import ch.admin.foitt.wallet.platform.eIdApplicationProcess.domain.model.LegalRepresentant
import ch.admin.foitt.wallet.platform.eIdApplicationProcess.domain.model.MrzData
import ch.admin.foitt.wallet.platform.eIdApplicationProcess.domain.model.StateResponse
import ch.admin.foitt.wallet.platform.eIdApplicationProcess.domain.model.toLegalRepresentativeConsent
import ch.admin.foitt.wallet.platform.eIdApplicationProcess.domain.usecase.FetchSIdStatus
import ch.admin.foitt.wallet.platform.navArgs.domain.model.EIdQueueNavArg
import ch.admin.foitt.wallet.platform.navigation.NavigationManager
import ch.admin.foitt.wallet.platform.scaffold.domain.model.TopBarState
import ch.admin.foitt.wallet.platform.scaffold.domain.usecase.SetTopBarState
import ch.admin.foitt.wallet.platform.scaffold.presentation.ScreenViewModel
import ch.admin.foitt.wallet.platform.utils.trackCompletion
import ch.admin.foitt.walletcomposedestinations.destinations.EIdGuardianSelectionScreenDestination
import ch.admin.foitt.walletcomposedestinations.destinations.EIdIntroScreenDestination
import ch.admin.foitt.walletcomposedestinations.destinations.EIdQueueScreenDestination
import ch.admin.foitt.walletcomposedestinations.destinations.EIdWalletPairingScreenDestination
import ch.admin.foitt.walletcomposedestinations.destinations.HomeScreenDestination
import ch.admin.foitt.walletcomposedestinations.destinations.MrzSubmissionScreenDestination
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.coroutines.runSuspendCatching
import com.github.michaelbull.result.get
import com.github.michaelbull.result.onFailure
import com.github.michaelbull.result.onSuccess
import com.github.michaelbull.result.unwrapError
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.time.Instant
import javax.inject.Inject

@HiltViewModel
internal class MrzSubmissionViewModel @Inject constructor(
    private val fetchSIdCase: FetchSIdCase,
    private val fetchSIdStatus: FetchSIdStatus,
    private val saveEIdRequestCase: SaveEIdRequestCase,
    private val saveEIdRequestState: SaveEIdRequestState,
    savedStateHandle: SavedStateHandle,
    private val navManager: NavigationManager,
    setTopBarState: SetTopBarState,
) : ScreenViewModel(setTopBarState) {
    override val topBarState = TopBarState.Empty

    private val navArgs = MrzSubmissionScreenDestination.argsFrom(savedStateHandle)

    private val isLoading = MutableStateFlow(false)
    private val applyResult = MutableStateFlow<Result<Unit, ApplyRequestError>?>(null)

    val mrzState: StateFlow<ApplyUiState> = combine(
        isLoading,
        applyResult
    ) { isLoading, applyResult ->
        when {
            isLoading -> ApplyUiState.Loading
            applyResult?.isOk == true -> ApplyUiState.Valid
            applyResult?.unwrapError() is EIdRequestError.NetworkError -> ApplyUiState.NetworkError(
                onClose = ::onClose,
                onRetry = ::onRetry
            )

            applyResult?.unwrapError() is EIdRequestError.Unexpected -> ApplyUiState.Unexpected(
                onClose = ::onClose,
                onRetry = ::onRetry
            )

            else -> ApplyUiState.Unexpected(
                onClose = ::onClose,
                onRetry = ::onRetry
            )
        }
    }.toStateFlow(ApplyUiState.Loading)

    private fun onRefreshState() {
        if (isLoading.value) {
            return
        }
        viewModelScope.launch {
            val sIdCase = fetchSIdCase(navArgs.mrzData.payload)
            if (sIdCase.isOk) {
                checkStatus(sIdCase.value, navArgs.mrzData)
            }
        }.trackCompletion(isLoading)
    }

    private suspend fun checkStatus(caseResponse: CaseResponse, mrzData: MrzData) {
        fetchSIdStatus.invoke(caseResponse.caseId)
            .onSuccess { stateResponse ->
                val rawMrz = mrzData.payload.mrz.joinToString(";")
                saveData(rawMrz, caseResponse, stateResponse)

                if (isLegalCaseNeeded(stateResponse.legalRepresentant)) {
                    navManager.navigateToAndClearCurrent(
                        EIdGuardianSelectionScreenDestination(
                            sIdCaseId = caseResponse.caseId
                        )
                    )
                } else if (stateResponse.state == EIdRequestQueueState.READY_FOR_ONLINE_SESSION) {
                    navManager.navigateToAndClearCurrent(
                        direction = EIdWalletPairingScreenDestination
                    )
                } else {
                    navManager.navigateToAndClearCurrent(
                        EIdQueueScreenDestination(
                            navArgs = EIdQueueNavArg(
                                rawDeadline = stateResponse.queueInformation?.expectedOnlineSessionStart
                            )
                        )
                    )
                }
            }
            .onFailure {
                navManager.navigateBackToHome(EIdIntroScreenDestination)
            }
    }

    private suspend fun saveData(rawMrz: String, applyResponseBody: CaseResponse, stateResponseBody: StateResponse) {
        val eIdRequestCase = EIdRequestCase(
            id = applyResponseBody.caseId,
            rawMrz = rawMrz,
            documentNumber = applyResponseBody.identityNumber,
            firstName = applyResponseBody.givenNames,
            lastName = applyResponseBody.surname,
        )

        val onlineSessionStartOpenAt: Long? = runSuspendCatching {
            Instant.parse(stateResponseBody.queueInformation?.expectedOnlineSessionStart).epochSecond
        }.get()

        val onlineSessionStartTimeoutAt: Long? = runSuspendCatching {
            Instant.parse(stateResponseBody.onlineSessionStartTimeout).epochSecond
        }.get()

        val eIdRequestState = EIdRequestState(
            eIdRequestCaseId = applyResponseBody.caseId,
            state = stateResponseBody.state,
            lastPolled = Instant.now().epochSecond,
            onlineSessionStartOpenAt = onlineSessionStartOpenAt,
            onlineSessionStartTimeoutAt = onlineSessionStartTimeoutAt,
            legalRepresentativeConsent = stateResponseBody.toLegalRepresentativeConsent(),
        )

        saveEIdRequestCase(eIdRequestCase)
        saveEIdRequestState(eIdRequestState)
    }

    private fun isLegalCaseNeeded(legalRepresentant: LegalRepresentant?): Boolean = when {
        legalRepresentant != null && legalRepresentant.verified.not() -> true
        else -> false
    }

    init {
        onRefreshState()
    }

    fun onClose() = navManager.popBackStackTo(HomeScreenDestination, false)

    fun onRetry() = onRefreshState()
}
