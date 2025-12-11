package ch.admin.foitt.wallet.platform.activityList.presentation.model

import androidx.compose.ui.graphics.painter.Painter
import ch.admin.foitt.wallet.platform.activityList.domain.model.ActivityDisplayData
import ch.admin.foitt.wallet.platform.activityList.domain.model.ActivityType

data class ActivityUiState(
    val id: Long,
    val activityType: ActivityType,
    val date: String,
    val localizedActorName: String,
    val actorImage: Painter? = null,
)

fun ActivityDisplayData.toActivityUiState(actorImage: Painter?) = ActivityUiState(
    id = id,
    activityType = activityType,
    date = date,
    localizedActorName = localizedActorName,
    actorImage = actorImage,
)
