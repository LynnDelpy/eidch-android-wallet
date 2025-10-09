package ch.admin.foitt.wallet.feature.eIdApplicationProcess.presentation.model

internal sealed interface ProcessDataUiState {
    data object Loading : ProcessDataUiState
    data object Valid : ProcessDataUiState
    data class Declined(
        val onClose: () -> Unit,
        val onHelp: () -> Unit,
    ) : ProcessDataUiState
    data class GenericError(
        val onClose: () -> Unit,
        val onRetry: () -> Unit,
        val onHelp: () -> Unit,
    ) : ProcessDataUiState
}
