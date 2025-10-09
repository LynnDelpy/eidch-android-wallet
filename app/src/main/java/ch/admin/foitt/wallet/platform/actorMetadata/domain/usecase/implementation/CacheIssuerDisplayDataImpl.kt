package ch.admin.foitt.wallet.platform.actorMetadata.domain.usecase.implementation

import ch.admin.foitt.wallet.platform.actorMetadata.domain.model.ActorDisplayData
import ch.admin.foitt.wallet.platform.actorMetadata.domain.model.ActorField
import ch.admin.foitt.wallet.platform.actorMetadata.domain.model.ActorType
import ch.admin.foitt.wallet.platform.actorMetadata.domain.usecase.CacheIssuerDisplayData
import ch.admin.foitt.wallet.platform.actorMetadata.domain.usecase.InitializeActorForScope
import ch.admin.foitt.wallet.platform.credential.domain.model.AnyIssuerDisplay
import ch.admin.foitt.wallet.platform.database.domain.model.DisplayLanguage
import ch.admin.foitt.wallet.platform.navigation.domain.model.ComponentScope
import ch.admin.foitt.wallet.platform.trustRegistry.domain.model.MetadataV1TrustStatement
import ch.admin.foitt.wallet.platform.trustRegistry.domain.model.TrustCheckResult
import ch.admin.foitt.wallet.platform.trustRegistry.domain.model.TrustStatus
import ch.admin.foitt.wallet.platform.trustRegistry.domain.model.VcSchemaTrustStatus
import javax.inject.Inject

internal class CacheIssuerDisplayDataImpl @Inject constructor(
    private val initializeActorForScope: InitializeActorForScope,
) : CacheIssuerDisplayData {
    override suspend fun invoke(
        trustCheckResult: TrustCheckResult?,
        issuerDisplays: List<AnyIssuerDisplay>,
    ) {
        val trustStatement = trustCheckResult?.actorTrustStatement

        val trustStatementStatus = if (trustStatement != null) {
            TrustStatus.TRUSTED
        } else {
            TrustStatus.NOT_TRUSTED
        }

        val vcSchemaTrustStatus = trustCheckResult?.vcSchemaTrustStatus ?: VcSchemaTrustStatus.UNPROTECTED

        val issuerTrustNameDisplay: List<ActorField<String>>? = issuerDisplays.toIssuerName()
        val issuerTrustLogoDisplay: List<ActorField<String>>? = issuerDisplays.toIssuerLogo()
        val preferredLanguage = when (trustStatement) {
            is MetadataV1TrustStatement -> trustStatement.preferredLanguage
            else -> null
        }

        val offerIssuerDisplay = ActorDisplayData(
            name = issuerTrustNameDisplay,
            image = issuerTrustLogoDisplay,
            trustStatus = trustStatementStatus,
            vcSchemaTrustStatus = vcSchemaTrustStatus,
            preferredLanguage = preferredLanguage,
            actorType = ActorType.ISSUER,
        )

        initializeActorForScope(
            actorDisplayData = offerIssuerDisplay,
            componentScope = ComponentScope.CredentialIssuer,
        )
    }

    private fun List<AnyIssuerDisplay>.toIssuerName(): List<ActorField<String>> = map { entry ->
        ActorField(
            value = entry.name,
            locale = entry.locale ?: DisplayLanguage.UNKNOWN,
        )
    }

    private fun List<AnyIssuerDisplay>.toIssuerLogo(): List<ActorField<String>> = mapNotNull { entry ->
        entry.logo?.let {
            ActorField(
                value = entry.logo,
                locale = entry.locale ?: DisplayLanguage.UNKNOWN,
            )
        }
    }
}
