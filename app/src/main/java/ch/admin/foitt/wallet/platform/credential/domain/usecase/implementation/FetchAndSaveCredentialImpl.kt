package ch.admin.foitt.wallet.platform.credential.domain.usecase.implementation

import ch.admin.foitt.openid4vc.domain.model.credentialoffer.CredentialOffer
import ch.admin.foitt.openid4vc.domain.model.credentialoffer.FetchCredentialByConfigError
import ch.admin.foitt.openid4vc.domain.model.credentialoffer.FetchIssuerCredentialInfoError
import ch.admin.foitt.openid4vc.domain.model.credentialoffer.PrepareFetchVerifiableCredentialError
import ch.admin.foitt.openid4vc.domain.model.credentialoffer.metadata.AnyCredentialConfiguration
import ch.admin.foitt.openid4vc.domain.usecase.FetchCredentialByConfig
import ch.admin.foitt.openid4vc.domain.usecase.FetchRawAndParsedIssuerCredentialInfo
import ch.admin.foitt.openid4vc.domain.usecase.GetVerifiableCredentialParams
import ch.admin.foitt.wallet.platform.actorMetadata.domain.usecase.CacheIssuerDisplayData
import ch.admin.foitt.wallet.platform.credential.domain.model.CredentialError
import ch.admin.foitt.wallet.platform.credential.domain.model.FetchCredentialError
import ch.admin.foitt.wallet.platform.credential.domain.model.GenerateCredentialDisplaysError
import ch.admin.foitt.wallet.platform.credential.domain.model.SaveCredentialError
import ch.admin.foitt.wallet.platform.credential.domain.model.toFetchCredentialError
import ch.admin.foitt.wallet.platform.credential.domain.usecase.FetchAndSaveCredential
import ch.admin.foitt.wallet.platform.credential.domain.usecase.GenerateAnyDisplays
import ch.admin.foitt.wallet.platform.credential.domain.usecase.SaveCredential
import ch.admin.foitt.wallet.platform.database.domain.model.RawCredentialData
import ch.admin.foitt.wallet.platform.environmentSetup.domain.repository.EnvironmentSetupRepository
import ch.admin.foitt.wallet.platform.holderBinding.domain.model.GenerateKeyPairError
import ch.admin.foitt.wallet.platform.holderBinding.domain.usecase.GenerateKeyPair
import ch.admin.foitt.wallet.platform.oca.domain.model.FetchVcMetadataByFormatError
import ch.admin.foitt.wallet.platform.oca.domain.usecase.FetchVcMetadataByFormat
import ch.admin.foitt.wallet.platform.oca.domain.usecase.OcaBundler
import ch.admin.foitt.wallet.platform.trustRegistry.domain.model.IdentityV1TrustStatement
import ch.admin.foitt.wallet.platform.trustRegistry.domain.model.MetadataV1TrustStatement
import ch.admin.foitt.wallet.platform.trustRegistry.domain.model.TrustCheckResult
import ch.admin.foitt.wallet.platform.trustRegistry.domain.model.TrustStatement
import ch.admin.foitt.wallet.platform.trustRegistry.domain.model.TrustStatementActor
import ch.admin.foitt.wallet.platform.trustRegistry.domain.model.VcSchemaTrustStatus
import ch.admin.foitt.wallet.platform.trustRegistry.domain.usecase.FetchVcSchemaTrustStatus
import ch.admin.foitt.wallet.platform.trustRegistry.domain.usecase.ProcessIdentityV1TrustStatement
import ch.admin.foitt.wallet.platform.trustRegistry.domain.usecase.ProcessMetadataV1TrustStatement
import ch.admin.foitt.wallet.platform.utils.compress
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.coroutines.coroutineBinding
import com.github.michaelbull.result.get
import com.github.michaelbull.result.getOrElse
import com.github.michaelbull.result.mapError
import javax.inject.Inject

