package ch.admin.foitt.wallet.platform.ssi.domain.model

data class CredentialClaimImage(
    override val localizedLabel: String,
    override val order: Int,
    val imageData: ByteArray,
) : CredentialClaimItem {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CredentialClaimImage

        if (localizedLabel != other.localizedLabel) return false
        return imageData.contentEquals(other.imageData)
    }

    override fun hashCode(): Int {
        var result = localizedLabel.hashCode()
        result = 31 * result + imageData.contentHashCode()
        return result
    }
}
