package ch.admin.foitt.wallet.feature.walletPairing.presentation

import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import ch.admin.foitt.wallet.R
import ch.admin.foitt.wallet.platform.composables.presentation.spaceBarKeyClickable
import ch.admin.foitt.wallet.platform.preview.WalletComponentPreview
import ch.admin.foitt.wallet.theme.Sizes
import ch.admin.foitt.wallet.theme.WalletTexts
import ch.admin.foitt.wallet.theme.WalletTheme

@Composable
fun EIdDeviceItem(
    onClick: () -> Unit,
    @StringRes title: Int,
    subtitle: String? = null,
    dateAddedText: String? = null,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .clickable(onClick = onClick)
            .spaceBarKeyClickable(onClick)
            .padding(start = Sizes.s04, top = Sizes.s03, end = Sizes.s06, bottom = Sizes.s03),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            painter = painterResource(id = R.drawable.wallet_ic_paring_device_add),
            contentDescription = null,
            tint = WalletTheme.colorScheme.tertiary,
        )
        Spacer(modifier = Modifier.width(Sizes.s04))
        Column(
            modifier = Modifier.weight(1f)
        ) {
            WalletTexts.BodyLarge(
                text = stringResource(id = title),
                color = WalletTheme.colorScheme.tertiary,
            )
            if (subtitle != null) {
                WalletTexts.BodyLarge(
                    text = subtitle,
                    color = WalletTheme.colorScheme.onSurface,
                )
            }
        }
        if (dateAddedText != null) {
            Spacer(modifier = Modifier.width(Sizes.s04))
            WalletTexts.BodySmall(
                text = dateAddedText,
                color = WalletTheme.colorScheme.onSurface,
            )
            Icon(
                modifier = Modifier.padding(start = Sizes.s02),
                painter = painterResource(id = R.drawable.wallet_ic_paring_device_check),
                contentDescription = null,
                tint = WalletTheme.colorScheme.tertiary,
            )
        }
    }
    HorizontalDivider(
        modifier = Modifier.padding(start = Sizes.s04)
    )
}

@WalletComponentPreview
@Composable
private fun EIdDeviceItemPreview() {
    WalletTheme {
        EIdDeviceItem(
            title = R.string.tk_eidRequest_walletPairing_currentDevice_button_primary,
            subtitle = "Android Pixel 10",
            dateAddedText = "22.07.2025 15:36",
            onClick = {},
        )
    }
}
