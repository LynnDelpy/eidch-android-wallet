package ch.admin.foitt.wallet.feature.presentationRequest.presentation

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.focusGroup
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.window.core.layout.WindowWidthSizeClass
import ch.admin.foitt.wallet.R
import ch.admin.foitt.wallet.platform.actorMetadata.domain.model.ActorType
import ch.admin.foitt.wallet.platform.actorMetadata.presentation.InvitationHeader
import ch.admin.foitt.wallet.platform.actorMetadata.presentation.model.ActorUiState
import ch.admin.foitt.wallet.platform.composables.Buttons
import ch.admin.foitt.wallet.platform.composables.LoadingOverlay
import ch.admin.foitt.wallet.platform.composables.presentation.SurroundingClusterCard
import ch.admin.foitt.wallet.platform.composables.presentation.horizontalSafeDrawing
import ch.admin.foitt.wallet.platform.composables.presentation.layout.LazyColumn
import ch.admin.foitt.wallet.platform.composables.presentation.layout.WalletLayouts
import ch.admin.foitt.wallet.platform.credential.presentation.CredentialCardSmall
import ch.admin.foitt.wallet.platform.credential.presentation.MediumCredentialBox
import ch.admin.foitt.wallet.platform.credential.presentation.credentialClaimItems
import ch.admin.foitt.wallet.platform.credential.presentation.mock.CredentialMocks
import ch.admin.foitt.wallet.platform.credential.presentation.model.CredentialCardState
import ch.admin.foitt.wallet.platform.navArgs.domain.model.PresentationRequestNavArg
import ch.admin.foitt.wallet.platform.preview.WalletAllScreenPreview
import ch.admin.foitt.wallet.platform.ssi.domain.model.CredentialClaimCluster
import ch.admin.foitt.wallet.platform.trustRegistry.domain.model.TrustStatus
import ch.admin.foitt.wallet.platform.utils.TestTags
import ch.admin.foitt.wallet.theme.Sizes
import ch.admin.foitt.wallet.theme.WalletTexts
import ch.admin.foitt.wallet.theme.WalletTheme
import com.ramcosta.composedestinations.annotation.Destination

@Destination(
    navArgsDelegate = PresentationRequestNavArg::class
)
@Composable
fun PresentationRequestScreen(viewModel: PresentationRequestViewModel) {
    BackHandler(onBack = viewModel::onDecline)

    val presentationRequestUiState = viewModel.presentationRequestUiState.collectAsStateWithLifecycle().value
    val verifierUiState = viewModel.verifierUiState.collectAsStateWithLifecycle().value

    PresentationRequestContent(
        verifierUiState = verifierUiState,
        requestedClaims = presentationRequestUiState.requestedClaims,
        numberOfRequestClaims = presentationRequestUiState.numberOfClaims,
        credentialCardState = presentationRequestUiState.credential,
        isLoading = viewModel.isLoading.collectAsStateWithLifecycle().value,
        isSubmitting = viewModel.isSubmitting.collectAsStateWithLifecycle().value,
        showDelayReason = viewModel.showDelayReason.collectAsStateWithLifecycle().value,
        onWrongData = viewModel::onReportWrongData,
        onSubmit = viewModel::submit,
        onDecline = viewModel::onDecline,
    )
}

@Composable
private fun PresentationRequestContent(
    verifierUiState: ActorUiState,
    requestedClaims: List<CredentialClaimCluster>,
    numberOfRequestClaims: Int,
    credentialCardState: CredentialCardState,
    windowSizeClass: WindowWidthSizeClass = currentWindowAdaptiveInfo().windowSizeClass.windowWidthSizeClass,
    isLoading: Boolean,
    isSubmitting: Boolean,
    showDelayReason: Boolean,
    onWrongData: () -> Unit,
    onSubmit: () -> Unit,
    onDecline: () -> Unit,
) {
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(color = WalletTheme.colorScheme.background)
    ) {
        when (windowSizeClass) {
            WindowWidthSizeClass.COMPACT -> CompactContent(
                verifierUiState = verifierUiState,
                requestedClaims = requestedClaims,
                numberOfRequestClaims = numberOfRequestClaims,
                credentialCardState = credentialCardState,
                isSubmitting = isSubmitting,
                showDelayReason = showDelayReason,
                onWrongData = onWrongData,
                onSubmit = onSubmit,
                onDecline = onDecline,
            )

            else -> LargeContent(
                verifierUiState = verifierUiState,
                requestedClaims = requestedClaims,
                numberOfRequestClaims = numberOfRequestClaims,
                credentialCardState = credentialCardState,
                isSubmitting = isSubmitting,
                showDelayReason = showDelayReason,
                onWrongData = onWrongData,
                onSubmit = onSubmit,
                onDecline = onDecline,
            )
        }
        LoadingOverlay(showOverlay = isLoading)
    }
}

