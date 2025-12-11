package ch.admin.foitt.wallet.platform.activityList.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import ch.admin.foitt.wallet.R
import ch.admin.foitt.wallet.platform.activityList.domain.usecase.GetActivitiesWithDisplaysFlow
import ch.admin.foitt.wallet.platform.activityList.presentation.model.toActivityUiState
import ch.admin.foitt.wallet.platform.composables.presentation.adapter.GetDrawableFromImageData
import ch.admin.foitt.wallet.platform.messageEvents.domain.model.ActivityEvent
import ch.admin.foitt.wallet.platform.messageEvents.domain.repository.ActivityEventRepository
import ch.admin.foitt.wallet.platform.navArgs.domain.model.ActivityDetailNavArg
import ch.admin.foitt.wallet.platform.navigation.NavigationManager
import ch.admin.foitt.wallet.platform.scaffold.domain.model.TopBarState
import ch.admin.foitt.wallet.platform.scaffold.domain.usecase.SetTopBarState
import ch.admin.foitt.wallet.platform.scaffold.presentation.ScreenViewModel
import ch.admin.foitt.wallet.platform.utils.toPainter
import ch.admin.foitt.walletcomposedestinations.destinations.ActivityDetailScreenDestination
import ch.admin.foitt.walletcomposedestinations.destinations.ActivityListScreenDestination
import ch.admin.foitt.walletcomposedestinations.destinations.ErrorScreenDestination
import com.github.michaelbull.result.mapBoth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ActivityListViewModel @Inject constructor(
    getActivitiesWithDisplaysFlow: GetActivitiesWithDisplaysFlow,
    private val getDrawableFromImageData: GetDrawableFromImageData,
    private val activityEventRepository: ActivityEventRepository,
    private val navManager: NavigationManager,
    setTopBarState: SetTopBarState,
    savedStateHandle: SavedStateHandle
) : ScreenViewModel(setTopBarState) {
    override val topBarState = TopBarState.Details(
        titleId = R.string.tk_activity_activityList_title,
        useTransparentBackground = false,
        onUp = this::onBack
    )

    private val navArgs = ActivityListScreenDestination.argsFrom(savedStateHandle)

    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()

    val activities = getActivitiesWithDisplaysFlow(navArgs.credentialId)
        .map { result ->
            result.mapBoth(
                success = { activitiesWithDisplays ->
                    _isLoading.value = false
                    activitiesWithDisplays.map { activityDisplayData ->
                        val drawable = activityDisplayData.actorImageData?.let {
                            getDrawableFromImageData(it)
                        }

                        activityDisplayData.toActivityUiState(drawable?.toPainter())
                    }
                },
                failure = {
                    navigateToErrorScreen()
                    emptyList()
                }
            )
        }.toStateFlow(emptyList())

    private val _isSnackbarVisible = MutableStateFlow(false)
    val isSnackbarVisible = _isSnackbarVisible.asStateFlow()

    init {
        viewModelScope.launch {
            activityEventRepository.event.collect { event ->
                when (event) {
                    ActivityEvent.NONE -> _isSnackbarVisible.value = false
                    ActivityEvent.DELETED -> {
                        _isSnackbarVisible.value = true
                        delay(4000L)
                        activityEventRepository.resetEvent()
                    }
                }
            }
        }
    }

    fun hideActivityDeletedSnackbar() = activityEventRepository.resetEvent()

    fun onBack() {
        navManager.navigateUp()
    }

    fun onActivity(activityId: Long) {
        navManager.navigateTo(
            ActivityDetailScreenDestination(
                ActivityDetailNavArg(
                    credentialId = navArgs.credentialId,
                    activityId = activityId
                )
            )
        )
    }

    private fun navigateToErrorScreen() {
        navManager.navigateToAndClearCurrent(ErrorScreenDestination)
    }
}
