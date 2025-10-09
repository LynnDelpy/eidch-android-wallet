package ch.admin.foitt.wallet.feature.eIdRequestVerification.presentation

import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ch.admin.foitt.wallet.feature.eIdRequestVerification.presentation.model.EIdNfcScannerUiState
import ch.admin.foitt.wallet.feature.eIdRequestVerification.presentation.nfcScanner.NfcScannerInfoContent
import ch.admin.foitt.wallet.platform.navArgs.domain.model.EIdOnlineSessionNavArg
import ch.admin.foitt.wallet.platform.preview.WalletAllScreenPreview
import ch.admin.foitt.wallet.theme.WalletTheme
import com.ramcosta.composedestinations.annotation.Destination

@Destination(
    navArgsDelegate = EIdOnlineSessionNavArg::class,
)
@Composable
fun EIdNfcScannerScreen(viewModel: EIdNfcScannerViewModel) {
    EIdNfcScannerScreenContent(
        uiState = viewModel.uiState.collectAsStateWithLifecycle().value
    )
}

@Composable
private fun EIdNfcScannerScreenContent(
    uiState: EIdNfcScannerUiState,
) = when (uiState) {
    is EIdNfcScannerUiState.Error -> {
    }
    is EIdNfcScannerUiState.Info -> NfcScannerInfoContent(
        onStart = uiState.onStart,
        onTips = uiState.onTips,
    )
    is EIdNfcScannerUiState.Initializing -> {
    }
    is EIdNfcScannerUiState.LoadingChipData -> {
    }
    is EIdNfcScannerUiState.Scanning -> {
    }
    is EIdNfcScannerUiState.Success -> {
    }
}

@Composable
@WalletAllScreenPreview
private fun EIdNfcScannerPreview() {
    WalletTheme {
        EIdNfcScannerScreenContent(
            uiState = EIdNfcScannerUiState.Info(
                onStart = {},
                onTips = {},
            )
        )
    }
}
