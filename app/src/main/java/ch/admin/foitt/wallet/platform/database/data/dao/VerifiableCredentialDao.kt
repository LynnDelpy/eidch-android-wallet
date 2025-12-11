package ch.admin.foitt.wallet.platform.database.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ch.admin.foitt.wallet.platform.database.domain.model.CredentialStatus
import ch.admin.foitt.wallet.platform.database.domain.model.VerifiableCredentialEntity

@Dao
interface VerifiableCredentialDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(verifiableCredential: VerifiableCredentialEntity): Long

    @Query("UPDATE VerifiableCredentialEntity SET status = :status, updatedAt = :updatedAt WHERE credentialId = :id")
    fun updateStatusByCredentialId(id: Long, status: CredentialStatus, updatedAt: Long): Int

    @Query("DELETE FROM VerifiableCredentialEntity WHERE credentialId = :id")
    fun deleteById(id: Long)

    @Query("SELECT * FROM VerifiableCredentialEntity WHERE credentialId = :id")
    fun getById(id: Long): VerifiableCredentialEntity
}
