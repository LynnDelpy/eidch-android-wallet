package ch.admin.foitt.wallet.platform.database.data.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import ch.admin.foitt.wallet.platform.database.domain.model.CredentialWithDisplaysAndClusters
import kotlinx.coroutines.flow.Flow

@Dao
interface CredentialWithDisplaysAndClustersDao {
    @Transaction
    @Query("SELECT * FROM credential WHERE id = :id")
    fun getCredentialWithDisplaysAndClustersFlowById(id: Long): Flow<CredentialWithDisplaysAndClusters>

    @Transaction
    @Query("SELECT * FROM credential WHERE id = :id")
    fun getNullableCredentialWithDisplaysAndClustersFlowById(id: Long): Flow<CredentialWithDisplaysAndClusters?>

    @Transaction
    @Query("SELECT * FROM credential ORDER BY createdAt DESC")
    fun getCredentialsWithDisplaysAndClustersFlow(): Flow<List<CredentialWithDisplaysAndClusters>>
}
