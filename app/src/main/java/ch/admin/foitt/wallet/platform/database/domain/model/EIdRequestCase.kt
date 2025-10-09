package ch.admin.foitt.wallet.platform.database.domain.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import ch.admin.foitt.wallet.platform.eIdApplicationProcess.domain.model.IdentityType
import java.time.Instant

@Entity
data class EIdRequestCase(
    @PrimaryKey
    val id: String, // caseId from the api
    val rawMrz: String,
    val documentNumber: String,
    @ColumnInfo(
        defaultValue = "SWISS_IDK"
    )
    val selectedDocumentType: IdentityType,
    val firstName: String,
    val lastName: String,
    val createdAt: Long = Instant.now().epochSecond,
)
