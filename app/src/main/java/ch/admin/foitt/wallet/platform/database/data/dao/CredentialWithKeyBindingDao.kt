package ch.admin.foitt.wallet.platform.database.data.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import ch.admin.foitt.wallet.platform.database.domain.model.CredentialWithKeyBinding

@Dao
interface CredentialWithKeyBindingDao {
    @Transaction
    @Query("SELECT * FROM credential")
    fun getAll(): List<CredentialWithKeyBinding>

    @Transaction
    @Query("SELECT * FROM credential WHERE id = :id")
    fun getCredentialWithKeyBindingById(id: Long): CredentialWithKeyBinding
}
