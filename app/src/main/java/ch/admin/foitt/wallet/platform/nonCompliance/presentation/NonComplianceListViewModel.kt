package ch.admin.foitt.wallet.platform.nonCompliance.presentation

import androidx.lifecycle.SavedStateHandle
import ch.admin.foitt.wallet.R
import ch.admin.foitt.wallet.platform.activityList.domain.model.ActivityType
import ch.admin.foitt.wallet.platform.navArgs.domain.model.NonComplianceInfoNavArg
import ch.admin.foitt.wallet.platform.navigation.NavigationManager
import ch.admin.foitt.wallet.platform.nonCompliance.domain.model.NonComplianceReportReason
import ch.admin.foitt.wallet.platform.scaffold.domain.model.TopBarState
import ch.admin.foitt.wallet.platform.scaffold.domain.usecase.SetTopBarState
import ch.admin.foitt.wallet.platform.scaffold.presentation.ScreenViewModel
import ch.admin.foitt.walletcomposedestinations.destinations.NonComplianceInfoScreenDestination
import ch.admin.foitt.walletcomposedestinations.destinations.NonComplianceListScreenDestination
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class NonComplianceListViewModel @Inject constructor(
    private val navManager: NavigationManager,
    setTopBarState: SetTopBarState,
    savedStateHandle: SavedStateHandle
) : ScreenViewModel(setTopBarState) {
    override val topBarState = TopBarState.Details(
        titleId = R.string.tk_nonComplianceList_title,
        useTransparentBackground = false,
        onUp = this::onBack
    )

    private val navArgs = NonComplianceListScreenDestination.argsFrom(savedStateHandle)

    private val issuanceReportingReasons = listOf<NonComplianceReportReason>()
    private val verificationReportingReasons = listOf(NonComplianceReportReason.EXCESSIVE_DATA_REQUEST)

    val reasons = when (navArgs.activityType) {
        ActivityType.ISSUANCE -> issuanceReportingReasons
        ActivityType.PRESENTATION_ACCEPTED,
        ActivityType.PRESENTATION_DECLINED -> verificationReportingReasons
    }

    fun onBack() {
        navManager.navigateUp()
    }

    fun onReason(reportReason: NonComplianceReportReason) {
        navManager.navigateTo(
            NonComplianceInfoScreenDestination(
                NonComplianceInfoNavArg(nonComplianceReportReason = reportReason)
            )
        )
    }
}
