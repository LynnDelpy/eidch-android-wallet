package ch.admin.foitt.wallet.feature.mrzScan.presentation

sealed interface SDKInfoState {
    data object Empty : SDKInfoState
    data object Loading : SDKInfoState
    data object Ready : SDKInfoState
    data object NetworkError : SDKInfoState
    data object UnexpectedError : SDKInfoState
    data object InfoData : SDKInfoState
}
