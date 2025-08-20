package ch.admin.foitt.wallet.platform.database.data

import androidx.core.database.getStringOrNull
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import ch.admin.foitt.openid4vc.domain.model.credentialoffer.metadata.CredentialFormat
import ch.admin.foitt.openid4vc.domain.model.keyBinding.KeyBindingType
import ch.admin.foitt.openid4vc.domain.model.sdjwt.SdJwt
import ch.admin.foitt.wallet.platform.database.domain.model.Converters
import ch.admin.foitt.wallet.platform.eIdApplicationProcess.domain.model.LegalRepresentativeConsent

// DB schema v3.1 to v3.3
internal val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE Credential ADD COLUMN validFrom INTEGER")
        db.execSQL("ALTER TABLE Credential ADD COLUMN validUntil INTEGER")

        val cursor = db.query("SELECT id, format, payload FROM CREDENTIAL")
        while (cursor.moveToNext()) {
            val id = cursor.getLong(cursor.getColumnIndexOrThrow("id"))
            val payload = cursor.getString(cursor.getColumnIndexOrThrow("payload"))
            val formatString = cursor.getString(cursor.getColumnIndexOrThrow("format"))
            val format = Converters().toCredentialFormat(formatString)

            val (validFrom, validUntil) = when (format) {
                CredentialFormat.VC_SD_JWT -> {
                    val sdJwt = SdJwt(payload)
                    Pair(sdJwt.nbfInstant?.epochSecond, sdJwt.expInstant?.epochSecond)
                }

                else -> error("invalid format")
            }

            db.execSQL("UPDATE Credential SET validFrom = $validFrom, validUntil = $validUntil WHERE id = $id")
        }

        cursor.close()
    }
}

// DB schema v3.3 to v3.4
internal val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            "ALTER TABLE EIdRequestState " +
                "ADD COLUMN legalRepresentativeConsent TEXT NOT NULL " +
                "DEFAULT '${LegalRepresentativeConsent.NOT_REQUIRED.name}'"
        )
    }
}

// DB schema v3.4 to v3.5
// MIGRATION_3_4 is done with AutoMigration in [AppDatabase]

// DB schema v3.5 to v3.6
// MIGRATION_4_5 is done with AutoMigration in [AppDatabase]

