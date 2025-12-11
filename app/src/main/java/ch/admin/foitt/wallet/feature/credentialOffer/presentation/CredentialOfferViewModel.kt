package ch.admin.foitt.wallet.feature.credentialOffer.presentation

import android.content.Context
import androidx.annotation.StringRes
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import ch.admin.foitt.wallet.R
import ch.admin.foitt.wallet.feature.credentialOffer.domain.model.CredentialOffer
import ch.admin.foitt.wallet.feature.credentialOffer.domain.usecase.GetCredentialOfferFlow
import ch.admin.foitt.wallet.feature.credentialOffer.presentation.model.CredentialOfferUiState
import ch.admin.foitt.wallet.platform.activityList.domain.usecase.SaveIssuanceActivity
import ch.admin.foitt.wallet.platform.actorMetadata.domain.usecase.GetActorForScope
import ch.admin.foitt.wallet.platform.actorMetadata.presentation.adapter.GetActorUiState
import ch.admin.foitt.wallet.platform.actorMetadata.presentation.model.ActorUiState
import ch.admin.foitt.wallet.platform.appSetupState.domain.usecase.SaveFirstCredentialWasAdded
import ch.admin.foitt.wallet.platform.badges.domain.model.BadgeType
import ch.admin.foitt.wallet.platform.badges.presentation.model.BadgeBottomSheetUiState
import ch.admin.foitt.wallet.platform.badges.presentation.model.toBadgeBottomSheetUiState
import ch.admin.foitt.wallet.platform.credential.presentation.adapter.GetCredentialCardState
import ch.admin.foitt.wallet.platform.credentialStatus.domain.usecase.UpdateCredentialStatus
import ch.admin.foitt.wallet.platform.messageEvents.domain.model.CredentialOfferEvent
import ch.admin.foitt.wallet.platform.messageEvents.domain.repository.CredentialOfferEventRepository
import ch.admin.foitt.wallet.platform.navigation.NavigationManager
import ch.admin.foitt.wallet.platform.navigation.domain.model.ComponentScope
import ch.admin.foitt.wallet.platform.scaffold.domain.model.TopBarState
import ch.admin.foitt.wallet.platform.scaffold.domain.usecase.SetTopBarState
import ch.admin.foitt.wallet.platform.scaffold.extension.navigateUpOrToRoot
import ch.admin.foitt.wallet.platform.scaffold.extension.refreshableStateFlow
import ch.admin.foitt.wallet.platform.scaffold.presentation.ScreenViewModel
import ch.admin.foitt.wallet.platform.trustRegistry.domain.model.TrustStatus
import ch.admin.foitt.wallet.platform.utils.openLink
import ch.admin.foitt.walletcomposedestinations.destinations.CredentialOfferDeclinedScreenDestination
import ch.admin.foitt.walletcomposedestinations.destinations.CredentialOfferScreenDestination
import ch.admin.foitt.walletcomposedestinations.destinations.DeclineCredentialOfferScreenDestination
import ch.admin.foitt.walletcomposedestinations.destinations.ErrorScreenDestination
import ch.admin.foitt.walletcomposedestinations.destinations.ReportWrongDataScreenDestination
import com.github.michaelbull.result.mapBoth
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CredentialOfferViewModel @Inject constructor(
    @param:ApplicationContext private val appContext: Context,
    savedStateHandle: SavedStateHandle,
    getCredentialOfferFlow: GetCredentialOfferFlow,
    private val navManager: NavigationManager,
    private val updateCredentialStatus: UpdateCredentialStatus,
    private val getCredentialCardState: GetCredentialCardState,
    private val saveFirstCredentialWasAdded: SaveFirstCredentialWasAdded,
    private val getActorUiState: GetActorUiState,
    getActorForScope: GetActorForScope,
    private val credentialOfferEventRepository: CredentialOfferEventRepository,
    private val saveIssuanceActivity: SaveIssuanceActivity,
    setTopBarState: SetTopBarState,
) : ScreenViewModel(setTopBarState) {
    override val topBarState = TopBarState.None

    private val navArgs = CredentialOfferScreenDestination.argsFrom(savedStateHandle)
    private val credentialId = navArgs.credentialId

    private val _badgeBottomSheetUiState: MutableStateFlow<BadgeBottomSheetUiState?> = MutableStateFlow(null)
    val badgeBottomSheet = _badgeBottomSheetUiState.asStateFlow()

    private val _showConfirmationBottomSheet: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val showConfirmationBottomSheet = _showConfirmationBottomSheet.asStateFlow()

    private val actorDisplayData = getActorForScope(ComponentScope.CredentialIssuer)
    private val issuerUiState = actorDisplayData.map { displayData ->
        getActorUiState(
            actorDisplayData = displayData,
        )
    }.toStateFlow(ActorUiState.EMPTY, 0)

    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()

    private val credentialOffer: StateFlow<CredentialOffer?> = getCredentialOfferFlow(credentialId)
        .map { result ->
            result.mapBoth(
                success = { credentialOffer ->
                    _isLoading.value = false
                    credentialOffer
                },
                failure = {
                    navigateToErrorScreen()
                    null
                }
            )
        }.toStateFlow(null)

    val credentialOfferUiState = refreshableStateFlow(initialData = CredentialOfferUiState.EMPTY) {
        combine(
            credentialOffer,
            issuerUiState,
        ) { credentialOffer, issuerUiState ->
            credentialOffer?.let {
                CredentialOfferUiState(
                    issuer = issuerUiState,
                    credential = getCredentialCardState(credentialOffer.credential),
                    claims = credentialOffer.claims,
                )
            }
        }.filterNotNull()
    }

    init {
        viewModelScope.launch {
            updateCredentialStatus(credentialId)
        }
    }

    fun onAcceptClicked() = if (issuerUiState.value.trustStatus != TrustStatus.EXTERNAL) {
        acceptCredential()
    } else {
        _showConfirmationBottomSheet.value = true
    }

    fun acceptCredential() = viewModelScope.launch {
        saveFirstCredentialWasAdded()
        saveIssuanceActivity(
            credentialId = credentialId,
            actorDisplayData = actorDisplayData.value,
            issuerFallbackName = appContext.getString(R.string.tk_credential_offer_issuer_name_unknown)
        )
        credentialOfferEventRepository.setEvent(CredentialOfferEvent.ACCEPTED)
        navManager.navigateUpOrToRoot()
    }

    fun onDeclineClicked() {
        credentialOffer.value?.let { credentialOffer ->
            navManager.navigateTo(
                DeclineCredentialOfferScreenDestination(
                    credentialId = credentialId,
                )
            )
        } ?: navigateToErrorScreen()
    }

    fun onDeclineBottomSheet() {
        credentialOffer.value?.let { credentialOffer ->
            navManager.navigateTo(
                CredentialOfferDeclinedScreenDestination(
                    credentialId = credentialId,
                )
            )
        } ?: navigateToErrorScreen()
    }

    fun onBadge(badgeType: BadgeType) {
        _badgeBottomSheetUiState.value = when (badgeType) {
            is BadgeType.ActorInfoBadge -> badgeType.toBadgeBottomSheetUiState(
                actorName = issuerUiState.value.name ?: "",
                reason = issuerUiState.value.nonComplianceReason,
                onMoreInformation = { onMoreInformation(R.string.tk_badgeInformation_furtherInformation_link_value) },
            )

            is BadgeType.ClaimInfoBadge -> badgeType.toBadgeBottomSheetUiState(
                onMoreInformation = { onMoreInformation(R.string.tk_badgeInformation_furtherInformation_link_value) },
            )
        }
    }

    fun onDismissBadgeBottomSheet() {
        _badgeBottomSheetUiState.value = null
    }

    fun onDismissConfirmationBottomSheet() {
        _showConfirmationBottomSheet.value = false
    }

    private fun navigateToErrorScreen() {
        navManager.navigateToAndClearCurrent(ErrorScreenDestination)
    }

    fun onReportWrongDataClicked() {
        navManager.navigateTo(ReportWrongDataScreenDestination)
    }

    private fun onMoreInformation(@StringRes uriResource: Int) = appContext.openLink(uriResource)
}
