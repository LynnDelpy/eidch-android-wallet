package ch.admin.foitt.wallet.platform.nonCompliance.presentation

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import ch.admin.foitt.wallet.R
import ch.admin.foitt.wallet.platform.navigation.NavigationManager
import ch.admin.foitt.wallet.platform.nonCompliance.domain.model.NonComplianceReportReason
import ch.admin.foitt.wallet.platform.scaffold.domain.model.TopBarState
import ch.admin.foitt.wallet.platform.scaffold.domain.usecase.SetTopBarState
import ch.admin.foitt.wallet.platform.scaffold.presentation.ScreenViewModel
import ch.admin.foitt.wallet.platform.utils.openLink
import ch.admin.foitt.walletcomposedestinations.destinations.NonComplianceInfoScreenDestination
import ch.admin.foitt.walletcomposedestinations.destinations.NonComplianceListScreenDestination
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@HiltViewModel
class NonComplianceInfoViewModel @Inject constructor(
    @param:ApplicationContext private val appContext: Context,
    private val navManager: NavigationManager,
    setTopBarState: SetTopBarState,
    savedStateHandle: SavedStateHandle
) : ScreenViewModel(setTopBarState) {
    private val navArgs = NonComplianceInfoScreenDestination.argsFrom(savedStateHandle)
    val reportReason = navArgs.nonComplianceReportReason

    val titleId = when (reportReason) {
        NonComplianceReportReason.EXCESSIVE_DATA_REQUEST -> R.string.tk_nonCompliance_reportExcessiveData_title
    }

    override val topBarState = TopBarState.DetailsWithCloseButton(
        titleId = titleId,
        useTransparentBackground = false,
        onClose = this::onClose,
        onUp = this::onBack
    )

    private fun onBack() {
        navManager.navigateUp()
    }

    private fun onClose() {
        navManager.popBackStackTo(destination = NonComplianceListScreenDestination, inclusive = true)
    }

    fun onMoreInformation() {
        val linkValue = when (reportReason) {
            NonComplianceReportReason.EXCESSIVE_DATA_REQUEST -> R.string.tk_nonCompliance_reportExcessiveData_moreInformation_link_value
        }

        appContext.openLink(linkValue)
    }

    fun onContinue() {
    }
}
