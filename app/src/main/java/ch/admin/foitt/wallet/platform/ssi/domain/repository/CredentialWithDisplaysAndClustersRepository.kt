package ch.admin.foitt.wallet.platform.ssi.domain.repository

import ch.admin.foitt.wallet.platform.database.domain.model.CredentialWithDisplaysAndClusters
import ch.admin.foitt.wallet.platform.ssi.domain.model.CredentialWithDisplaysAndClustersRepositoryError
import com.github.michaelbull.result.Result
import kotlinx.coroutines.flow.Flow

interface CredentialWithDisplaysAndClustersRepository {
    fun getCredentialWithDisplaysAndClustersFlowById(
        credentialId: Long
    ): Flow<Result<CredentialWithDisplaysAndClusters, CredentialWithDisplaysAndClustersRepositoryError>>

    fun getNullableCredentialWithDisplaysAndClustersFlowById(
        credentialId: Long
    ): Flow<Result<CredentialWithDisplaysAndClusters?, CredentialWithDisplaysAndClustersRepositoryError>>

    fun getCredentialsWithDisplaysAndClustersFlow():
        Flow<Result<List<CredentialWithDisplaysAndClusters>, CredentialWithDisplaysAndClustersRepositoryError>>
}
