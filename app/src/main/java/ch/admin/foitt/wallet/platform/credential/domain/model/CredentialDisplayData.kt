package ch.admin.foitt.wallet.platform.credential.domain.model

import ch.admin.foitt.wallet.platform.actorEnvironment.domain.model.ActorEnvironment
import ch.admin.foitt.wallet.platform.credentialStatus.domain.model.CredentialDisplayStatus
import ch.admin.foitt.wallet.platform.database.domain.model.CredentialDisplay

data class CredentialDisplayData(
    val credentialId: Long,
    val title: String?,
    val subtitle: String?,
    val status: CredentialDisplayStatus,
    val logoUri: String?,
    val backgroundColor: String?,
    val actorEnvironment: ActorEnvironment
) {
    constructor(
        credentialId: Long,
        status: CredentialDisplayStatus,
        credentialDisplay: CredentialDisplay,
        actorEnvironment: ActorEnvironment,
    ) : this(
        credentialId = credentialId,
        status = status,
        title = credentialDisplay.name,
        subtitle = credentialDisplay.description,
        logoUri = credentialDisplay.logoUri,
        backgroundColor = credentialDisplay.backgroundColor,
        actorEnvironment = actorEnvironment
    )
}
