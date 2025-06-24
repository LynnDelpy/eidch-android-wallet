package ch.admin.foitt.wallet.feature.eIdApplicationProcess.presentation

import android.content.Context
import androidx.lifecycle.viewModelScope
import ch.admin.foitt.wallet.R
import ch.admin.foitt.wallet.platform.appAttestation.domain.usecase.RequestClientAttestation
import ch.admin.foitt.wallet.platform.appAttestation.domain.usecase.RequestKeyAttestation
import ch.admin.foitt.wallet.platform.eIdApplicationProcess.domain.usecase.ValidateAttestations
import ch.admin.foitt.wallet.platform.navigation.NavigationManager
import ch.admin.foitt.wallet.platform.scaffold.domain.model.TopBarState
import ch.admin.foitt.wallet.platform.scaffold.domain.usecase.SetTopBarState
import ch.admin.foitt.wallet.platform.scaffold.presentation.ScreenViewModel
import ch.admin.foitt.wallet.platform.utils.openLink
import ch.admin.foitt.wallet.platform.utils.trackCompletion
import ch.admin.foitt.walletcomposedestinations.destinations.EIdGuardianshipScreenDestination
import ch.admin.foitt.walletcomposedestinations.destinations.EIdIntroScreenDestination
import com.github.michaelbull.result.get
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class EIdPrivacyPolicyViewModel @Inject constructor(
    private val requestKeyAttestation: RequestKeyAttestation,
    private val requestClientAttestation: RequestClientAttestation,
    private val validateAttestations: ValidateAttestations,
    @ApplicationContext private val context: Context,
    private val navManager: NavigationManager,
    setTopBarState: SetTopBarState,
) : ScreenViewModel(setTopBarState) {
    override val topBarState = TopBarState.DetailsWithCloseButton(
        titleId = null,
        onUp = navManager::popBackStack,
        onClose = { navManager.navigateBackToHome(EIdIntroScreenDestination) }
    )

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    fun onEIdPrivacyPolicy() = context.openLink(R.string.tk_getEid_dataPrivacy_link_value)

    fun onNext() {
        viewModelScope.launch {
            val clientAttestation = requestClientAttestation().get()
            Timber.d(message = "Client attestation result: $clientAttestation")
            val attestation = requestKeyAttestation().get()
            Timber.d(message = "Key Attestation result: $attestation")
            if (clientAttestation != null && attestation != null) {
                val validationResult = validateAttestations(
                    clientAttestation = clientAttestation,
                    keyAttestation = attestation,
                )
                Timber.d(message = "Validation result: $validationResult")
            }
            navManager.navigateTo(EIdGuardianshipScreenDestination)
        }.trackCompletion(_isLoading)
    }
}
