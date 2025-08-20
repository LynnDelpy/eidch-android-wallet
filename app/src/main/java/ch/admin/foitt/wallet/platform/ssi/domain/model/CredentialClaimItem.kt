package ch.admin.foitt.wallet.platform.ssi.domain.model

sealed interface CredentialClaimItem {
    val localizedLabel: String
    val order: Int
}
