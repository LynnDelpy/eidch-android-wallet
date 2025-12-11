package ch.admin.foitt.wallet.platform.activityList.presentation.model

import androidx.compose.ui.graphics.Color
import ch.admin.foitt.wallet.platform.activityList.domain.model.ActivityType
import ch.admin.foitt.wallet.platform.credential.presentation.model.CredentialCardState
import ch.admin.foitt.wallet.platform.credentialStatus.domain.model.CredentialDisplayStatus
import ch.admin.foitt.wallet.platform.ssi.domain.model.CredentialClaimCluster

data class ActivityDetailUiState(
    val activity: ActivityUiState,
    val credential: CredentialCardState,
    val claims: List<CredentialClaimCluster>,
) {
    companion object {
        val EMPTY = ActivityDetailUiState(
            credential = CredentialCardState(
                credentialId = -1,
                status = CredentialDisplayStatus.Unknown,
                title = "",
                subtitle = null,
                logo = null,
                backgroundColor = Color.Transparent,
                contentColor = Color.Transparent,
                borderColor = Color.Transparent,
                isCredentialFromBetaIssuer = false
            ),
            activity = ActivityUiState(
                id = -1,
                activityType = ActivityType.ISSUANCE,
                date = "01.01.1970 | 00:00",
                localizedActorName = "",
                actorImage = null
            ),
            claims = emptyList(),
        )
    }
}
