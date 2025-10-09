package ch.admin.foitt.wallet.platform.composables

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import ch.admin.foitt.wallet.R
import ch.admin.foitt.wallet.platform.composables.presentation.spaceBarKeyClickable
import ch.admin.foitt.wallet.platform.preview.WalletComponentPreview
import ch.admin.foitt.wallet.theme.Sizes
import ch.admin.foitt.wallet.theme.WalletTexts
import ch.admin.foitt.wallet.theme.WalletTheme

@Composable
fun TrustBadgeTrusted(
    onClick: () -> Unit,
) = Badge(
    icon = R.drawable.wallet_ic_trusted,
    text = R.string.tk_issuer_trusted,
    contentColor = WalletTheme.colorScheme.onLightTertiary,
    backgroundColor = WalletTheme.colorScheme.lightTertiary,
    onClick = onClick,
)

@Composable
fun TrustBadgeNotTrusted(
    onClick: () -> Unit,
) = Badge(
    icon = R.drawable.wallet_ic_questionmark_small,
    text = R.string.tk_issuer_notTrusted,
    contentColor = WalletTheme.colorScheme.primary,
    backgroundColor = WalletTheme.colorScheme.lightPrimary,
    onClick = onClick,
)

@Composable
fun LegitimateIssuerBadge(
    onClick: () -> Unit,
) = Badge(
    icon = R.drawable.wallet_ic_legitimate_actor,
    text = R.string.tk_issuer_legitimate,
    contentColor = WalletTheme.colorScheme.onLightTertiary,
    backgroundColor = WalletTheme.colorScheme.lightTertiary,
    onClick = onClick,
)

@Composable
fun NonLegitimateIssuerBadge(
    onClick: () -> Unit,
) = Badge(
    icon = R.drawable.wallet_ic_non_legitimate_actor,
    text = R.string.tk_issuer_notLegitimate,
    contentColor = WalletTheme.colorScheme.onLightError,
    backgroundColor = WalletTheme.colorScheme.lightError,
    onClick = onClick,
)

@Composable
fun LegitimateVerifierBadge(
    onClick: () -> Unit,
) = Badge(
    icon = R.drawable.wallet_ic_legitimate_actor,
    text = R.string.tk_verifier_legitimate,
    contentColor = WalletTheme.colorScheme.onLightTertiary,
    backgroundColor = WalletTheme.colorScheme.lightTertiary,
    onClick = onClick,
)

@Composable
fun NonLegitimateVerifierBadge(
    onClick: () -> Unit,
) = Badge(
    icon = R.drawable.wallet_ic_non_legitimate_actor,
    text = R.string.tk_verifier_notLegitimate,
    contentColor = WalletTheme.colorScheme.onLightError,
    backgroundColor = WalletTheme.colorScheme.lightError,
    onClick = onClick,
)

@Composable
private fun Badge(
    @DrawableRes icon: Int,
    @StringRes text: Int,
    contentColor: Color,
    backgroundColor: Color,
    onClick: () -> Unit,
) = Box(
    modifier = Modifier
        .padding(top = Sizes.s02, bottom = Sizes.s02),
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(Sizes.s10))
            .clickable(onClick = onClick)
            .spaceBarKeyClickable(onSpace = onClick)
            .background(backgroundColor)
            .padding(top = Sizes.s02, bottom = Sizes.s02, start = Sizes.s03, end = Sizes.s04),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = null,
            tint = contentColor,
        )
        Spacer(modifier = Modifier.width(Sizes.s01))
        WalletTexts.LabelMedium(
            text = stringResource(text),
            color = contentColor,
        )
    }
}

@WalletComponentPreview
@Composable
private fun BadgePreview() {
    WalletTheme {
        Column(
            verticalArrangement = Arrangement.spacedBy(Sizes.s02)
        ) {
            TrustBadgeTrusted(onClick = {})
            TrustBadgeNotTrusted(onClick = {})
            LegitimateIssuerBadge(onClick = {})
            NonLegitimateIssuerBadge(onClick = {})
            LegitimateVerifierBadge(onClick = {})
            NonLegitimateVerifierBadge(onClick = {})
        }
    }
}
