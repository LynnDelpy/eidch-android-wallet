package ch.admin.foitt.wallet.platform.database.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ch.admin.foitt.wallet.platform.database.domain.model.ClientAttestation

@Dao
interface ClientAttestationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(attestation: ClientAttestation): Long

    @Query("SELECT * FROM ClientAttestation ORDER BY createdAt DESC")
    suspend fun getAll(): List<ClientAttestation>

    @Delete
    fun delete(clientAttestation: ClientAttestation): Int
}
