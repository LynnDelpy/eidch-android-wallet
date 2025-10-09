package ch.admin.foitt.wallet.platform.trustRegistry.domain.usecase.implementation

import ch.admin.foitt.openid4vc.domain.model.vcSdJwt.VcSdJwt
import ch.admin.foitt.wallet.platform.trustRegistry.domain.model.GetTrustUrlFromDidError
import ch.admin.foitt.wallet.platform.trustRegistry.domain.model.MetadataV1TrustStatement
import ch.admin.foitt.wallet.platform.trustRegistry.domain.model.ProcessMetadataV1TrustStatementError
import ch.admin.foitt.wallet.platform.trustRegistry.domain.model.TrustStatementRepositoryError
import ch.admin.foitt.wallet.platform.trustRegistry.domain.model.TrustStatementType
import ch.admin.foitt.wallet.platform.trustRegistry.domain.model.toProcessMetadataV1TrustStatementError
import ch.admin.foitt.wallet.platform.trustRegistry.domain.repository.TrustStatementRepository
import ch.admin.foitt.wallet.platform.trustRegistry.domain.usecase.GetTrustUrlFromDid
import ch.admin.foitt.wallet.platform.trustRegistry.domain.usecase.ProcessMetadataV1TrustStatement
import ch.admin.foitt.wallet.platform.trustRegistry.domain.usecase.ValidateTrustStatement
import ch.admin.foitt.wallet.platform.utils.JsonParsingError
import ch.admin.foitt.wallet.platform.utils.SafeJson
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.coroutines.coroutineBinding
import com.github.michaelbull.result.coroutines.runSuspendCatching
import com.github.michaelbull.result.get
import com.github.michaelbull.result.mapError
import javax.inject.Inject

class ProcessMetadataV1TrustStatementImpl @Inject constructor(
    private val getTrustUrlFromDid: GetTrustUrlFromDid,
    private val trustStatementRepository: TrustStatementRepository,
    private val validateTrustStatement: ValidateTrustStatement,
    private val safeJson: SafeJson,
) : ProcessMetadataV1TrustStatement {
    override suspend fun invoke(did: String): Result<MetadataV1TrustStatement, ProcessMetadataV1TrustStatementError> = coroutineBinding {
        val trustUrl = getTrustUrlFromDid(
            trustStatementType = TrustStatementType.METADATA,
            actorDid = did,
            vcSchemaId = null,
        )
            .mapError(GetTrustUrlFromDidError::toProcessMetadataV1TrustStatementError)
            .bind()

        val trustStatementsRaw = trustStatementRepository.fetchTrustStatements(trustUrl)
            .mapError(TrustStatementRepositoryError::toProcessMetadataV1TrustStatementError)
            .bind()

        val trustStatements = runSuspendCatching {
            trustStatementsRaw.map { VcSdJwt(it) }
        }.mapError { throwable ->
            throwable.toProcessMetadataV1TrustStatementError(message = "trust statement vc sd jwt creation failed")
        }.bind()

        val metadataStatements = trustStatements.filter { it.vct == METADATA_VCT }

        val validMetadataStatement = runSuspendCatching {
            metadataStatements.firstNotNullOf {
                validateTrustStatement(
                    trustStatement = it,
                    actorDid = did,
                ).get()
            }
        }.mapError { throwable ->
            throwable.toProcessMetadataV1TrustStatementError(message = "no trust statement was successfully validated")
        }.bind()

        val metadataTrustStatement = safeJson.safeDecodeElementTo<MetadataV1TrustStatement>(
            validMetadataStatement.sdJwtJson
        ).mapError(JsonParsingError::toProcessMetadataV1TrustStatementError)
            .bind()

        metadataTrustStatement
    }

    private companion object {
        const val METADATA_VCT = "TrustStatementMetadataV1"
    }
}
