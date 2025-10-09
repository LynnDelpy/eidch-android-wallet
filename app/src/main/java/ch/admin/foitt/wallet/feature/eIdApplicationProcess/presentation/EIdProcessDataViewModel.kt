package ch.admin.foitt.wallet.feature.eIdApplicationProcess.presentation

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import ch.admin.foitt.wallet.R
import ch.admin.foitt.wallet.feature.eIdApplicationProcess.presentation.model.ProcessDataUiState
import ch.admin.foitt.wallet.platform.eIdApplicationProcess.domain.model.AvRepositoryError
import ch.admin.foitt.wallet.platform.eIdApplicationProcess.domain.model.EIdRequestError
import ch.admin.foitt.wallet.platform.navigation.NavigationManager
import ch.admin.foitt.wallet.platform.scaffold.domain.model.TopBarState
import ch.admin.foitt.wallet.platform.scaffold.domain.usecase.SetTopBarState
import ch.admin.foitt.wallet.platform.scaffold.presentation.ScreenViewModel
import ch.admin.foitt.wallet.platform.utils.openLink
import ch.admin.foitt.wallet.platform.utils.trackCompletion
import ch.admin.foitt.walletcomposedestinations.destinations.EIdIntroScreenDestination
import ch.admin.foitt.walletcomposedestinations.destinations.EIdProcessDataScreenDestination
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.unwrapError
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class EIdProcessDataViewModel @Inject constructor(
    private val navManager: NavigationManager,
    @param:ApplicationContext private val context: Context,
    setTopBarState: SetTopBarState,
    savedStateHandle: SavedStateHandle,
) : ScreenViewModel(setTopBarState) {
    override val topBarState = TopBarState.Empty

    private val navArgs = EIdProcessDataScreenDestination.argsFrom(savedStateHandle)

    private val isLoading = MutableStateFlow(false)

    private val uploadFilesResult = MutableStateFlow<Result<Unit, AvRepositoryError>?>(null)

    val state: StateFlow<ProcessDataUiState> = combine(
        isLoading,
        uploadFilesResult,
    ) { isLoading, uploadFiles ->
        when {
            isLoading -> ProcessDataUiState.Loading
            uploadFiles?.isOk == true -> ProcessDataUiState.Valid
            uploadFiles?.unwrapError() is EIdRequestError.NetworkError ->
                ProcessDataUiState.GenericError(
                    onClose = ::onClose,
                    onRetry = ::onRetry,
                    onHelp = ::onHelp
                )

            uploadFiles?.unwrapError() is EIdRequestError.DeclinedProcessData ->
                ProcessDataUiState.Declined(
                    onClose = ::onClose,
                    onHelp = ::onHelp
                )

            else -> ProcessDataUiState.GenericError(
                onClose = ::onClose,
                onRetry = ::onRetry,
                onHelp = ::onHelp
            )
        }
    }.toStateFlow(ProcessDataUiState.Loading)

    private fun onRefreshState() {
        if (isLoading.value) {
            return
        }
        viewModelScope.launch {
            val caseId = navArgs.caseId
            // uploadFilesResult.value = uploadAllFiles(caseId = navArgs.caseId)
        }.trackCompletion(isLoading)
    }

    init {
        onRefreshState()
    }

    fun onClose() = navManager.navigateBackToHome(EIdIntroScreenDestination)

    fun onRetry() = onRefreshState()

    fun onHelp() = context.openLink(context.getString(R.string.tk_eidRequest_dataProcess_link_text))
}
