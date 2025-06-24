package ch.admin.foitt.wallet.platform.appAttestation.domain.model

/**
 * Attack Potential Resistance values as per ISO 18045
 *
 * [See OID4VCI](https://openid.net/specs/openid-4-verifiable-credential-issuance-1_0.html#name-attack-potential-resistance)
 */
enum class KeyAttestationStorage(val value: String) {
    ISO_18045_BASIC("iso_18045_basic"),
    ISO_18045_ENHANCED_BASIC("iso_18045_enhanced-basic"),
    ISO_18045_MODERATE("iso_18045_moderate"),
    ISO_18045_HIGH("iso_18045_high");

    companion object {
        fun get(value: String): KeyAttestationStorage? =
            KeyAttestationStorage.entries.firstOrNull { it.value == value }
    }
}
