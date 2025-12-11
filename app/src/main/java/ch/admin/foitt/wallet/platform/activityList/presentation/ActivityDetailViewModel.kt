package ch.admin.foitt.wallet.platform.activityList.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import ch.admin.foitt.wallet.R
import ch.admin.foitt.wallet.platform.activityList.domain.model.ActivityDetail
import ch.admin.foitt.wallet.platform.activityList.domain.usecase.DeleteActivity
import ch.admin.foitt.wallet.platform.activityList.domain.usecase.GetActivityDetailFlow
import ch.admin.foitt.wallet.platform.activityList.presentation.model.ActivityDetailUiState
import ch.admin.foitt.wallet.platform.activityList.presentation.model.toActivityUiState
import ch.admin.foitt.wallet.platform.composables.presentation.adapter.GetDrawableFromImageData
import ch.admin.foitt.wallet.platform.credential.presentation.adapter.GetCredentialCardState
import ch.admin.foitt.wallet.platform.environmentSetup.domain.repository.EnvironmentSetupRepository
import ch.admin.foitt.wallet.platform.messageEvents.domain.model.ActivityEvent
import ch.admin.foitt.wallet.platform.messageEvents.domain.repository.ActivityEventRepository
import ch.admin.foitt.wallet.platform.navArgs.domain.model.NonComplianceListNavArg
import ch.admin.foitt.wallet.platform.navigation.NavigationManager
import ch.admin.foitt.wallet.platform.scaffold.domain.model.TopBarState
import ch.admin.foitt.wallet.platform.scaffold.domain.usecase.SetTopBarState
import ch.admin.foitt.wallet.platform.scaffold.presentation.ScreenViewModel
import ch.admin.foitt.wallet.platform.utils.toPainter
import ch.admin.foitt.walletcomposedestinations.destinations.ActivityDetailScreenDestination
import ch.admin.foitt.walletcomposedestinations.destinations.ErrorScreenDestination
import ch.admin.foitt.walletcomposedestinations.destinations.NonComplianceListScreenDestination
import com.github.michaelbull.result.mapBoth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ActivityDetailViewModel @Inject constructor(
    getActivityDetailFlow: GetActivityDetailFlow,
    private val getDrawableFromImageData: GetDrawableFromImageData,
    private val getCredentialCardState: GetCredentialCardState,
    private val deleteActivity: DeleteActivity,
    private val activityEventRepository: ActivityEventRepository,
    environmentSetupRepository: EnvironmentSetupRepository,
    private val navManager: NavigationManager,
    setTopBarState: SetTopBarState,
    savedStateHandle: SavedStateHandle
) : ScreenViewModel(setTopBarState) {
    override val topBarState = TopBarState.Details(
        titleId = R.string.tk_activity_activityDetail_title,
        useTransparentBackground = false,
        onUp = this::onBack
    )

    private val navArgs = ActivityDetailScreenDestination.argsFrom(savedStateHandle)

    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()

    val nonComplianceEnabled = environmentSetupRepository.nonComplianceEnabled

    private val _showConfirmationBottomSheet: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val showConfirmationBottomSheet = _showConfirmationBottomSheet.asStateFlow()

    val activity = getActivityDetailFlow(navArgs.credentialId, navArgs.activityId)
        .map { result ->
            result.mapBoth(
                success = { activityDetail ->
                    _isLoading.value = false
                    mapToUiState(activityDetail)
                },
                failure = {
                    navigateToErrorScreen()
                    null
                }
            )
        }.filterNotNull()
        .toStateFlow(ActivityDetailUiState.EMPTY)

    private suspend fun mapToUiState(activityDetail: ActivityDetail?) = when (activityDetail) {
        null -> ActivityDetailUiState.EMPTY
        else -> {
            val drawable = activityDetail.activity.actorImageData?.let {
                getDrawableFromImageData(it)
            }

            ActivityDetailUiState(
                activity = activityDetail.activity.toActivityUiState(drawable?.toPainter()),
                credential = getCredentialCardState(activityDetail.credential).copy(status = null),
                claims = activityDetail.claims,
            )
        }
    }

    fun onDeleteActivity() {
        _showConfirmationBottomSheet.value = true
    }

    fun hideConfirmationBottomSheet() {
        _showConfirmationBottomSheet.value = false
    }

    fun deleteActivity() = viewModelScope.launch {
        deleteActivity(navArgs.activityId)
        activityEventRepository.setEvent(ActivityEvent.DELETED)
        onBack()
    }

    fun onReportActor() {
        navManager.navigateTo(
            NonComplianceListScreenDestination(
                NonComplianceListNavArg(activityType = activity.value.activity.activityType)
            )
        )
    }

    fun onBack() {
        navManager.navigateUp()
    }

    private fun navigateToErrorScreen() {
        navManager.navigateToAndClearCurrent(ErrorScreenDestination)
    }
}
