package ch.admin.foitt.wallet.platform.credential.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ch.admin.foitt.wallet.platform.composables.presentation.clusterLazyListItem
import ch.admin.foitt.wallet.platform.credential.presentation.model.CredentialCardState
import ch.admin.foitt.wallet.theme.Sizes
import ch.admin.foitt.wallet.theme.WalletTexts
import ch.admin.foitt.wallet.theme.WalletTheme

fun LazyListScope.credentialCardListItem(
    credentialCardState: CredentialCardState,
    paddingValues: PaddingValues = PaddingValues(0.dp),
) = clusterLazyListItem(
    isFirstItem = true,
    isLastItem = true,
    paddingValues = paddingValues,
) {
    ListItem(
        modifier = Modifier.padding(vertical = Sizes.s01),
        colors = ListItemDefaults.colors(containerColor = WalletTheme.colorScheme.listItemBackground),
        headlineContent = {
            Column {
                credentialCardState.title?.let {
                    WalletTexts.BodyLarge(
                        text = it,
                        color = WalletTheme.colorScheme.onSurface,
                    )
                }
                credentialCardState.subtitle?.let {
                    WalletTexts.BodyLarge(
                        text = it,
                        color = WalletTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        },
        leadingContent = {
            CredentialCardVerySmall(credentialCardState)
        },
    )
}
