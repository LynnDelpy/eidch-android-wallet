package ch.admin.foitt.wallet.feature.eIdRequestVerification.presentation

import ch.admin.foitt.wallet.platform.scaffold.extension.screenDestination
import ch.admin.foitt.walletcomposedestinations.destinations.EIdDocumentRecordingScreenDestination
import ch.admin.foitt.walletcomposedestinations.destinations.EIdNfcScannerScreenDestination
import ch.admin.foitt.walletcomposedestinations.destinations.EIdStartAutoVerificationScreenDestination
import ch.admin.foitt.walletcomposedestinations.destinations.MrzChooserScreenDestination
import ch.admin.foitt.walletcomposedestinations.destinations.MrzScanPermissionScreenDestination
import ch.admin.foitt.walletcomposedestinations.destinations.MrzSubmissionScreenDestination
import ch.admin.foitt.walletcomposedestinations.destinations.SDKFaceScannerScreenDestination
import ch.admin.foitt.walletcomposedestinations.destinations.SDKScannerScreenDestination
import com.ramcosta.composedestinations.manualcomposablecalls.ManualComposableCallsBuilder

fun ManualComposableCallsBuilder.eIdRequestVerificationDestinations() {
    screenDestination(MrzScanPermissionScreenDestination) { viewModel: MrzScanPermissionViewModel ->
        MrzScanPermissionScreen(viewModel)
    }
    screenDestination(MrzChooserScreenDestination) { viewModel: MrzChooserViewModel ->
        MrzChooserScreen(viewModel)
    }

    screenDestination(SDKScannerScreenDestination) { viewModel: SDKScannerViewModel ->
        SDKScannerScreen(viewModel)
    }

    screenDestination(SDKFaceScannerScreenDestination) { viewModel: SDKFaceScannerViewModel ->
        SDKFaceScannerScreen(viewModel)
    }

    screenDestination(MrzSubmissionScreenDestination) { viewModel: MrzSubmissionViewModel ->
        MrzSubmissionScreen(viewModel)
    }

    screenDestination(EIdDocumentRecordingScreenDestination) { viewModel: EIdDocumentRecordingViewModel ->
        EIdDocumentRecordingScreen(viewModel)
    }

    screenDestination(EIdNfcScannerScreenDestination) { viewModel: EIdNfcScannerViewModel ->
        EIdNfcScannerScreen(viewModel)
    }

    screenDestination(EIdStartAutoVerificationScreenDestination) { viewModel: EIdStartAutoVerificationViewModel ->
        EIdStartAutoVerificationScreen(viewModel)
    }
}
