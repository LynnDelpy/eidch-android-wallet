package ch.admin.foitt.wallet.feature.eIdRequestVerification.presentation.nfcScanner

sealed interface EIdNfcScannerUiState {
    data object Initializing : EIdNfcScannerUiState
    data class Info(
        val onStart: () -> Unit,
        val onTips: () -> Unit,
    ) : EIdNfcScannerUiState
    data class Scanning(
        val onStop: () -> Unit,
    ) : EIdNfcScannerUiState
    data class ReadingChipData(
        val onStop: () -> Unit,
    ) : EIdNfcScannerUiState
    data class Error(
        val onRetry: () -> Unit,
    ) : EIdNfcScannerUiState
    data object Success : EIdNfcScannerUiState
    data class NfcDisabled(
        val onEnable: () -> Unit,
    ) : EIdNfcScannerUiState
}
