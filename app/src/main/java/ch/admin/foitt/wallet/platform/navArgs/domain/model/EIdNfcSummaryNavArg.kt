package ch.admin.foitt.wallet.platform.navArgs.domain.model

data class EIdNfcSummaryNavArg(
    val caseId: String,
    val picture: ByteArray,
    val givenName: String,
    val surname: String,
    val documentId: String,
    val expiryDate: String,
)
