package ch.admin.foitt.wallet.platform.composables.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import ch.admin.foitt.wallet.theme.Sizes
import ch.admin.foitt.wallet.theme.WalletListItems
import ch.admin.foitt.wallet.theme.WalletTheme

fun LazyListScope.clusterLazyListItem(
    isFirstItem: Boolean,
    isLastItem: Boolean,
    showDivider: Boolean = true,
    paddingValues: PaddingValues = PaddingValues(0.dp),
    content: @Composable () -> Unit,
) = item {
    ClusterListItem(
        isFirstItem = isFirstItem,
        isLastItem = isLastItem,
        showDivider = showDivider,
        paddingValues = paddingValues,
        content = content
    )
}

@Composable
fun ClusterListItem(
    isFirstItem: Boolean,
    isLastItem: Boolean,
    showDivider: Boolean = true,
    paddingValues: PaddingValues = PaddingValues(0.dp),
    backgroundColor: Color = WalletTheme.colorScheme.listItemBackground,
    cornerSize: Dp = Sizes.s05,
    content: @Composable () -> Unit,
) = Box(
    modifier = Modifier
        .padding(paddingValues)
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (isFirstItem) {
                    Modifier.clip(RoundedCornerShape(topStart = cornerSize, topEnd = cornerSize))
                } else {
                    Modifier
                }
            )
            .then(
                if (isLastItem) {
                    Modifier.clip(RoundedCornerShape(bottomStart = cornerSize, bottomEnd = cornerSize))
                } else {
                    Modifier
                }
            )
            .background(backgroundColor)
    ) {
        content()
        if (!isLastItem && showDivider) {
            WalletListItems.Divider()
        }
    }
}
