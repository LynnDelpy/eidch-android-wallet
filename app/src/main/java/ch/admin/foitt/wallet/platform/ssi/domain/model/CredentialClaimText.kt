package ch.admin.foitt.wallet.platform.ssi.domain.model

data class CredentialClaimText(
    override val localizedLabel: String,
    override val order: Int,
    val value: String?,
) : CredentialClaimItem
