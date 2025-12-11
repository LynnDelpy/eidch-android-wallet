package ch.admin.foitt.wallet.theme

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource

object WalletListItems {

    @Composable
    fun SimpleListItem(
        @DrawableRes leadingIcon: Int? = null,
        title: String,
        onItemClick: () -> Unit,
        @DrawableRes trailingIcon: Int,
        showDivider: Boolean = true,
    ) {
        ListItem(
            modifier = Modifier.clickable {
                onItemClick()
            },
            leadingContent = leadingIcon?.let {
                {
                    ListItemIcon(icon = leadingIcon)
                }
            },
            headlineContent = {
                WalletTexts.Body(
                    text = title,
                    color = MaterialTheme.colorScheme.primary
                )
            },
            trailingContent = { ListItemIcon(icon = trailingIcon) },
        )
        if (showDivider) {
            val startPadding = if (leadingIcon != null) Sizes.s15 else Sizes.s04
            HorizontalDivider(modifier = Modifier.padding(start = startPadding, end = Sizes.s06))
        }
    }

    @Composable
    private fun ListItemIcon(icon: Int) {
        Icon(
            painterResource(id = icon),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary
        )
    }

    @Composable
    fun Divider() = HorizontalDivider(
        modifier = Modifier
            .background(WalletTheme.colorScheme.listItemBackground)
            .padding(start = Sizes.s04),
        thickness = Sizes.line01,
        color = WalletTheme.colorScheme.outlineVariant,
    )
}
