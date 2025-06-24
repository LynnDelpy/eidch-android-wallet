package ch.admin.foitt.wallet.feature.eIdApplicationProcess.presentation

import ch.admin.foitt.wallet.platform.eIdApplicationProcess.domain.model.EIdDocumentType
import ch.admin.foitt.wallet.platform.eIdApplicationProcess.domain.usecase.SetDocumentType
import ch.admin.foitt.wallet.platform.navigation.NavigationManager
import ch.admin.foitt.wallet.platform.scaffold.domain.model.TopBarState
import ch.admin.foitt.wallet.platform.scaffold.domain.usecase.SetTopBarState
import ch.admin.foitt.wallet.platform.scaffold.presentation.ScreenViewModel
import ch.admin.foitt.walletcomposedestinations.destinations.EIdIntroScreenDestination
import ch.admin.foitt.walletcomposedestinations.destinations.MrzScanPermissionScreenDestination
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class EIdDocumentSelectionViewModel @Inject constructor(
    private val navManager: NavigationManager,
    private val setDocumentType: SetDocumentType,
    setTopBarState: SetTopBarState,
) : ScreenViewModel(setTopBarState) {
    override val topBarState = TopBarState.DetailsWithCloseButton(
        titleId = null,
        onUp = navManager::popBackStack,
        onClose = { navManager.navigateBackToHome(EIdIntroScreenDestination) }
    )

    fun onDocumentSelected(documentType: EIdDocumentType) {
        setDocumentType(documentType)
        navManager.navigateTo(MrzScanPermissionScreenDestination)
    }
}
