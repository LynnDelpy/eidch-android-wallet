package ch.admin.foitt.wallet.platform.actorMetadata.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import ch.admin.foitt.wallet.R
import ch.admin.foitt.wallet.platform.actorMetadata.domain.model.ActorType
import ch.admin.foitt.wallet.platform.composables.Avatar
import ch.admin.foitt.wallet.platform.composables.AvatarSize
import ch.admin.foitt.wallet.platform.composables.LegitimateIssuerBadge
import ch.admin.foitt.wallet.platform.composables.LegitimateVerifierBadge
import ch.admin.foitt.wallet.platform.composables.NonLegitimateIssuerBadge
import ch.admin.foitt.wallet.platform.composables.NonLegitimateVerifierBadge
import ch.admin.foitt.wallet.platform.composables.TrustBadgeNotTrusted
import ch.admin.foitt.wallet.platform.composables.TrustBadgeTrusted
import ch.admin.foitt.wallet.platform.preview.WalletComponentPreview
import ch.admin.foitt.wallet.platform.trustRegistry.domain.model.TrustStatus
import ch.admin.foitt.wallet.platform.trustRegistry.domain.model.VcSchemaTrustStatus
import ch.admin.foitt.wallet.theme.Sizes
import ch.admin.foitt.wallet.theme.WalletTexts
import ch.admin.foitt.wallet.theme.WalletTheme

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun InvitationHeader(
    inviterName: String?,
    inviterImage: Painter?,
    actorType: ActorType,
    trustStatus: TrustStatus,
    vcSchemaTrustStatus: VcSchemaTrustStatus,
    modifier: Modifier = Modifier,
) = Card(
    shape = RoundedCornerShape(bottomStart = Sizes.s09, bottomEnd = Sizes.s09),
    colors = CardDefaults.cardColors(containerColor = WalletTheme.colorScheme.surface)
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Top))
            .padding(start = Sizes.s04, end = Sizes.s04, top = Sizes.s04, bottom = Sizes.s02),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Avatar(
                imagePainter = inviterImage ?: fallBackIcon(actorType),
                size = AvatarSize.LARGE,
                imageTint = WalletTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.size(Sizes.s04))
            WalletTexts.TitleMedium(
                text = inviterName ?: fallBackName(actorType),
                color = WalletTheme.colorScheme.onSurface
            )
        }

        Spacer(modifier = Modifier.height(Sizes.s02))

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(Sizes.s03),
        ) {
            TrustBadge(trustStatus)
            LegitimateActorBadge(
                actorType = actorType,
                vcSchemaTrustStatus = vcSchemaTrustStatus,
            )
        }
    }
}

@Composable
private fun fallBackIcon(actorType: ActorType) = when (actorType) {
    ActorType.ISSUER,
    ActorType.VERIFIER -> painterResource(R.drawable.wallet_ic_actor_default)

    ActorType.UNKNOWN -> null
}

@Composable
private fun fallBackName(actorType: ActorType): String = when (actorType) {
    ActorType.ISSUER -> stringResource(R.string.tk_credential_offer_issuer_name_unknown)
    ActorType.VERIFIER -> stringResource(R.string.presentation_verifier_name_unknown)
    ActorType.UNKNOWN -> ""
}

@Composable
private fun TrustBadge(
    trustStatus: TrustStatus,
    onClick: () -> Unit = {},
) = when (trustStatus) {
    TrustStatus.TRUSTED -> TrustBadgeTrusted { onClick }
    TrustStatus.NOT_TRUSTED -> TrustBadgeNotTrusted { onClick }
    else -> {}
}


@Composable
private fun LegitimateActorBadge(
    actorType: ActorType,
    vcSchemaTrustStatus: VcSchemaTrustStatus,
    onClick: () -> Unit = {},
) = when {
    actorType == ActorType.ISSUER && vcSchemaTrustStatus == VcSchemaTrustStatus.TRUSTED -> LegitimateIssuerBadge(onClick = onClick)

    actorType == ActorType.ISSUER && vcSchemaTrustStatus == VcSchemaTrustStatus.NOT_TRUSTED -> NonLegitimateIssuerBadge(onClick = onClick)

    actorType == ActorType.VERIFIER && vcSchemaTrustStatus == VcSchemaTrustStatus.TRUSTED -> LegitimateVerifierBadge(onClick = onClick)

    actorType == ActorType.VERIFIER && vcSchemaTrustStatus == VcSchemaTrustStatus.NOT_TRUSTED -> NonLegitimateVerifierBadge(
        onClick = onClick
    )

    else -> {}
}

private data class InvitationHeaderPreviewParam(
    val actorName: String?,
    val actorLogo: Int,
    val trustStatus: TrustStatus,
    val vcSchemaTrustStatus: VcSchemaTrustStatus,
)

private class InvitationHeaderPreviewParams : PreviewParameterProvider<InvitationHeaderPreviewParam> {
    override val values: Sequence<InvitationHeaderPreviewParam> = sequenceOf(
        InvitationHeaderPreviewParam("Issuer Name", R.drawable.wallet_ic_eid, TrustStatus.TRUSTED, VcSchemaTrustStatus.TRUSTED),
        InvitationHeaderPreviewParam("Issuer Name", R.drawable.wallet_ic_eid, TrustStatus.TRUSTED, VcSchemaTrustStatus.NOT_TRUSTED),
        InvitationHeaderPreviewParam(
            "Issuer with a veeeeryyyyy loooonnnnnng name",
            R.drawable.ic_launcher_background,
            TrustStatus.TRUSTED,
            VcSchemaTrustStatus.UNPROTECTED
        ),
        InvitationHeaderPreviewParam(
            "Issuer Name not trusted",
            R.drawable.wallet_ic_actor_default,
            TrustStatus.NOT_TRUSTED,
            VcSchemaTrustStatus.UNPROTECTED
        ),
        InvitationHeaderPreviewParam(
            "Issuer Name trust unknown",
            R.drawable.wallet_ic_dotted_cross,
            TrustStatus.UNKNOWN,
            VcSchemaTrustStatus.UNPROTECTED
        ),
        InvitationHeaderPreviewParam(null, R.drawable.wallet_ic_dotted_cross, TrustStatus.UNKNOWN, VcSchemaTrustStatus.UNPROTECTED),
    )
}

@WalletComponentPreview
@Composable
private fun InvitationHeaderPreview(
    @PreviewParameter(InvitationHeaderPreviewParams::class) previewParams: InvitationHeaderPreviewParam,
) {
    WalletTheme {
        InvitationHeader(
            inviterName = previewParams.actorName,
            inviterImage = painterResource(previewParams.actorLogo),
            trustStatus = previewParams.trustStatus,
            vcSchemaTrustStatus = previewParams.vcSchemaTrustStatus,
            actorType = ActorType.ISSUER,
        )
    }
}
