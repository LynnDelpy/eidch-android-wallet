package ch.admin.foitt.wallet.platform.database.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ch.admin.foitt.wallet.platform.database.domain.model.DeferredCredentialEntity

@Dao
interface DeferredCredentialDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(deferredCredential: DeferredCredentialEntity): Long

    @Query("DELETE FROM DeferredCredentialEntity WHERE credentialId = :id")
    fun deleteById(id: Long)

    @Query("SELECT * FROM DeferredCredentialEntity WHERE credentialId = :id")
    fun getById(id: Long): DeferredCredentialEntity
}
