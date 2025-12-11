package ch.admin.foitt.wallet.platform.ssi.domain.repository

import ch.admin.foitt.wallet.platform.database.domain.model.VerifiableCredentialWithDisplaysAndClusters
import ch.admin.foitt.wallet.platform.ssi.domain.model.VerifiableCredentialWithDisplaysAndClustersRepositoryError
import com.github.michaelbull.result.Result
import kotlinx.coroutines.flow.Flow

interface VerifiableCredentialWithDisplaysAndClustersRepository {
    fun getVerifiableCredentialWithDisplaysAndClustersFlowById(
        credentialId: Long
    ): Flow<Result<VerifiableCredentialWithDisplaysAndClusters, VerifiableCredentialWithDisplaysAndClustersRepositoryError>>

    fun getNullableVerifiableCredentialWithDisplaysAndClustersFlowById(
        credentialId: Long
    ): Flow<Result<VerifiableCredentialWithDisplaysAndClusters?, VerifiableCredentialWithDisplaysAndClustersRepositoryError>>

    fun getVerifiableCredentialsWithDisplaysAndClustersFlow():
        Flow<Result<List<VerifiableCredentialWithDisplaysAndClusters>, VerifiableCredentialWithDisplaysAndClustersRepositoryError>>
}
