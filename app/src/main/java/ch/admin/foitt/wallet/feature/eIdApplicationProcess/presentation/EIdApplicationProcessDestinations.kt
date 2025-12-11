package ch.admin.foitt.wallet.feature.eIdApplicationProcess.presentation

import ch.admin.foitt.wallet.feature.walletPairing.presentation.EIdPairingOverviewScreen
import ch.admin.foitt.wallet.feature.walletPairing.presentation.EIdPairingOverviewViewModel
import ch.admin.foitt.wallet.feature.walletPairing.presentation.EIdWalletPairingQrCodeScreen
import ch.admin.foitt.wallet.feature.walletPairing.presentation.EIdWalletPairingQrCodeViewModel
import ch.admin.foitt.wallet.feature.walletPairing.presentation.EIdWalletPairingScreen
import ch.admin.foitt.wallet.feature.walletPairing.presentation.EIdWalletPairingViewModel
import ch.admin.foitt.wallet.platform.scaffold.extension.screenDestination
import ch.admin.foitt.walletcomposedestinations.destinations.EIdAttestationScreenDestination
import ch.admin.foitt.walletcomposedestinations.destinations.EIdDocumentSelectionScreenDestination
import ch.admin.foitt.walletcomposedestinations.destinations.EIdGuardianConsentResultScreenDestination
import ch.admin.foitt.walletcomposedestinations.destinations.EIdGuardianConsentScreenDestination
import ch.admin.foitt.walletcomposedestinations.destinations.EIdGuardianSelectionScreenDestination
import ch.admin.foitt.walletcomposedestinations.destinations.EIdGuardianVerificationScreenDestination
import ch.admin.foitt.walletcomposedestinations.destinations.EIdGuardianshipScreenDestination
import ch.admin.foitt.walletcomposedestinations.destinations.EIdIntroScreenDestination
import ch.admin.foitt.walletcomposedestinations.destinations.EIdPairingOverviewScreenDestination
import ch.admin.foitt.walletcomposedestinations.destinations.EIdPrivacyPolicyScreenDestination
import ch.admin.foitt.walletcomposedestinations.destinations.EIdProcessDataScreenDestination
import ch.admin.foitt.walletcomposedestinations.destinations.EIdQueueScreenDestination
import ch.admin.foitt.walletcomposedestinations.destinations.EIdReadyForAvScreenDestination
import ch.admin.foitt.walletcomposedestinations.destinations.EIdStartAvSessionScreenDestination
import ch.admin.foitt.walletcomposedestinations.destinations.EIdStartSelfieVideoScreenDestination
import ch.admin.foitt.walletcomposedestinations.destinations.EIdWalletPairingQrCodeScreenDestination
import ch.admin.foitt.walletcomposedestinations.destinations.EIdWalletPairingScreenDestination
import com.ramcosta.composedestinations.manualcomposablecalls.ManualComposableCallsBuilder

fun ManualComposableCallsBuilder.eIdApplicationProcessDestinations() {
    screenDestination(EIdIntroScreenDestination) { viewModel: EIdIntroViewModel ->
        EIdIntroScreen(viewModel)
    }

    screenDestination(EIdPrivacyPolicyScreenDestination) { viewModel: EIdPrivacyPolicyViewModel ->
        EIdPrivacyPolicyScreen(viewModel)
    }

    screenDestination(EIdAttestationScreenDestination) { viewModel: EIdAttestationViewModel ->
        EIdAttestationScreen(viewModel)
    }

    screenDestination(EIdGuardianshipScreenDestination) { viewModel: EIdGuardianshipViewModel ->
        EIdGuardianshipScreen(viewModel)
    }

    screenDestination(EIdWalletPairingScreenDestination) { viewModel: EIdWalletPairingViewModel ->
        EIdWalletPairingScreen(viewModel)
    }

    screenDestination(EIdGuardianConsentResultScreenDestination) { viewModel: EIdGuardianConsentResultViewModel ->
        EIdGuardianConsentResultScreen(viewModel)
    }

    screenDestination(EIdGuardianSelectionScreenDestination) { viewModel: EIdGuardianSelectionViewModel ->
        EIdGuardianSelectionScreen(viewModel)
    }

    screenDestination(EIdGuardianVerificationScreenDestination) { viewModel: EIdGuardianVerificationViewModel ->
        EIdGuardianVerificationScreen(viewModel)
    }

    screenDestination(EIdGuardianConsentScreenDestination) { viewModel: EIdGuardianConsentViewModel ->
        EIdGuardianConsentScreen(viewModel)
    }

    screenDestination(EIdReadyForAvScreenDestination) { viewModel: EIdReadyForAvViewModel ->
        EIdReadyForAvScreen(viewModel)
    }

    screenDestination(EIdQueueScreenDestination) { viewModel: EIdQueueViewModel ->
        EIdQueueScreen(viewModel)
    }

    screenDestination(EIdStartAvSessionScreenDestination) { viewModel: EIdStartAvSessionViewModel ->
        EIdStartAvSessionScreen(viewModel)
    }

    screenDestination(EIdDocumentSelectionScreenDestination) { viewModel: EIdDocumentSelectionViewModel ->
        EIdDocumentSelectionScreen(viewModel)
    }

    screenDestination(EIdStartSelfieVideoScreenDestination) { viewModel: EIdStartSelfieVideoViewModel ->
        EIdStartSelfieVideoScreen(viewModel)
    }

    screenDestination(EIdProcessDataScreenDestination) { viewModel: EIdProcessDataViewModel ->
        EIdProcessDataScreen(viewModel)
    }
    screenDestination(EIdPairingOverviewScreenDestination) { viewModel: EIdPairingOverviewViewModel ->
        EIdPairingOverviewScreen(viewModel)
    }
    screenDestination(EIdWalletPairingQrCodeScreenDestination) { viewModel: EIdWalletPairingQrCodeViewModel ->
        EIdWalletPairingQrCodeScreen(viewModel)
    }
}
