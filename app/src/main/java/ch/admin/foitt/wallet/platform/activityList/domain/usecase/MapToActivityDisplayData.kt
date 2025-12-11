package ch.admin.foitt.wallet.platform.activityList.domain.usecase

import ch.admin.foitt.wallet.platform.activityList.domain.model.ActivityDisplayData
import ch.admin.foitt.wallet.platform.database.domain.model.ActivityWithDisplays

interface MapToActivityDisplayData {
    suspend operator fun invoke(
        activity: ActivityWithDisplays
    ): ActivityDisplayData
}
