package ch.admin.foitt.wallet.feature.eIdRequestVerification.presentation.nfcScanner

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import ch.admin.foitt.wallet.R
import ch.admin.foitt.wallet.platform.composables.Buttons
import ch.admin.foitt.wallet.platform.composables.presentation.ScreenMainImage
import ch.admin.foitt.wallet.platform.composables.presentation.layout.ScrollableColumnWithPicture
import ch.admin.foitt.wallet.platform.composables.presentation.layout.WalletLayouts
import ch.admin.foitt.wallet.theme.Sizes
import ch.admin.foitt.wallet.theme.WalletTexts
import ch.admin.foitt.wallet.theme.WalletTheme

@Composable
internal fun NfcScannerInfoContent(
    onStart: () -> Unit,
    onTips: () -> Unit,
) = WalletLayouts.ScrollableColumnWithPicture(
    stickyStartContent = {
        ScreenMainImage(
            iconRes = R.drawable.wallet_nfc_info,
            backgroundColor = WalletTheme.colorScheme.surfaceContainerLow
        )
    },
    stickyBottomBackgroundColor = Color.Transparent,
    stickyBottomContent = {
        Buttons.FilledPrimary(
            text = stringResource(R.string.tk_eidRequest_nfcScan_primaryButton),
            onClick = onStart,
        )
    },
) {
    Spacer(modifier = Modifier.Companion.height(Sizes.s06))
    WalletTexts.TitleScreen(
        text = stringResource(R.string.tk_eidRequest_nfcScan_primary)
    )
    Spacer(modifier = Modifier.Companion.height(Sizes.s06))
    WalletTexts.BodyLarge(
        modifier = Modifier.Companion.fillMaxWidth(),
        text = stringResource(R.string.tk_eidRequest_nfcScan_secondary)
    )
    Spacer(modifier = Modifier.Companion.height(Sizes.s04))
    Buttons.TextLink(
        text = stringResource(id = R.string.tk_eidRequest_nfcScan_tertiary),
        onClick = onTips,
        endIcon = painterResource(id = R.drawable.wallet_ic_chevron),
    )
}