@Composable
private fun CompactContent(
    verifierUiState: ActorUiState,
    requestedClaims: List<CredentialClaimCluster>,
    numberOfRequestClaims: Int,
    credentialCardState: CredentialCardState,
    isSubmitting: Boolean,
    showDelayReason: Boolean,
    onWrongData: () -> Unit,
    onSubmit: () -> Unit,
    onDecline: () -> Unit,
) {
    if (isSubmitting) {
        IsSubmittingCompact(verifierUiState, credentialCardState, showDelayReason)
    } else {
        CompactList(
            verifierUiState = verifierUiState,
            requestedClaims = requestedClaims,
            numberOfRequestClaims = numberOfRequestClaims,
            credentialCardState = credentialCardState,
            onWrongData = onWrongData,
            onSubmit = onSubmit,
            onDecline = onDecline,
        )
    }
}

@Composable
private fun IsSubmittingCompact(
    verifierUiState: ActorUiState,
    credentialCardState: CredentialCardState,
    showDelayReason: Boolean,
) {
    Column {
        Spacer(Modifier.windowInsetsTopHeight(WindowInsets.safeDrawing))
        InvitationHeader(
            verifierUiState = verifierUiState,
            modifier = Modifier.padding(bottom = Sizes.s06),
        )
        Box(
            modifier = Modifier
                .clip(
                    RoundedCornerShape(
                        topStart = Sizes.boxCornerSize,
                        topEnd = Sizes.boxCornerSize
                    )
                )
                .fillMaxSize()
                .background(WalletTheme.colorScheme.surfaceContainerLow),
            contentAlignment = Alignment.Center
        ) {
            ConstraintLayout(modifier = Modifier.fillMaxSize()) {
                val (
                    credentialCard,
                    loading,
                ) = createRefs()

                CredentialCardSmall(
                    credentialState = credentialCardState,
                    modifier = Modifier.constrainAs(credentialCard) {
                        top.linkTo(parent.top)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        bottom.linkTo(parent.bottom)
                    },
                )

                LoadingIndicator(
                    showDelayReason = showDelayReason,
                    modifier = Modifier
                        .constrainAs(loading) {
                            top.linkTo(credentialCard.bottom)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                            bottom.linkTo(parent.bottom)
                        }
                        .padding(top = Sizes.s06)
                )
            }
        }
    }
}

@Composable
private fun CompactList(
    verifierUiState: ActorUiState,
    requestedClaims: List<CredentialClaimCluster>,
    numberOfRequestClaims: Int,
    credentialCardState: CredentialCardState,
    onWrongData: () -> Unit,
    onSubmit: () -> Unit,
    onDecline: () -> Unit,
) = WalletLayouts.LazyColumn(
    modifier = Modifier.fillMaxWidth(),
    state = rememberLazyListState(),
    useTopInsets = true,
    useBottomInsets = false,
    contentPadding = PaddingValues(
        bottom = Sizes.s06
    )
) {
    item {
        InvitationHeader(verifierUiState = verifierUiState)
    }
    item {
        WalletTexts.BodyLarge(
            text = stringResource(id = R.string.tk_present_review_credential_section_primary),
            modifier = Modifier.padding(start = Sizes.s06, end = Sizes.s03, bottom = Sizes.s03)
        )
    }
    item {
        SurroundingClusterCard {
            MediumCredentialBox(
                credentialCardState = credentialCardState,
            )
        }
        Spacer(modifier = Modifier.height(Sizes.s04))
    }
    item {
        WalletTexts.BodyLarge(
            text = stringResource(id = R.string.tk_present_review_claims_section_primary, numberOfRequestClaims),
            modifier = Modifier.padding(start = Sizes.s06, end = Sizes.s03, bottom = Sizes.s03)
        )
        Spacer(modifier = Modifier.height(Sizes.s02))
    }
    credentialClaimItems(
        useSurroundingCard = true,
        claimItems = requestedClaims,
        onWrongData = onWrongData,
    )
    item {
        StickyButtons(
            onDecline = onDecline,
            onAccept = onSubmit,
        )
    }
}

@Composable
private fun BoxWithConstraintsScope.LargeContent(
    verifierUiState: ActorUiState,
    requestedClaims: List<CredentialClaimCluster>,
    numberOfRequestClaims: Int,
    credentialCardState: CredentialCardState,
    isSubmitting: Boolean,
    showDelayReason: Boolean,
    onWrongData: () -> Unit,
    onSubmit: () -> Unit,
    onDecline: () -> Unit,
) {
    Row(modifier = Modifier.horizontalSafeDrawing()) {
        Spacer(modifier = Modifier.width(Sizes.s04))
        Column(
            modifier = Modifier.width(this@LargeContent.maxWidth * 0.33f),
        ) {
            Spacer(
                Modifier.windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Top))
            )
            WalletTexts.BodyLarge(
                text = stringResource(id = R.string.tk_present_review_credential_section_primary),
                modifier = Modifier.padding(horizontal = Sizes.s06, vertical = Sizes.s03)
            )
            MediumCredentialBox(
                credentialCardState = credentialCardState,
            )
        }
        Spacer(modifier = Modifier.width(Sizes.s04))
        if (isSubmitting) {
            LoadingIndicator(
                modifier = Modifier.fillMaxSize(),
                showDelayReason = showDelayReason,
            )
        } else {
            LargeList(
                verifierUiState = verifierUiState,
                numberOfRequestClaims = numberOfRequestClaims,
                requestedClaims = requestedClaims,
                onWrongData = onWrongData,
                onSubmit = onSubmit,
                onDecline = onDecline,
            )
        }
    }
}