// DB schema v3.6 to v3.7
internal val MIGRATION_5_6 = object : Migration(5, 6) {
    override fun migrate(db: SupportSQLiteDatabase) {
        // 1. add cluster table
        db.execSQL(
            "CREATE TABLE IF NOT EXISTS `CredentialClaimClusterEntity` (" +
                "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                "`credentialId` INTEGER NOT NULL, " +
                "`parentClusterId` INTEGER, " +
                "`order` INTEGER NOT NULL, " +
                "FOREIGN KEY(`credentialId`) REFERENCES `Credential`(`id`) ON UPDATE CASCADE ON DELETE CASCADE , " +
                "FOREIGN KEY(`parentClusterId`) REFERENCES `CredentialClaimClusterEntity`(`id`) ON UPDATE CASCADE ON DELETE CASCADE " +
                ")"
        )
        db.execSQL(
            "CREATE INDEX IF NOT EXISTS `index_CredentialClaimClusterEntity_credentialId` " +
                "ON `CredentialClaimClusterEntity` (`credentialId`)"
        )
        db.execSQL(
            "CREATE INDEX IF NOT EXISTS `index_CredentialClaimClusterEntity_parentClusterId` " +
                "ON `CredentialClaimClusterEntity` (`parentClusterId`)"
        )
        // 2. add cluster display table
        db.execSQL(
            "CREATE TABLE IF NOT EXISTS `CredentialClaimClusterDisplayEntity` (" +
                "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                "`clusterId` INTEGER NOT NULL, " +
                "`name` TEXT NOT NULL, " +
                "`locale` TEXT NOT NULL, " +
                "FOREIGN KEY(`clusterId`) REFERENCES `CredentialClaimClusterEntity`(`id`) ON UPDATE CASCADE ON DELETE CASCADE " +
                ")"
        )
        db.execSQL(
            "CREATE INDEX IF NOT EXISTS `index_CredentialClaimClusterDisplayEntity_clusterId` " +
                "ON `CredentialClaimClusterDisplayEntity` (`clusterId`)"
        )

        // 3. get credentials and for each insert a cluster
        val cursor = db.query("SELECT * FROM `Credential`")
        while (cursor.moveToNext()) {
            val credentialId = cursor.getLong(cursor.getColumnIndexOrThrow("id"))
            // set id of cluster to same id as credential had, to not mess up the claim foreign key ids
            db.execSQL("INSERT INTO `CredentialClaimClusterEntity` VALUES ($credentialId, $credentialId, NULL, -1)")
        }

        // 4. update claims table (add clusterId column with foreign key constraint to cluster table, remove credentialId column)
        // you can not just add a new column with a foreign key constraint -> recreate the table acc.
        // https://www.sqlite.org/lang_altertable.html#otheralter
        db.execSQL("PRAGMA foreign_keys = OFF")
        db.execSQL(
            "CREATE TABLE IF NOT EXISTS `new_CredentialClaim` (" +
                "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                "`clusterId` INTEGER NOT NULL, " +
                "`key` TEXT NOT NULL, " +
                "`value` TEXT NOT NULL, " +
                "`valueType` TEXT, " +
                "`valueDisplayInfo` TEXT, " +
                "`order` INTEGER NOT NULL, " +
                "FOREIGN KEY(`clusterId`) REFERENCES `CredentialClaimClusterEntity`(`id`) ON UPDATE CASCADE ON DELETE CASCADE " +
                ")"
        )

        db.execSQL(
            "INSERT INTO `new_CredentialClaim` (`id`, `clusterId`, `key`, `value`, `valueType`, `valueDisplayInfo`, `order`) " +
                "SELECT `id`, `credentialId`, `key`, `value`, `valueType`, `valueDisplayInfo`, `order` FROM `CredentialClaim`"
        )
        db.execSQL("DROP TABLE `CredentialClaim`")
        db.execSQL("ALTER TABLE `new_CredentialClaim` RENAME TO `CredentialClaim`")
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_CredentialClaim_clusterId` ON `CredentialClaim` (`clusterId`)")
        db.execSQL("PRAGMA foreign_key_check")
        db.execSQL("PRAGMA foreign_keys = ON")
    }
}

// DB schema v3.7 to v3.8
internal val MIGRATION_6_7 = object : Migration(6, 7) {
    override fun migrate(db: SupportSQLiteDatabase) {
        // Add new CredentialKeyBindingTable
        db.execSQL(
            "CREATE TABLE IF NOT EXISTS `CredentialKeyBindingEntity` (" +
                "`id` TEXT NOT NULL, " +
                "`credentialId` INTEGER NOT NULL, " +
                "`algorithm` TEXT NOT NULL, " +
                "`bindingType` TEXT NOT NULL, " +
                "`publicKey` BLOB, " +
                "`privateKey` BLOB, " +
                "PRIMARY KEY(`id`), " +
                "FOREIGN KEY(`credentialId`) REFERENCES `Credential`(`id`) ON UPDATE CASCADE ON DELETE CASCADE " +
                ")"
        )
        db.execSQL(
            "CREATE INDEX IF NOT EXISTS `index_CredentialKeyBindingEntity_credentialId` ON `CredentialKeyBindingEntity` (`credentialId`)"
        )

        // Get credentials and for each (where bindingIdentifier and bindingAlgo are not null) insert a key binding
        val cursor = db.query("SELECT * FROM Credential")
        while (cursor.moveToNext()) {
            val credentialId = cursor.getLong(cursor.getColumnIndexOrThrow("id"))
            val bindingIdentifier = cursor.getStringOrNull(cursor.getColumnIndexOrThrow("keyBindingIdentifier"))
            val bindingAlgo = cursor.getStringOrNull(cursor.getColumnIndexOrThrow("keyBindingAlgorithm"))

            if (bindingIdentifier != null && bindingAlgo != null) {
                db.execSQL(
                    "INSERT INTO `CredentialKeyBindingEntity` (" +
                        "`id`, `credentialId`, `algorithm`, `bindingType`, `publicKey`, `privateKey`" +
                        ") " +
                        "VALUES ('$bindingIdentifier', $credentialId, '$bindingAlgo', '${KeyBindingType.HARDWARE.name}', NULL, NULL)"
                )
            }
        }

        /*
        Dropping a column in SQLite is only supported from version 3.35 onwards. Android versions 11 and below are shipped with a maximum of
        SQLite 3.34. It is possible that there are devices that have our app installed, but not 3.35+, meaning we can not rely on the
        "DROP COLUMN" command and need to do the special migration of re-creating the table without the columns...
        https://www.sqlite.org/lang_altertable.html#otheralter
         */
        db.execSQL("PRAGMA foreign_keys = OFF")
        db.execSQL(
            "CREATE TABLE IF NOT EXISTS `new_Credential` (" +
                "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                "`status` TEXT NOT NULL, " +
                "`payload` TEXT NOT NULL, " +
                "`issuer` TEXT, " +
                "`format` TEXT NOT NULL, " +
                "`validFrom` INTEGER, " +
                "`validUntil` INTEGER, " +
                "`createdAt` INTEGER NOT NULL, " +
                "`updatedAt` INTEGER" +
                ")"
        )
        db.execSQL(
            "INSERT INTO `new_Credential` " +
                "SELECT `id`, `status`, `payload`, `issuer`, `format`, `validFrom`, `validUntil`, `createdAt`, `updatedAt` " +
                "FROM `Credential`"
        )
        db.execSQL("DROP TABLE `Credential`")
        db.execSQL("ALTER TABLE `new_Credential` RENAME TO `Credential`")
        db.execSQL("PRAGMA foreign_key_check")
        db.execSQL("PRAGMA foreign_keys = ON")
    }
}

// DB schema v3.8 to v3.9
// MIGRATION_7_8 is done with AutoMigration in [AppDatabase]

// DB schema v3.9 to v3.10
// MIGRATION_8_9 is done with AutoMigration in [AppDatabase]
