package ch.admin.foitt.wallet.platform.trustRegistry.domain.usecase.implementation

import ch.admin.foitt.wallet.platform.environmentSetup.domain.repository.EnvironmentSetupRepository
import ch.admin.foitt.wallet.platform.trustRegistry.domain.model.GetTrustDomainFromDidError
import ch.admin.foitt.wallet.platform.trustRegistry.domain.model.toGetTrustDomainFromDidError
import ch.admin.foitt.wallet.platform.trustRegistry.domain.usecase.GetTrustDomainFromDid
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import com.github.michaelbull.result.coroutines.runSuspendCatching
import com.github.michaelbull.result.mapError
import javax.inject.Inject

internal class GetTrustDomainFromDidImpl @Inject constructor(
    private val repo: EnvironmentSetupRepository
) : GetTrustDomainFromDid {
    override fun invoke(
        actorDid: String,
    ): Result<String, GetTrustDomainFromDidError> = binding {
        val trustDomain = runSuspendCatching {
            val baseDomain = repo.baseTrustDomainRegex.find(actorDid)?.groups?.get(1)?.value
            repo.trustRegistryMapping[baseDomain]
        }.mapError { throwable ->
            throwable.toGetTrustDomainFromDidError(message = "Failed to get trust domain for did")
        }.bind()

        if (trustDomain.isNullOrBlank()) {
            return@binding Err(
                GetTrustDomainFromDidError.NoTrustRegistryMapping(message = "Could not get trust registry mapping for base domain")
            ).bind<String>()
        }

        trustDomain
    }
}
