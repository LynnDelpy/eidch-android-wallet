package ch.admin.foitt.wallet.platform.credential.domain.model

data class AnyIssuerDisplay(
    override val locale: String? = null,
    override val name: String? = null,
    val logo: String? = null,
    val logoAltText: String? = null,
) : AnyDisplay
