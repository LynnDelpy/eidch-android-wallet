package ch.admin.foitt.wallet.platform.trustRegistry.domain.usecase

import ch.admin.foitt.wallet.platform.trustRegistry.domain.model.MetadataV1TrustStatement
import ch.admin.foitt.wallet.platform.trustRegistry.domain.model.ProcessMetadataV1TrustStatementError
import com.github.michaelbull.result.Result

interface ProcessMetadataV1TrustStatement {
    suspend operator fun invoke(
        did: String,
    ): Result<MetadataV1TrustStatement, ProcessMetadataV1TrustStatementError>
}