class FetchAndSaveCredentialImpl @Inject constructor(
    private val fetchRawAndParsedIssuerCredentialInfo: FetchRawAndParsedIssuerCredentialInfo,
    private val getVerifiableCredentialParams: GetVerifiableCredentialParams,
    private val generateKeyPair: GenerateKeyPair,
    private val fetchCredentialByConfig: FetchCredentialByConfig,
    private val fetchVcMetadataByFormat: FetchVcMetadataByFormat,
    private val ocaBundler: OcaBundler,
    private val environmentSetupRepository: EnvironmentSetupRepository,
    private val processMetadataV1TrustStatement: ProcessMetadataV1TrustStatement,
    private val processIdentityV1TrustStatement: ProcessIdentityV1TrustStatement,
    private val fetchVcSchemaTrustStatus: FetchVcSchemaTrustStatus,
    private val generateAnyDisplays: GenerateAnyDisplays,
    private val cacheIssuerDisplayData: CacheIssuerDisplayData,
    private val saveCredential: SaveCredential
) : FetchAndSaveCredential {
    override suspend fun invoke(credentialOffer: CredentialOffer): Result<Long, FetchCredentialError> = coroutineBinding {
        val rawAndParsedCredentialInfo =
            fetchRawAndParsedIssuerCredentialInfo(credentialOffer.credentialIssuer)
                .mapError(FetchIssuerCredentialInfoError::toFetchCredentialError)
                .bind()

        val issuerInfo = rawAndParsedCredentialInfo.issuerCredentialInfo

        val config = getCredentialConfig(
            credentials = credentialOffer.credentialConfigurationIds,
            credentialConfigurations = issuerInfo.credentialConfigurations
        ).bind()

        val verifiableCredentialParams = getVerifiableCredentialParams(
            credentialConfiguration = config,
            credentialOffer = credentialOffer,
        ).mapError(PrepareFetchVerifiableCredentialError::toFetchCredentialError).bind()

        val keyPair = when (val proofTypeConfig = verifiableCredentialParams.proofTypeConfig) {
            null -> null
            else -> generateKeyPair(proofTypeConfig)
                .mapError(GenerateKeyPairError::toFetchCredentialError)
                .bind()
        }

        val anyCredential = fetchCredentialByConfig(
            verifiableCredentialParams = verifiableCredentialParams,
            keyPair = keyPair?.keyPair,
            attestationJwt = keyPair?.attestationJwt
        ).mapError(FetchCredentialByConfigError::toFetchCredentialError).bind()

        val vcMetadata = fetchVcMetadataByFormat(anyCredential)
            .mapError(FetchVcMetadataByFormatError::toFetchCredentialError)
            .bind()

        val rawOcaBundle = vcMetadata.rawOcaBundle?.rawOcaBundle
        val ocaBundle = rawOcaBundle?.let {
            ocaBundler(it).get()
        }

        val trustCheckResult = anyCredential.issuer?.let { issuerDid ->
            fetchTrustForIssuance(issuerDid, anyCredential.vcSchemaId)
        }

        val displays = generateAnyDisplays(
            anyCredential = anyCredential,
            issuerInfo = issuerInfo,
            trustIssuerNames = getTrustIssuerNames(trustCheckResult?.actorTrustStatement),
            metadata = config,
            ocaBundle = ocaBundle,
        ).mapError(GenerateCredentialDisplaysError::toFetchCredentialError).bind()

        cacheIssuerDisplayData(trustCheckResult, displays.issuerDisplays)

        val rawCredentialData = RawCredentialData(
            credentialId = -1,
            rawOcaBundle = rawOcaBundle?.toByteArray()?.compress(),
            rawOIDMetadata = rawAndParsedCredentialInfo.rawIssuerCredentialInfo.toByteArray().compress()
        )

        saveCredential(
            anyCredential = anyCredential,
            anyDisplays = displays,
            rawCredentialData = rawCredentialData
        ).mapError(SaveCredentialError::toFetchCredentialError).bind()
    }

    private fun getCredentialConfig(
        credentials: List<String>,
        credentialConfigurations: List<AnyCredentialConfiguration>
    ): Result<AnyCredentialConfiguration, FetchCredentialError> {
        val matchingCredentials = credentialConfigurations.filter { it.identifier in credentials }
        return if (matchingCredentials.isEmpty()) {
            Err(CredentialError.UnsupportedCredentialIdentifier)
        } else {
            Ok(matchingCredentials.first())
        }
    }

    private suspend fun fetchTrustForIssuance(
        issuerDid: String,
        vcSchemaId: String,
    ): TrustCheckResult {
        return if (environmentSetupRepository.useMetadataV1TrustStatement) {
            val metadataTrustStatement = processMetadataV1TrustStatement(issuerDid).get()

            TrustCheckResult(
                actorTrustStatement = metadataTrustStatement,
                vcSchemaTrustStatus = VcSchemaTrustStatus.UNPROTECTED,
            )
        } else {
            val identityTrustStatement = processIdentityV1TrustStatement(issuerDid).get()

            val issuanceTrustStatus = fetchVcSchemaTrustStatus(
                trustStatementActor = TrustStatementActor.ISSUER,
                actorDid = issuerDid,
                vcSchemaId = vcSchemaId,
            ).getOrElse { VcSchemaTrustStatus.UNPROTECTED }

            TrustCheckResult(
                actorTrustStatement = identityTrustStatement,
                vcSchemaTrustStatus = issuanceTrustStatus,
            )
        }
    }

    private fun getTrustIssuerNames(trustStatement: TrustStatement?): Map<String, String>? = when (trustStatement) {
        is MetadataV1TrustStatement -> trustStatement.orgName
        is IdentityV1TrustStatement -> trustStatement.entityName
        else -> null
    }
}
