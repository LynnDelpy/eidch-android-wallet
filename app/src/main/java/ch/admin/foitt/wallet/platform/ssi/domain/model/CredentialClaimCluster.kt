package ch.admin.foitt.wallet.platform.ssi.domain.model

data class CredentialClaimCluster(
    val id: Long,
    override val order: Int,
    override val localizedLabel: String,
    val parentId: Long?,
    val items: MutableList<CredentialClaimItem>,
    var numberOfNonClusterChildren: Int = -1
) : CredentialClaimItem
