package ch.admin.foitt.wallet.platform.credential.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.ListItemDefaults.contentColor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import ch.admin.foitt.wallet.platform.badges.domain.model.BadgeType
import ch.admin.foitt.wallet.platform.badges.presentation.NonSensitiveClaimInfoBadge
import ch.admin.foitt.wallet.platform.badges.presentation.SensitiveClaimInfoBadge
import ch.admin.foitt.wallet.platform.badges.presentation.model.ClaimBadgeUiState
import ch.admin.foitt.wallet.platform.composables.presentation.clusterLazyListItem
import ch.admin.foitt.wallet.platform.composables.presentation.layout.LazyColumn
import ch.admin.foitt.wallet.platform.composables.presentation.layout.WalletLayouts
import ch.admin.foitt.wallet.platform.credential.presentation.mock.CredentialMocks
import ch.admin.foitt.wallet.platform.credential.presentation.model.CredentialCardState
import ch.admin.foitt.wallet.platform.preview.WalletAllScreenPreview
import ch.admin.foitt.wallet.theme.Sizes
import ch.admin.foitt.wallet.theme.WalletTexts
import ch.admin.foitt.wallet.theme.WalletTheme

fun LazyListScope.credentialInfoWithBadgesWidget(
    credentialCardState: CredentialCardState,
    claimBadgesUiStates: List<ClaimBadgeUiState> = emptyList(),
    onBadge: (BadgeType) -> Unit = {},
    paddingValues: PaddingValues = PaddingValues(
        horizontal = Sizes.s04
    )
) {
    this.credentialCardListItem(
        credentialCardState = credentialCardState,
        paddingValues = paddingValues,
        isLastItem = claimBadgesUiStates.isEmpty()
    )

    if (claimBadgesUiStates.isNotEmpty()) {
        this.claimInfoBadgeListItem(
            claimBadges = claimBadgesUiStates,
            onBadge = onBadge,
            paddingValues = paddingValues,
        )
    }
}

private fun LazyListScope.credentialCardListItem(
    credentialCardState: CredentialCardState,
    paddingValues: PaddingValues = PaddingValues(0.dp),
    isLastItem: Boolean = true,
) = clusterLazyListItem(
    isFirstItem = true,
    isLastItem = isLastItem,
    showDivider = false,
    paddingValues = paddingValues,
) {
    ListItem(
        modifier = Modifier.padding(vertical = Sizes.s01),
        colors = ListItemDefaults.colors(containerColor = WalletTheme.colorScheme.listItemBackground),
        leadingContent = {
            CredentialCardVerySmall(credentialCardState)
        },
        headlineContent = {
            Column {
                val bodyTextHeight = with(LocalDensity.current) {
                    WalletTheme.typography.bodyMedium.lineHeight.toDp()
                }
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
                credentialCardState.status?.let {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            painter = painterResource(id = credentialCardState.status.getIcon()),
                            contentDescription = null,
                            tint = contentColor,
                            modifier = Modifier.sizeIn(
                                maxWidth = bodyTextHeight,
                                maxHeight = bodyTextHeight,
                            )
                        )
                        Spacer(modifier = Modifier.size(Sizes.s01))
                        WalletTexts.Body(
                            text = credentialCardState.status.getText(),
                            color = contentColor,
                        )
                    }
                }
            }
        },
    )
}

@OptIn(ExperimentalLayoutApi::class)
private fun LazyListScope.claimInfoBadgeListItem(
    claimBadges: List<ClaimBadgeUiState> = emptyList(),
    onBadge: (BadgeType) -> Unit,
    paddingValues: PaddingValues = PaddingValues(0.dp),
) = clusterLazyListItem(
    isFirstItem = false,
    isLastItem = true,
    showDivider = false,
    paddingValues = paddingValues,
) {
    ListItem(
        colors = ListItemDefaults.colors(containerColor = WalletTheme.colorScheme.listItemBackground),
        headlineContent = {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(Sizes.s02)
            ) {
                claimBadges.forEach { badge ->
                    when (badge.isSensitive) {
                        true -> SensitiveClaimInfoBadge(badge.localizedLabel, onClick = onBadge)
                        false -> NonSensitiveClaimInfoBadge(badge.localizedLabel, onClick = onBadge)
                    }
                }
            }
        }
    )
}

@WalletAllScreenPreview
@Composable
private fun CredentialInfoWithBadgesWidgetPreview() {
    val cardState = CredentialMocks.cardState01
    WalletTheme {
        WalletLayouts.LazyColumn {
            credentialInfoWithBadgesWidget(
                credentialCardState = cardState,
                onBadge = {}
            )
            item {
                Spacer(modifier = Modifier.height(8.dp))
            }
            credentialInfoWithBadgesWidget(
                credentialCardState = cardState,
                claimBadgesUiStates = listOf(
                    ClaimBadgeUiState(
                        localizedLabel = "Sensitive Claim",
                        isSensitive = true
                    ),
                    ClaimBadgeUiState(
                        localizedLabel = "Claim 2",
                        isSensitive = false
                    ),
                    ClaimBadgeUiState(
                        localizedLabel = "Non-sensitive Claim",
                        isSensitive = false
                    ),
                    ClaimBadgeUiState(
                        localizedLabel = "Some Claim",
                        isSensitive = false
                    ),
                ),
                onBadge = {}
            )
        }
    }
}
