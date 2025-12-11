package ch.admin.foitt.wallet.platform.ssi.domain.model

sealed interface CredentialClaimItem {
    val id: Long
    val localizedLabel: String
    val order: Int
    val isSensitive: Boolean
}
