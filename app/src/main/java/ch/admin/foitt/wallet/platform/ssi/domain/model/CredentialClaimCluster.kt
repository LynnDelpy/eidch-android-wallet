package ch.admin.foitt.wallet.platform.ssi.domain.model

data class CredentialClaimCluster(
    override val id: Long,
    override val order: Int,
    override val localizedLabel: String,
    override val isSensitive: Boolean = false,
    val parentId: Long?,
    val items: MutableList<CredentialClaimItem>,
    var numberOfNonClusterChildren: Int = -1,
) : CredentialClaimItem

fun List<CredentialClaimCluster>.getClaimIds(): List<Long> = this.flatMap { it.getClaimIds() }

private fun CredentialClaimCluster.getClaimIds(): List<Long> {
    val ids = mutableListOf<Long>()

    items.forEach { credentialClaimItem ->
        when (credentialClaimItem) {
            is CredentialClaimText, is CredentialClaimImage -> ids.add(credentialClaimItem.id)
            is CredentialClaimCluster -> ids.addAll(credentialClaimItem.getClaimIds())
        }
    }

    return ids
}