@Composable
private fun LargeList(
    verifierUiState: ActorUiState,
    numberOfRequestClaims: Int,
    requestedClaims: List<CredentialClaimCluster>,
    onWrongData: () -> Unit,
    onSubmit: () -> Unit,
    onDecline: () -> Unit,
) = WalletLayouts.LazyColumn {
    item {
        InvitationHeader(
            verifierUiState = verifierUiState,
        )
        Spacer(modifier = Modifier.width(Sizes.s02))
    }
    item {
        WalletTexts.BodyLarge(
            text = stringResource(id = R.string.tk_present_review_claims_section_primary, numberOfRequestClaims),
            modifier = Modifier.padding(start = Sizes.s06, end = Sizes.s03, bottom = Sizes.s03)
        )
        Spacer(modifier = Modifier.height(Sizes.s02))
    }
    credentialClaimItems(
        useSurroundingCard = true,
        claimItems = requestedClaims,
        onWrongData = onWrongData,
    )
    item {
        StickyButtons(
            onAccept = onSubmit,
            onDecline = onDecline,
        )
    }
}

@Composable
private fun InvitationHeader(modifier: Modifier = Modifier, verifierUiState: ActorUiState) {
    InvitationHeader(
        modifier = modifier
            .padding(start = Sizes.s04, top = Sizes.s06, end = Sizes.s04, bottom = Sizes.s02),
        inviterName = verifierUiState.name,
        inviterImage = verifierUiState.painter,
        trustStatus = verifierUiState.trustStatus,
        actorType = verifierUiState.actorType,
    )
    Spacer(modifier = Modifier.height(Sizes.s02))
}

@Composable
private fun LoadingIndicator(
    modifier: Modifier = Modifier,
    showDelayReason: Boolean
) = Box(
    modifier = modifier,
    contentAlignment = Alignment.BottomCenter,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator(
            color = WalletTheme.colorScheme.primary,
            modifier = Modifier
                .padding(bottom = Sizes.s02)
                .size(Sizes.s12),
            strokeWidth = Sizes.line02,
        )
        if (showDelayReason) {
            WalletTexts.Body(text = stringResource(R.string.tk_present_review_loading))
        }
    }
}

@Composable
@OptIn(ExperimentalLayoutApi::class)
private fun StickyButtons(
    onDecline: () -> Unit,
    onAccept: () -> Unit,
) {
    FlowRow(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(corner = CornerSize(Sizes.s16)))
            .background(WalletTheme.colorScheme.background)
            .padding(vertical = Sizes.s03, horizontal = Sizes.s04)
            .focusGroup(),
        horizontalArrangement = Arrangement.spacedBy(Sizes.s02, Alignment.CenterHorizontally),
        verticalArrangement = Arrangement.spacedBy(Sizes.s02, Alignment.Bottom),
        maxItemsInEachRow = 2,
    ) {
        Buttons.FilledPrimary(
            modifier = Modifier.testTag(TestTags.DECLINE_BUTTON.name),
            text = stringResource(id = R.string.tk_present_review_button_decline),
            startIcon = painterResource(id = R.drawable.wallet_ic_cross),
            onClick = onDecline,
        )
        Buttons.FilledTertiary(
            modifier = Modifier.testTag(TestTags.ACCEPT_BUTTON.name),
            text = stringResource(id = R.string.tk_present_review_button_accept),
            startIcon = painterResource(id = R.drawable.wallet_ic_checkmark),
            onClick = onAccept,
        )
    }
}

@WalletAllScreenPreview
@Composable
private fun PresentationRequestScreenPreview() {
    WalletTheme {
        PresentationRequestContent(
            verifierUiState = ActorUiState(
                name = "My Verfifier Name",
                painter = painterResource(id = R.drawable.ic_swiss_cross_small),
                trustStatus = TrustStatus.TRUSTED,
                actorType = ActorType.VERIFIER,
            ),
            requestedClaims = CredentialMocks.clusterList,
            numberOfRequestClaims = 5,
            isLoading = false,
            credentialCardState = CredentialMocks.cardState01,
            isSubmitting = false,
            showDelayReason = false,
            onWrongData = {},
            onSubmit = {},
            onDecline = {},
        )
    }
}
