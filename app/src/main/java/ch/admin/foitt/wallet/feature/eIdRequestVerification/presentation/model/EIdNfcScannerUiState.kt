package ch.admin.foitt.wallet.feature.eIdRequestVerification.presentation.model

sealed interface EIdNfcScannerUiState {
    data object Initializing : EIdNfcScannerUiState
    data class Info(
        val onStart: () -> Unit,
        val onTips: () -> Unit,
    ) : EIdNfcScannerUiState
    data object Scanning : EIdNfcScannerUiState
    data object LoadingChipData : EIdNfcScannerUiState
    data object Error : EIdNfcScannerUiState
    data object Success : EIdNfcScannerUiState
}
