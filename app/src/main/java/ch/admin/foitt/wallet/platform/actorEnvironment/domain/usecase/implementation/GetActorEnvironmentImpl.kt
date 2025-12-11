package ch.admin.foitt.wallet.platform.actorEnvironment.domain.usecase.implementation

import ch.admin.foitt.wallet.platform.actorEnvironment.domain.model.ActorEnvironment
import ch.admin.foitt.wallet.platform.actorEnvironment.domain.usecase.GetActorEnvironment
import javax.inject.Inject

class GetActorEnvironmentImpl @Inject constructor() : GetActorEnvironment {
    private val prodPattern =
        Regex(pattern = "^did:(?:tdw|webvh):[^:]+:identifier-reg(?:-[^:])?\\.trust-infra\\.swiyu\\.admin\\.ch:.*")
    private val betaPattern =
        Regex(pattern = "^did:(?:tdw|webvh):[^:]+:identifier-reg(?:-[^:])?\\.trust-infra\\.swiyu-int\\.admin\\.ch:.*")

    override suspend fun invoke(credentialIssuer: String?): ActorEnvironment = when {
        credentialIssuer == null -> ActorEnvironment.EXTERNAL
        prodPattern.containsMatchIn(credentialIssuer) -> ActorEnvironment.PRODUCTION
        betaPattern.containsMatchIn(credentialIssuer) -> ActorEnvironment.BETA
        else -> ActorEnvironment.EXTERNAL
    }
}
