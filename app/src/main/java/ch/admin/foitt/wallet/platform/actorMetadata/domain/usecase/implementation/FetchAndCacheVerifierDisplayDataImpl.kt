package ch.admin.foitt.wallet.platform.actorMetadata.domain.usecase.implementation

import ch.admin.foitt.openid4vc.domain.model.presentationRequest.ClientMetaData
import ch.admin.foitt.openid4vc.domain.model.presentationRequest.InputDescriptorFormat
import ch.admin.foitt.openid4vc.domain.model.presentationRequest.PresentationRequest
import ch.admin.foitt.wallet.platform.actorMetadata.domain.model.ActorDisplayData
import ch.admin.foitt.wallet.platform.actorMetadata.domain.model.ActorField
import ch.admin.foitt.wallet.platform.actorMetadata.domain.model.ActorType
import ch.admin.foitt.wallet.platform.actorMetadata.domain.usecase.FetchAndCacheVerifierDisplayData
import ch.admin.foitt.wallet.platform.actorMetadata.domain.usecase.InitializeActorForScope
import ch.admin.foitt.wallet.platform.environmentSetup.domain.repository.EnvironmentSetupRepository
import ch.admin.foitt.wallet.platform.navigation.domain.model.ComponentScope
import ch.admin.foitt.wallet.platform.trustRegistry.domain.model.IdentityV1TrustStatement
import ch.admin.foitt.wallet.platform.trustRegistry.domain.model.MetadataV1TrustStatement
import ch.admin.foitt.wallet.platform.trustRegistry.domain.model.TrustCheckResult
import ch.admin.foitt.wallet.platform.trustRegistry.domain.model.TrustStatementActor
import ch.admin.foitt.wallet.platform.trustRegistry.domain.model.TrustStatus
import ch.admin.foitt.wallet.platform.trustRegistry.domain.model.VcSchemaTrustStatus
import ch.admin.foitt.wallet.platform.trustRegistry.domain.usecase.FetchVcSchemaTrustStatus
import ch.admin.foitt.wallet.platform.trustRegistry.domain.usecase.ProcessIdentityV1TrustStatement
import ch.admin.foitt.wallet.platform.trustRegistry.domain.usecase.ProcessMetadataV1TrustStatement
import com.github.michaelbull.result.get
import com.github.michaelbull.result.getOrElse
import timber.log.Timber
import javax.inject.Inject

internal class FetchAndCacheVerifierDisplayDataImpl @Inject constructor(
    private val environmentSetupRepository: EnvironmentSetupRepository,
    private val processMetadataV1TrustStatement: ProcessMetadataV1TrustStatement,
    private val processIdentityV1TrustStatement: ProcessIdentityV1TrustStatement,
    private val fetchVcSchemaTrustStatus: FetchVcSchemaTrustStatus,
    private val initializeActorForScope: InitializeActorForScope,
) : FetchAndCacheVerifierDisplayData {
    override suspend fun invoke(
        presentationRequest: PresentationRequest,
        shouldFetchTrustStatement: Boolean,
    ) {
        val verifierNameDisplay = presentationRequest.clientMetaData?.toVerifierName()
        val verifierLogoDisplay = presentationRequest.clientMetaData?.toVerifierLogo()

        val trustCheckResult = if (shouldFetchTrustStatement) {
            fetchTrustForVerification(presentationRequest)
        } else {
            null
        }

        val trustStatement = trustCheckResult?.actorTrustStatement

        Timber.d("${trustStatement ?: "trust statement not evaluated or failed"}")

        val trustStatementStatus = if (trustStatement != null) {
            TrustStatus.TRUSTED
        } else {
            TrustStatus.NOT_TRUSTED
        }

        val vcSchemaTrustStatus = trustCheckResult?.vcSchemaTrustStatus ?: VcSchemaTrustStatus.UNPROTECTED

        val entityName = when (trustStatement) {
            is MetadataV1TrustStatement -> trustStatement.orgName
            is IdentityV1TrustStatement -> trustStatement.entityName
            else -> null
        }

        val verifierTrustNameDisplay: List<ActorField<String>>? = entityName?.toActorField() ?: verifierNameDisplay
        val verifierTrustLogoDisplay: List<ActorField<String>>? = verifierLogoDisplay
        val preferredLanguage = when (trustStatement) {
            is MetadataV1TrustStatement -> trustStatement.preferredLanguage
            else -> null
        }

        val presentationVerifierDisplay = ActorDisplayData(
            name = verifierTrustNameDisplay,
            image = verifierTrustLogoDisplay,
            trustStatus = trustStatementStatus,
            vcSchemaTrustStatus = vcSchemaTrustStatus,
            preferredLanguage = preferredLanguage,
            actorType = ActorType.VERIFIER,
        )

        initializeActorForScope(
            actorDisplayData = presentationVerifierDisplay,
            componentScope = ComponentScope.Verifier,
        )
    }

    private fun ClientMetaData.toVerifierName(): List<ActorField<String>> = clientNameList.map { entry ->
        ActorField(
            value = entry.clientName,
            locale = entry.locale,
        )
    }

    private fun ClientMetaData.toVerifierLogo(): List<ActorField<String>> = logoUriList.map { entry ->
        ActorField(
            value = entry.logoUri,
            locale = entry.locale,
        )
    }

    private suspend fun fetchTrustForVerification(presentationRequest: PresentationRequest): TrustCheckResult {
        val verifierDid = presentationRequest.clientId

        return if (environmentSetupRepository.useMetadataV1TrustStatement) {
            val metadataTrustStatement = processMetadataV1TrustStatement(verifierDid).get()

            TrustCheckResult(
                actorTrustStatement = metadataTrustStatement,
                vcSchemaTrustStatus = VcSchemaTrustStatus.UNPROTECTED,
            )
        } else {
            val identityTrustStatement = processIdentityV1TrustStatement(verifierDid).get()

            val vcSchemaId = getVcSchemaId(presentationRequest)

            val verificationTrustStatus = vcSchemaId?.let {
                fetchVcSchemaTrustStatus(
                    trustStatementActor = TrustStatementActor.VERIFIER,
                    actorDid = verifierDid,
                    vcSchemaId = vcSchemaId,
                ).getOrElse { VcSchemaTrustStatus.UNPROTECTED }
            } ?: VcSchemaTrustStatus.UNPROTECTED

            TrustCheckResult(
                actorTrustStatement = identityTrustStatement,
                vcSchemaTrustStatus = verificationTrustStatus
            )
        }
    }

    private fun getVcSchemaId(
        presentationRequest: PresentationRequest
    ) = presentationRequest.presentationDefinition.inputDescriptors.firstNotNullOfOrNull { inputDescriptor ->
        val filteredFormats =
            inputDescriptor.formats.filter { inputDescriptorFormat -> inputDescriptorFormat is InputDescriptorFormat.VcSdJwt }
        if (filteredFormats.isNotEmpty()) {
            val vctField = inputDescriptor.constraints.fields.firstOrNull { field -> field.path.contains("$.vct") }
            vctField?.filter?.const
        } else {
            null
        }
    }

    private fun <T> Map<String, T>.toActorField(): List<ActorField<T>> = map { entry ->
        ActorField(
            value = entry.value,
            locale = entry.key,
        )
    }
}
