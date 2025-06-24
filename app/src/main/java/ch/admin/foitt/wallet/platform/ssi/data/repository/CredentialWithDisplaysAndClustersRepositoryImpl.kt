package ch.admin.foitt.wallet.platform.ssi.data.repository

import ch.admin.foitt.wallet.platform.database.data.dao.DaoProvider
import ch.admin.foitt.wallet.platform.database.domain.model.CredentialWithDisplaysAndClusters
import ch.admin.foitt.wallet.platform.ssi.domain.model.CredentialWithDisplaysAndClustersRepositoryError
import ch.admin.foitt.wallet.platform.ssi.domain.model.toCredentialWithDisplaysAndClustersRepositoryError
import ch.admin.foitt.wallet.platform.ssi.domain.repository.CredentialWithDisplaysAndClustersRepository
import ch.admin.foitt.wallet.platform.utils.catchAndMap
import com.github.michaelbull.result.Result
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
class CredentialWithDisplaysAndClustersRepositoryImpl @Inject constructor(
    daoProvider: DaoProvider,
) : CredentialWithDisplaysAndClustersRepository {

    override fun getCredentialWithDisplaysAndClustersFlowById(
        credentialId: Long
    ): Flow<Result<CredentialWithDisplaysAndClusters, CredentialWithDisplaysAndClustersRepositoryError>> =
        credentialWithDisplaysAndClustersDaoFlow.flatMapLatest { dao ->
            dao?.getCredentialWithDisplaysAndClustersFlowById(credentialId)
                ?.catchAndMap { throwable ->
                    throwable.toCredentialWithDisplaysAndClustersRepositoryError("Error to get CredentialWithDisplaysAndClusters")
                } ?: emptyFlow()
        }

    override fun getNullableCredentialWithDisplaysAndClustersFlowById(
        credentialId: Long
    ): Flow<Result<CredentialWithDisplaysAndClusters?, CredentialWithDisplaysAndClustersRepositoryError>> =
        credentialWithDisplaysAndClustersDaoFlow.flatMapLatest { dao ->
            dao?.getNullableCredentialWithDisplaysAndClustersFlowById(credentialId)
                ?.catchAndMap { throwable ->
                    throwable.toCredentialWithDisplaysAndClustersRepositoryError("Error to get CredentialWithDisplaysAndClusters")
                } ?: emptyFlow()
        }

    override fun getCredentialsWithDisplaysAndClustersFlow():
        Flow<Result<List<CredentialWithDisplaysAndClusters>, CredentialWithDisplaysAndClustersRepositoryError>> =
        credentialWithDisplaysAndClustersDaoFlow.flatMapLatest { dao ->
            dao?.getCredentialsWithDisplaysAndClustersFlow()
                ?.catchAndMap { throwable ->
                    throwable.toCredentialWithDisplaysAndClustersRepositoryError("Error to get CredentialWithDisplaysAndClusters")
                } ?: emptyFlow()
        }

    private val credentialWithDisplaysAndClustersDaoFlow = daoProvider.credentialWithDisplaysAndClustersDaoFlow
}
