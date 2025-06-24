package ch.admin.foitt.wallet.platform.credential.domain.usecase.implementation

import ch.admin.foitt.openid4vc.domain.model.credentialoffer.metadata.AnyCredentialConfiguration
import ch.admin.foitt.openid4vc.domain.model.credentialoffer.metadata.Claim
import ch.admin.foitt.wallet.platform.credential.domain.model.AnyClaimDisplay
import ch.admin.foitt.wallet.platform.credential.domain.model.AnyCredentialDisplay
import ch.admin.foitt.wallet.platform.credential.domain.model.CredentialError
import ch.admin.foitt.wallet.platform.credential.domain.model.GenerateMetadataDisplaysError
import ch.admin.foitt.wallet.platform.credential.domain.model.MetadataDisplays
import ch.admin.foitt.wallet.platform.credential.domain.model.toAnyCredentialDisplay
import ch.admin.foitt.wallet.platform.credential.domain.model.toGenerateMetadataDisplaysError
import ch.admin.foitt.wallet.platform.credential.domain.usecase.GenerateMetadataDisplays
import ch.admin.foitt.wallet.platform.credential.domain.util.addFallbackLanguageIfNecessary
import ch.admin.foitt.wallet.platform.database.domain.model.Cluster
import ch.admin.foitt.wallet.platform.database.domain.model.CredentialClaim
import ch.admin.foitt.wallet.platform.database.domain.model.DisplayLanguage
import ch.admin.foitt.wallet.platform.ssi.domain.model.ValueType
import ch.admin.foitt.wallet.platform.utils.JsonParsingError
import ch.admin.foitt.wallet.platform.utils.SafeJson
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.coroutines.coroutineBinding
import com.github.michaelbull.result.mapError
import javax.inject.Inject

class GenerateMetadataDisplaysImpl @Inject constructor(
    private val safeJson: SafeJson,
) : GenerateMetadataDisplays {
    override suspend fun invoke(
        credentialClaims: Map<String, String>,
        metadata: AnyCredentialConfiguration,
    ): Result<MetadataDisplays, GenerateMetadataDisplaysError> = coroutineBinding {
        val localizedCredentialDisplays = metadata.display?.map {
            it.toAnyCredentialDisplay()
        }.addFallbackLanguageIfNecessary {
            AnyCredentialDisplay(name = metadata.identifier, locale = DisplayLanguage.FALLBACK)
        }

        val localizedClaims = createLocalizedCredentialClaims(
            credentialConfiguration = metadata,
            credentialClaims = credentialClaims,
        ).bind()

        val cluster = Cluster(
            order = -1,
            claims = localizedClaims
        )

        MetadataDisplays(
            credentialDisplays = localizedCredentialDisplays,
            clusters = listOf(cluster)
        )
    }

    private suspend fun createLocalizedCredentialClaims(
        credentialConfiguration: AnyCredentialConfiguration,
        credentialClaims: Map<String, String>,
    ): Result<Map<CredentialClaim, List<AnyClaimDisplay>>, GenerateMetadataDisplaysError> = coroutineBinding {
        val metadataClaims = getMetadataClaims(credentialConfiguration = credentialConfiguration).bind()

        credentialClaims.map { (claimKey, claimValue) ->
            val metadataClaim = metadataClaims[claimKey]
            val credentialClaim = CredentialClaim(
                clusterId = UNKNOWN_CLUSTER_DISPLAY_ID,
                key = claimKey,
                value = claimValue,
                valueType = metadataClaim?.valueType ?: DEFAULT_VALUE_TYPE,
                order = credentialConfiguration.order?.indexOf(claimKey) ?: -1
            )
            val claimDisplays: List<AnyClaimDisplay> = metadataClaim?.display?.map {
                AnyClaimDisplay(
                    locale = it.locale,
                    name = it.name,
                )
            }.addFallbackLanguageIfNecessary {
                AnyClaimDisplay(name = claimKey, locale = DisplayLanguage.FALLBACK)
            }
            credentialClaim to claimDisplays
        }.toMap()
    }

    private fun getMetadataClaims(
        credentialConfiguration: AnyCredentialConfiguration,
    ): Result<Map<String, Claim>, GenerateMetadataDisplaysError> {
        val jsonString = credentialConfiguration.claims ?: return Err(CredentialError.InvalidGenerateMetadataClaims)
        return safeJson.safeDecodeStringTo<Map<String, Claim>>(jsonString)
            .mapError(JsonParsingError::toGenerateMetadataDisplaysError)
    }

    companion object {
        private const val UNKNOWN_CLUSTER_DISPLAY_ID = -1L
        private val DEFAULT_VALUE_TYPE = ValueType.STRING.value
    }
}
