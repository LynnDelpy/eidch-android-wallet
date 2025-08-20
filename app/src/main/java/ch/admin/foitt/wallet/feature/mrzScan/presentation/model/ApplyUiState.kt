package ch.admin.foitt.wallet.feature.mrzScan.presentation.model

internal sealed interface ApplyUiState {
    data object Loading : ApplyUiState
    data object Valid : ApplyUiState
    data class Unexpected(
        val onClose: () -> Unit,
        val onRetry: () -> Unit,
    ) : ApplyUiState
    data class NetworkError(
        val onClose: () -> Unit,
        val onRetry: () -> Unit,
    ) : ApplyUiState
}
