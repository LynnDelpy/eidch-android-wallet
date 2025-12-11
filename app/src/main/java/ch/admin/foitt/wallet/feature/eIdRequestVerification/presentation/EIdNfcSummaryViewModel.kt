package ch.admin.foitt.wallet.feature.eIdRequestVerification.presentation

import androidx.lifecycle.SavedStateHandle
import ch.admin.foitt.wallet.platform.navArgs.domain.model.EIdNfcSummaryNavArg
import ch.admin.foitt.wallet.platform.navigation.NavigationManager
import ch.admin.foitt.wallet.platform.scaffold.domain.model.TopBarState
import ch.admin.foitt.wallet.platform.scaffold.domain.usecase.SetTopBarState
import ch.admin.foitt.wallet.platform.scaffold.presentation.ScreenViewModel
import ch.admin.foitt.walletcomposedestinations.destinations.EIdNfcSummaryScreenDestination
import ch.admin.foitt.walletcomposedestinations.destinations.EIdStartSelfieVideoScreenDestination
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class EIdNfcSummaryViewModel @Inject constructor(
    private val navManager: NavigationManager,
    savedStateHandle: SavedStateHandle,
    setTopBarState: SetTopBarState,
) : ScreenViewModel(setTopBarState) {
    override val topBarState = TopBarState.EmptyWithCloseButton(::onClose)

    private val navArgs: EIdNfcSummaryNavArg = EIdNfcSummaryScreenDestination.argsFrom(savedStateHandle)

    val picture = navArgs.picture
    val givenName = navArgs.givenName
    val surname = navArgs.surname
    val documentId = navArgs.documentId
    val expiryDate = navArgs.expiryDate

    fun onContinue() = navManager.navigateToAndClearCurrent(
        EIdStartSelfieVideoScreenDestination(
            caseId = navArgs.caseId
        )
    )

    private fun onClose() {
        navManager.navigateBackToHome(popUntil = EIdNfcSummaryScreenDestination)
    }
}
