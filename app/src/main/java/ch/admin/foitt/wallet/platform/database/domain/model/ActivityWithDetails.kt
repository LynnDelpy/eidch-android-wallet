package ch.admin.foitt.wallet.platform.database.domain.model

import androidx.room.Embedded
import androidx.room.Relation

data class ActivityWithDetails(
    @Embedded override val activity: CredentialActivityEntity,
    @Relation(
        entity = ActivityActorDisplayEntity::class,
        parentColumn = "id",
        entityColumn = "activityId",
    )
    override val actorDisplays: List<ActivityActorDisplayWithImage>,
    @Relation(
        entity = ActivityClaimEntity::class,
        parentColumn = "id",
        entityColumn = "activityId"
    )
    val claims: List<ActivityClaimEntity>,
) : ActivityWithDisplays
