package ch.admin.foitt.wallet.feature.home.presentation

import android.content.Context
import androidx.lifecycle.viewModelScope
import ch.admin.foitt.wallet.R
import ch.admin.foitt.wallet.feature.home.domain.usecase.DeleteEIdRequestCase
import ch.admin.foitt.wallet.feature.home.domain.usecase.GetEIdRequestsFlow
import ch.admin.foitt.wallet.platform.credential.domain.model.CredentialDisplayData
import ch.admin.foitt.wallet.platform.credential.presentation.adapter.GetCredentialCardState
import ch.admin.foitt.wallet.platform.credential.presentation.model.CredentialCardState
import ch.admin.foitt.wallet.platform.credentialStatus.domain.usecase.UpdateAllCredentialStatuses
import ch.admin.foitt.wallet.platform.eIdApplicationProcess.domain.model.SIdRequestDisplayData
import ch.admin.foitt.wallet.platform.eIdApplicationProcess.domain.model.SIdRequestDisplayStatus
import ch.admin.foitt.wallet.platform.eIdApplicationProcess.domain.usecase.UpdateAllSIdStatuses
import ch.admin.foitt.wallet.platform.environmentSetup.domain.repository.EnvironmentSetupRepository
import ch.admin.foitt.wallet.platform.messageEvents.domain.model.CredentialOfferEvent
import ch.admin.foitt.wallet.platform.messageEvents.domain.repository.CredentialOfferEventRepository
import ch.admin.foitt.wallet.platform.navigation.NavigationManager
import ch.admin.foitt.wallet.platform.scaffold.domain.model.TopBarState
import ch.admin.foitt.wallet.platform.scaffold.domain.usecase.SetTopBarState
import ch.admin.foitt.wallet.platform.scaffold.extension.refreshableStateFlow
import ch.admin.foitt.wallet.platform.scaffold.presentation.ScreenViewModel
import ch.admin.foitt.wallet.platform.ssi.domain.usecase.GetCredentialsWithDetailsFlow
import ch.admin.foitt.wallet.platform.utils.openLink
import ch.admin.foitt.wallet.platform.utils.trackCompletion
import ch.admin.foitt.walletcomposedestinations.destinations.BetaIdScreenDestination
import ch.admin.foitt.walletcomposedestinations.destinations.CredentialDetailScreenDestination
import ch.admin.foitt.walletcomposedestinations.destinations.EIdGuardianSelectionScreenDestination
import ch.admin.foitt.walletcomposedestinations.destinations.EIdIntroScreenDestination
import ch.admin.foitt.walletcomposedestinations.destinations.EIdStartAvSessionScreenDestination
import ch.admin.foitt.walletcomposedestinations.destinations.ErrorScreenDestination
import ch.admin.foitt.walletcomposedestinations.destinations.QrScanPermissionScreenDestination
import ch.admin.foitt.walletcomposedestinations.destinations.SettingsScreenDestination
import com.ramcosta.composedestinations.spec.Direction
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
@Suppress("TooManyFunctions")
class HomeViewModel @Inject constructor(
    @param:ApplicationContext private val appContext: Context,
    getCredentialsWithDetailsFlow: GetCredentialsWithDetailsFlow,
    getEIdRequestsFlow: GetEIdRequestsFlow,
    private val getCredentialCardState: GetCredentialCardState,
    private val updateAllCredentialStatuses: UpdateAllCredentialStatuses,
    private val updateAllSIdStatuses: UpdateAllSIdStatuses,
    private val deleteEIdRequestCase: DeleteEIdRequestCase,
    environmentSetupRepository: EnvironmentSetupRepository,
    private val navManager: NavigationManager,
    private val credentialOfferEventRepository: CredentialOfferEventRepository,
    setTopBarState: SetTopBarState,
) : ScreenViewModel(setTopBarState) {
    override val topBarState = TopBarState.None

    private val _eventMessage = MutableStateFlow<Int?>(null)
    val eventMessage = _eventMessage.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing = _isRefreshing.asStateFlow()

    val screenState = refreshableStateFlow(initialData = HomeScreenState.Initial) {
        combine(
            getCredentialsWithDetailsFlow(),
            getEIdRequestsFlow(),
        ) { homeDataFlow, eIdRequestsFlow ->
            when {
                homeDataFlow.isOk && eIdRequestsFlow.isOk -> mapToUiState(homeDataFlow.value, eIdRequestsFlow.value)
                else -> {
                    navigateTo(ErrorScreenDestination)
                    null
                }
            }
        }.filterNotNull()
    }

    private val _homeContainerState = MutableStateFlow(
        HomeContainerState(
            showEIdRequestButton = environmentSetupRepository.eIdRequestEnabled,
            showBetaIdRequestButton = environmentSetupRepository.betaIdRequestEnabled,
            showMenu = false,
            onScan = { navigateTo(QrScanPermissionScreenDestination) },
            onGetEId = { navigateTo(EIdIntroScreenDestination) },
            onGetBetaId = { navigateTo(BetaIdScreenDestination) },
            onSettings = { navigateTo(SettingsScreenDestination) },
            onHelp = { onHelp() },
        )
    )
    val homeContainerState = _homeContainerState.asStateFlow()

    init {
        viewModelScope.launch {
            credentialOfferEventRepository.event.collect { event ->
                _eventMessage.value = when (event) {
                    CredentialOfferEvent.ACCEPTED -> R.string.tk_home_notification_credential_accepted
                    CredentialOfferEvent.DECLINED -> R.string.tk_home_notification_credential_declined
                    CredentialOfferEvent.NONE -> null
                }
                if (_eventMessage.value != null) {
                    delay(4000L)
                    credentialOfferEventRepository.resetEvent()
                }
            }
        }
    }

    private suspend fun mapToUiState(
        credentials: List<CredentialDisplayData>,
        eIdRequestCases: List<SIdRequestDisplayData>
    ): HomeScreenState = when {
        credentials.isNotEmpty() -> {
            HomeScreenState.CredentialList(
                eIdRequests = filterEIdRequests(eIdRequestCases),
                credentials = getCredentialStateList(credentials),
                onCredentialClick = { id -> navigateTo(CredentialDetailScreenDestination(credentialId = id)) },
            )
        }

        else -> HomeScreenState.NoCredential(
            eIdRequests = filterEIdRequests(eIdRequestCases),
        )
    }

    private fun filterEIdRequests(eIdRequestCases: List<SIdRequestDisplayData>): List<SIdRequestDisplayData> {
        return eIdRequestCases.filter { requestCase ->
            requestCase.status != SIdRequestDisplayStatus.OTHER
        }
    }

    private suspend fun getCredentialStateList(credentialDisplayData: List<CredentialDisplayData>): List<CredentialCardState> {
        return credentialDisplayData.map { credentialPreview ->
            getCredentialCardState(
                credentialPreview
            )
        }
    }

    fun onRefresh() {
        viewModelScope.launch {
            updateAllCredentialStatuses()
            updateAllSIdStatuses()
        }.trackCompletion(_isRefreshing)
    }

    fun onRefreshSIdStatuses() {
        if (!isRefreshing.value) {
            viewModelScope.launch {
                updateAllSIdStatuses()
            }.trackCompletion(_isRefreshing)
        }
    }

    fun onCloseToast() {
        _eventMessage.value = null
        credentialOfferEventRepository.resetEvent()
    }

    fun onStartOnlineIdentification(caseId: String) {
        navigateTo(EIdStartAvSessionScreenDestination(caseId = caseId))
    }

    fun onObtainConsent(caseId: String) {
        navigateTo(EIdGuardianSelectionScreenDestination(caseId = caseId))
    }

    fun onCloseEId(caseId: String) {
        viewModelScope.launch {
            deleteEIdRequestCase(caseId)
        }
    }

    fun onMenu(showMenu: Boolean) {
        _homeContainerState.update { currentState ->
            currentState.copy(showMenu = showMenu)
        }
    }

    fun navigateTo(direction: Direction) {
        // hide menu on navigation, so when coming back it is closed
        onMenu(false)

        navManager.navigateTo(direction)
    }

    fun onHelp() {
        // hide menu on navigation, so when coming back it is closed
        onMenu(false)
        appContext.openLink(R.string.tk_settings_general_help_link_value)
    }
}
