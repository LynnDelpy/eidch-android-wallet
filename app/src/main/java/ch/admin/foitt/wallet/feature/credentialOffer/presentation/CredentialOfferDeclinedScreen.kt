package ch.admin.foitt.wallet.feature.credentialOffer.presentation

import androidx.activity.compose.BackHandler
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ch.admin.foitt.wallet.R
import ch.admin.foitt.wallet.platform.actorMetadata.domain.model.ActorType
import ch.admin.foitt.wallet.platform.actorMetadata.presentation.model.ActorUiState
import ch.admin.foitt.wallet.platform.badges.domain.model.BadgeType
import ch.admin.foitt.wallet.platform.badges.presentation.BadgeBottomSheet
import ch.admin.foitt.wallet.platform.credential.presentation.CredentialActionFeedbackCard
import ch.admin.foitt.wallet.platform.navArgs.domain.model.CredentialOfferDeclinedNavArg
import ch.admin.foitt.wallet.platform.nonCompliance.domain.model.NonComplianceState
import ch.admin.foitt.wallet.platform.preview.WalletAllScreenPreview
import ch.admin.foitt.wallet.platform.trustRegistry.domain.model.TrustStatus
import ch.admin.foitt.wallet.platform.trustRegistry.domain.model.VcSchemaTrustStatus
import ch.admin.foitt.wallet.theme.WalletTheme
import com.ramcosta.composedestinations.annotation.Destination

@OptIn(ExperimentalMaterial3Api::class)
@Destination(
    navArgsDelegate = CredentialOfferDeclinedNavArg::class,
)
@Composable
fun CredentialOfferDeclinedScreen(
    viewModel: CredentialOfferDeclinedViewModel,
) {
    BackHandler {
        viewModel.navigateToHome()
    }

    val badgeBottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val badgeBottomSheet = viewModel.badgeBottomSheet.collectAsStateWithLifecycle().value
    if (badgeBottomSheet != null) {
        BadgeBottomSheet(
            sheetState = badgeBottomSheetState,
            badgeBottomSheetUiState = badgeBottomSheet,
            onDismiss = viewModel::onDismissBottomSheet
        )
    }

    CredentialOfferDeclinedScreenContent(
        isLoading = viewModel.isLoading.collectAsStateWithLifecycle().value,
        issuer = viewModel.uiState.collectAsStateWithLifecycle().value.issuer,
        onBadge = viewModel::onBadge,
    )
}

@Composable
private fun CredentialOfferDeclinedScreenContent(
    isLoading: Boolean,
    issuer: ActorUiState,
    onBadge: (BadgeType) -> Unit,
) {
    CredentialActionFeedbackCard(
        isLoading = isLoading,
        issuer = issuer,
        contentTextFirstParagraphText = R.string.tk_receive_declinedOffer_primary,
        iconAlwaysVisible = true,
        contentIcon = R.drawable.wallet_ic_circular_cross,
        onBadge = onBadge,
    )
}

@WalletAllScreenPreview
@Composable
private fun CredentialOfferDeclinedScreenContentPreview() {
    WalletTheme {
        CredentialOfferDeclinedScreenContent(
            isLoading = false,
            issuer = ActorUiState(
                name = "Test Issuer",
                painter = painterResource(id = R.drawable.wallet_ic_scan_person),
                trustStatus = TrustStatus.TRUSTED,
                vcSchemaTrustStatus = VcSchemaTrustStatus.TRUSTED,
                actorType = ActorType.ISSUER,
                nonComplianceState = NonComplianceState.REPORTED,
                nonComplianceReason = "report reason",
            ),
            onBadge = {},
        )
    }
}
