package ch.admin.foitt.wallet.platform.activityList.domain.usecase.implementation

import ch.admin.foitt.wallet.platform.activityList.domain.model.ActivityDisplayData
import ch.admin.foitt.wallet.platform.activityList.domain.model.ActivityWithActorDisplaysRepositoryError
import ch.admin.foitt.wallet.platform.activityList.domain.model.GetActivitiesWithDisplaysFlowError
import ch.admin.foitt.wallet.platform.activityList.domain.model.toGetActivitiesWithDisplaysFlowError
import ch.admin.foitt.wallet.platform.activityList.domain.repository.ActivityWithActorDisplaysRepository
import ch.admin.foitt.wallet.platform.activityList.domain.usecase.GetActivitiesWithDisplaysFlow
import ch.admin.foitt.wallet.platform.activityList.domain.usecase.MapToActivityDisplayData
import ch.admin.foitt.wallet.platform.utils.andThen
import ch.admin.foitt.wallet.platform.utils.mapError
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.coroutines.coroutineBinding
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetActivitiesWithDisplaysFlowImpl @Inject constructor(
    private val activityWithActorDisplaysRepository: ActivityWithActorDisplaysRepository,
    private val mapToActivityDisplayData: MapToActivityDisplayData,
) : GetActivitiesWithDisplaysFlow {
    override fun invoke(credentialId: Long): Flow<Result<List<ActivityDisplayData>, GetActivitiesWithDisplaysFlowError>> =
        activityWithActorDisplaysRepository.getActivitiesByCredentialId(credentialId)
            .mapError(ActivityWithActorDisplaysRepositoryError::toGetActivitiesWithDisplaysFlowError)
            .andThen { activitiesWithDisplays ->
                coroutineBinding {
                    val activityDisplayData = activitiesWithDisplays.map { mapToActivityDisplayData(it) }

                    activityDisplayData
                }
            }
}
