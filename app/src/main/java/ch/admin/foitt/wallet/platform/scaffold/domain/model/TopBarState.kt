package ch.admin.foitt.wallet.platform.scaffold.domain.model

import androidx.annotation.StringRes

sealed interface TopBarState {
    data object None : TopBarState
    data object Empty : TopBarState

    data class DetailsWithCloseButton(
        val onUp: () -> Unit,
        @param:StringRes
        val titleId: Int?,
        val useTransparentBackground: Boolean = true,
        val onClose: () -> Unit,
    ) : TopBarState

    data class Details(
        val onUp: () -> Unit,
        @param:StringRes
        val titleId: Int?,
        val useTransparentBackground: Boolean = true,
    ) : TopBarState

    data class EmptyWithCloseButton(
        val onClose: () -> Unit,
    ) : TopBarState

    data class OnGradient(
        val onUp: () -> Unit,
        @param:StringRes
        val titleId: Int,
    ) : TopBarState
}
