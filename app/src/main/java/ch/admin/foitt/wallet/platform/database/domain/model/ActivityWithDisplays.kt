package ch.admin.foitt.wallet.platform.database.domain.model

interface ActivityWithDisplays {
    val activity: CredentialActivityEntity
    val actorDisplays: List<ActivityActorDisplayWithImage>
}
