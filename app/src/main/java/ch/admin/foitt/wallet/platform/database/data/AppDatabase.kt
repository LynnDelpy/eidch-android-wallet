@file:Suppress("TooManyFunctions")

package ch.admin.foitt.wallet.platform.database.data

import androidx.room.AutoMigration
import androidx.room.Dao
import androidx.room.Database
import androidx.room.RawQuery
import androidx.room.RoomDatabase
import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.sqlite.db.SupportSQLiteQuery
import ch.admin.foitt.wallet.platform.database.data.AppDatabase.Companion.DATABASE_VERSION
import ch.admin.foitt.wallet.platform.database.data.dao.ClientAttestationDao
import ch.admin.foitt.wallet.platform.database.data.dao.CredentialClaimClusterDisplayEntityDao
import ch.admin.foitt.wallet.platform.database.data.dao.CredentialClaimClusterEntityDao
import ch.admin.foitt.wallet.platform.database.data.dao.CredentialClaimDao
import ch.admin.foitt.wallet.platform.database.data.dao.CredentialClaimDisplayDao
import ch.admin.foitt.wallet.platform.database.data.dao.CredentialDao
import ch.admin.foitt.wallet.platform.database.data.dao.CredentialDisplayDao
import ch.admin.foitt.wallet.platform.database.data.dao.CredentialIssuerDisplayDao
import ch.admin.foitt.wallet.platform.database.data.dao.CredentialKeyBindingEntityDao
import ch.admin.foitt.wallet.platform.database.data.dao.CredentialWithDisplaysAndClustersDao
import ch.admin.foitt.wallet.platform.database.data.dao.CredentialWithKeyBindingDao
import ch.admin.foitt.wallet.platform.database.data.dao.EIdRequestCaseDao
import ch.admin.foitt.wallet.platform.database.data.dao.EIdRequestCaseWithStateDao
import ch.admin.foitt.wallet.platform.database.data.dao.EIdRequestStateDao
import ch.admin.foitt.wallet.platform.database.data.dao.RawCredentialDataDao
import ch.admin.foitt.wallet.platform.database.domain.model.ClientAttestation
import ch.admin.foitt.wallet.platform.database.domain.model.Credential
import ch.admin.foitt.wallet.platform.database.domain.model.CredentialClaim
import ch.admin.foitt.wallet.platform.database.domain.model.CredentialClaimClusterDisplayEntity
import ch.admin.foitt.wallet.platform.database.domain.model.CredentialClaimClusterEntity
import ch.admin.foitt.wallet.platform.database.domain.model.CredentialClaimDisplay
import ch.admin.foitt.wallet.platform.database.domain.model.CredentialDisplay
import ch.admin.foitt.wallet.platform.database.domain.model.CredentialIssuerDisplay
import ch.admin.foitt.wallet.platform.database.domain.model.CredentialKeyBindingEntity
import ch.admin.foitt.wallet.platform.database.domain.model.DatabaseError
import ch.admin.foitt.wallet.platform.database.domain.model.EIdRequestCase
import ch.admin.foitt.wallet.platform.database.domain.model.EIdRequestState
import ch.admin.foitt.wallet.platform.database.domain.model.RawCredentialData
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.coroutines.runSuspendCatching
import com.github.michaelbull.result.mapError
import net.zetetic.database.sqlcipher.SQLiteDatabase
import timber.log.Timber

@Database(
    entities = [
        Credential::class,
        CredentialDisplay::class,
        CredentialClaim::class,
        CredentialClaimDisplay::class,
        CredentialIssuerDisplay::class,
        CredentialClaimClusterEntity::class,
        CredentialClaimClusterDisplayEntity::class,
        CredentialKeyBindingEntity::class,
        EIdRequestCase::class,
        EIdRequestState::class,
        RawCredentialData::class,
        ClientAttestation::class,
    ],
    version = DATABASE_VERSION,
    autoMigrations = [
        AutoMigration(from = 3, to = 4),
        AutoMigration(from = 4, to = 5),
        AutoMigration(from = 7, to = 8),
        AutoMigration(from = 8, to = 9),
    ], // see also migrations in SqlCipherDatabaseInitializer
    exportSchema = true,
)
@Suppress("TooManyFunctions")
abstract class AppDatabase : RoomDatabase() {
    // each DAO must be defined as an abstract method
    abstract fun credentialDao(): CredentialDao
    abstract fun credentialClaimDao(): CredentialClaimDao
    abstract fun credentialClaimDisplayDao(): CredentialClaimDisplayDao
    abstract fun credentialDisplayDao(): CredentialDisplayDao
    abstract fun credentialIssuerDisplayDao(): CredentialIssuerDisplayDao
    abstract fun credentialWithDisplaysAndClustersDao(): CredentialWithDisplaysAndClustersDao
    abstract fun credentialClaimClusterEntityDao(): CredentialClaimClusterEntityDao
    abstract fun credentialClaimClusterDisplayEntityDao(): CredentialClaimClusterDisplayEntityDao
    abstract fun credentialKeyBindingEntityDao(): CredentialKeyBindingEntityDao
    abstract fun credentialWithKeyBindingDao(): CredentialWithKeyBindingDao

    abstract fun eIdRequestCaseDao(): EIdRequestCaseDao
    abstract fun eIdRequestStateDao(): EIdRequestStateDao
    abstract fun eIdRequestCaseWithStateDao(): EIdRequestCaseWithStateDao
    abstract fun clientAttestationDao(): ClientAttestationDao

    abstract fun rawCredentialDataDao(): RawCredentialDataDao

    abstract fun decryptionTestDao(): DecryptionTestDao

    fun changePassword(newPassword: ByteArray): Result<Unit, DatabaseError.ReKeyFailed> =
        runSuspendCatching {
            val database = openHelper.writableDatabase as SQLiteDatabase
            database.changePassword(newPassword)
        }.mapError { throwable ->
            DatabaseError.ReKeyFailed(throwable)
        }

    suspend fun tryDecrypt(): Result<Unit, DatabaseError.WrongPassphrase> {
        return runSuspendCatching {
            decryptionTestDao().test()
            Unit
        }.mapError { throwable ->
            Timber.d(t = throwable, message = "error",)
            DatabaseError.WrongPassphrase(throwable)
        }
    }

    @Dao
    interface DecryptionTestDao {
        // Returns an Int if database decryption was successful
        // https://www.zetetic.net/sqlcipher/sqlcipher-api/#testing-the-key
        @RawQuery
        suspend fun test(query: SupportSQLiteQuery = SimpleSQLiteQuery("SELECT count(*) FROM sqlite_master")): Int
    }

    companion object {
        internal const val DATABASE_VERSION = 9 // db scheme v3.10
    }
}
