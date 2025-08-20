package ch.admin.foitt.wallet.feature.mrzScan.presentation

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ch.admin.foitt.wallet.R
import ch.admin.foitt.wallet.feature.mrzScan.presentation.model.ApplyUiState
import ch.admin.foitt.wallet.platform.composables.Buttons
import ch.admin.foitt.wallet.platform.composables.presentation.LoadingIndicator
import ch.admin.foitt.wallet.platform.composables.presentation.ScreenMainImage
import ch.admin.foitt.wallet.platform.composables.presentation.layout.ScrollableColumnWithPicture
import ch.admin.foitt.wallet.platform.composables.presentation.layout.WalletLayouts
import ch.admin.foitt.wallet.platform.navArgs.domain.model.EIdMrzResultNavArg
import ch.admin.foitt.wallet.platform.preview.WalletAllScreenPreview
import ch.admin.foitt.wallet.theme.Sizes
import ch.admin.foitt.wallet.theme.WalletTexts
import ch.admin.foitt.wallet.theme.WalletTheme
import com.ramcosta.composedestinations.annotation.Destination

@Destination(
    navArgsDelegate = EIdMrzResultNavArg::class
)
@Composable
internal fun MrzSubmissionScreen(
    viewModel: MrzSubmissionViewModel,
) {
    MrzSubmissionScreenContent(
        applySubmissionState = viewModel.mrzState.collectAsStateWithLifecycle().value,
    )
}

@Composable
private fun MrzSubmissionScreenContent(
    applySubmissionState: ApplyUiState,
) = when (applySubmissionState) {
    ApplyUiState.Loading -> LoadingContent()
    ApplyUiState.Valid -> LoadingContent()
    is ApplyUiState.Unexpected -> UnexpectedErrorContent(
        onClose = applySubmissionState.onClose,
        onRetry = applySubmissionState.onRetry
    )

    is ApplyUiState.NetworkError -> UnexpectedErrorContent(
        onClose = applySubmissionState.onClose,
        onRetry = applySubmissionState.onRetry
    )
}

@Composable
private fun LoadingContent() = WalletLayouts.ScrollableColumnWithPicture(
    stickyStartContent = {
        LoadingIndicator()
    },
    stickyBottomBackgroundColor = Color.Transparent,
    stickyBottomContent = null,
) {
    Spacer(modifier = Modifier.height(Sizes.s06))
    WalletTexts.TitleScreen(
        text = stringResource(id = R.string.tk_eidRequest_attestation_loading_primary),
    )
    Spacer(modifier = Modifier.height(Sizes.s06))
    WalletTexts.BodyLarge(
        modifier = Modifier.fillMaxWidth(),
        text = "Your MRZ data is being prepared and sent for validation.",
    )
}

@Composable
private fun UnexpectedErrorContent(
    onClose: () -> Unit,
    onRetry: () -> Unit,
) = WalletLayouts.ScrollableColumnWithPicture(
    stickyStartContent = {
        ScreenMainImage(
            iconRes = R.drawable.wallet_ic_sadface_colored,
            backgroundColor = WalletTheme.colorScheme.surfaceContainerLow
        )
    },
    stickyBottomBackgroundColor = Color.Transparent,
    stickyBottomContent = {
        Buttons.FilledPrimary(
            text = stringResource(R.string.tk_eidRequest_attestation_unexpectedError_button_retry),
            onClick = onRetry,
            modifier = Modifier
                .fillMaxWidth()
        )
        Buttons.FilledSecondary(
            text = stringResource(R.string.tk_eidRequest_attestation_unexpectedError_button_close),
            onClick = onClose,
            modifier = Modifier
                .fillMaxWidth()
        )
    },
) {
    Spacer(modifier = Modifier.height(Sizes.s06))
    WalletTexts.TitleScreen(
        text = stringResource(id = R.string.tk_eidRequest_attestation_unexpectedError_primary),
    )
    Spacer(modifier = Modifier.height(Sizes.s06))
    WalletTexts.BodyLarge(
        modifier = Modifier.fillMaxWidth(),
        text = stringResource(id = R.string.tk_eidRequest_attestation_unexpectedError_secondary),
    )
}

private class MrzSubmissionPreviewParams : PreviewParameterProvider<ApplyUiState> {
    override val values: Sequence<ApplyUiState> = sequenceOf(
        ApplyUiState.Loading,
        ApplyUiState.Valid,
        ApplyUiState.Unexpected({}, {}),
        ApplyUiState.NetworkError({}, {}),
    )
}

@WalletAllScreenPreview
@Composable
private fun MrzSubmissionScreenPreview(
    @PreviewParameter(MrzSubmissionPreviewParams::class) state: ApplyUiState,
) {
    WalletTheme {
        MrzSubmissionScreenContent(
            applySubmissionState = state,
        )
    }
}
