package ch.admin.foitt.wallet.feature.eIdApplicationProcess.presentation

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import ch.admin.foitt.wallet.R
import ch.admin.foitt.wallet.platform.composables.Buttons
import ch.admin.foitt.wallet.platform.composables.presentation.ScreenMainImage
import ch.admin.foitt.wallet.platform.composables.presentation.layout.ScrollableColumnWithPicture
import ch.admin.foitt.wallet.platform.composables.presentation.layout.WalletLayouts
import ch.admin.foitt.wallet.platform.preview.WalletAllScreenPreview
import ch.admin.foitt.wallet.theme.Sizes
import ch.admin.foitt.wallet.theme.WalletTexts
import ch.admin.foitt.wallet.theme.WalletTheme
import com.ramcosta.composedestinations.annotation.Destination

@Destination
@Composable
internal fun EIdStartDocumentRecordingScreen(
    viewModel: EIdStartDocumentRecordingViewModel,
) {
    EIdStartDocumentRecordingScreenContent(
        onStart = viewModel::onStart,
    )
}

@Composable
private fun EIdStartDocumentRecordingScreenContent(
    onStart: () -> Unit,
) = WalletLayouts.ScrollableColumnWithPicture(
    stickyStartContent = {
        ScreenMainImage(
            iconRes = R.drawable.wallet_ic_person_checkmark_colored,
            backgroundColor = WalletTheme.colorScheme.surfaceContainerLow,
        )
    },
    stickyBottomBackgroundColor = Color.Transparent,
    stickyBottomContent = {
        Buttons.FilledPrimary(
            text = stringResource(R.string.tk_getEid_startDocumentRecording_button_start),
            onClick = onStart,
        )
    },
) {
    Spacer(modifier = Modifier.height(Sizes.s06))
    WalletTexts.TitleScreen(
        text = stringResource(R.string.tk_getEid_startDocumentRecording_primary)
    )
    Spacer(modifier = Modifier.height(Sizes.s06))
    WalletTexts.BodyLarge(
        modifier = Modifier.fillMaxWidth(),
        text = stringResource(R.string.tk_getEid_startDocumentRecording_secondary)
    )
    Spacer(modifier = Modifier.height(Sizes.s04))
    WalletTexts.BodySmall(
        modifier = Modifier.fillMaxWidth(),
        text = stringResource(R.string.tk_getEid_startDocumentRecording_tertiary)
    )
}

@WalletAllScreenPreview
@Composable
private fun EIdStartDocumentRecordingScreenPreview() {
    WalletTheme {
        EIdStartDocumentRecordingScreenContent(
            onStart = {},
        )
    }
}
