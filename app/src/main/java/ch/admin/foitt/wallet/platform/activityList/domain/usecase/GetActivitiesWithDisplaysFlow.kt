package ch.admin.foitt.wallet.platform.activityList.domain.usecase

import ch.admin.foitt.wallet.platform.activityList.domain.model.ActivityDisplayData
import ch.admin.foitt.wallet.platform.activityList.domain.model.GetActivitiesWithDisplaysFlowError
import com.github.michaelbull.result.Result
import kotlinx.coroutines.flow.Flow

interface GetActivitiesWithDisplaysFlow {
    operator fun invoke(credentialId: Long): Flow<Result<List<ActivityDisplayData>, GetActivitiesWithDisplaysFlowError>>
}
