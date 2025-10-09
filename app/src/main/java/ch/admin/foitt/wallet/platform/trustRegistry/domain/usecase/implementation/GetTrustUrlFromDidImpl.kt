package ch.admin.foitt.wallet.platform.trustRegistry.domain.usecase.implementation

import ch.admin.foitt.wallet.platform.environmentSetup.domain.repository.EnvironmentSetupRepository
import ch.admin.foitt.wallet.platform.trustRegistry.domain.model.GetTrustUrlFromDidError
import ch.admin.foitt.wallet.platform.trustRegistry.domain.model.TrustStatementType
import ch.admin.foitt.wallet.platform.trustRegistry.domain.model.toGetTrustDomainFromDidError
import ch.admin.foitt.wallet.platform.trustRegistry.domain.usecase.GetTrustUrlFromDid
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import com.github.michaelbull.result.coroutines.runSuspendCatching
import com.github.michaelbull.result.mapError
import io.ktor.utils.io.charsets.name
import java.net.URL
import java.net.URLEncoder
import javax.inject.Inject

internal class GetTrustUrlFromDidImpl @Inject constructor(
    private val repo: EnvironmentSetupRepository
) : GetTrustUrlFromDid {
    override fun invoke(
        trustStatementType: TrustStatementType,
        actorDid: String,
        vcSchemaId: String?,
    ): Result<URL, GetTrustUrlFromDidError> = binding {
        val trustDomain = getTrustDomainForDid(didString = actorDid).bind()
        buildTrustUrl(
            trustStatementType = trustStatementType,
            actorDid = actorDid,
            trustDomain = trustDomain,
            vcSchemaId = vcSchemaId,
        ).bind()
    }

    private fun getTrustDomainForDid(didString: String): Result<String, GetTrustUrlFromDidError> = runSuspendCatching {
        val baseDomain = repo.baseTrustDomainRegex.find(didString)?.groups?.get(1)?.value
        val trustUrl = repo.trustRegistryMapping[baseDomain]
        if (trustUrl.isNullOrBlank()) {
            return Err(
                GetTrustUrlFromDidError.NoTrustRegistryMapping(message = "Could not get trust registry mapping for base domain")
            )
        }
        trustUrl
    }.mapError { throwable ->
        throwable.toGetTrustDomainFromDidError(message = "Failed to get trust domain for did")
    }

    private fun buildTrustUrl(
        trustStatementType: TrustStatementType,
        actorDid: String,
        trustDomain: String,
        vcSchemaId: String?,
    ): Result<URL, GetTrustUrlFromDidError> = runSuspendCatching {
        val trustPathBase = "$TRUST_SCHEME$trustDomain$TRUST_PATH"
        val urlString = when (trustStatementType) {
            TrustStatementType.METADATA -> {
                val didUrlEncoded = URLEncoder.encode(actorDid, Charsets.UTF_8.name)
                "$trustPathBase$didUrlEncoded"
            }
            TrustStatementType.IDENTITY -> {
                val didUrlEncoded = URLEncoder.encode(actorDid, Charsets.UTF_8.name)
                "$trustPathBase$TRUST_PATH_IDENTITY$didUrlEncoded"
            }
            TrustStatementType.ISSUANCE -> {
                val vcSchemaIdUrlEncoded = URLEncoder.encode(vcSchemaId, Charsets.UTF_8.name)
                "$trustPathBase$TRUST_PATH_ISSUANCE?vcSchemaId=$vcSchemaIdUrlEncoded"
            }
            TrustStatementType.VERIFICATION -> {
                val vcSchemaIdUrlEncoded = URLEncoder.encode(vcSchemaId, Charsets.UTF_8.name)
                "$trustPathBase$TRUST_PATH_VERIFICATION?vcSchemaId=$vcSchemaIdUrlEncoded"
            }
        }

        URL(urlString)
    }.mapError { throwable ->
        throwable.toGetTrustDomainFromDidError(message = "Failed to build trust URL")
    }

    private companion object {
        const val TRUST_SCHEME = "https://"
        const val TRUST_PATH = "/api/v1/truststatements/"
        const val TRUST_PATH_IDENTITY = "identity/"
        const val TRUST_PATH_ISSUANCE = "issuance/"
        const val TRUST_PATH_VERIFICATION = "verification/"
    }
}
