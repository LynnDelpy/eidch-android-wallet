package ch.admin.foitt.wallet.platform.trustRegistry.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

interface MetadataTrustStatement : TrustStatement

@Serializable
data class MetadataV1TrustStatement(
    override val vct: String,
    override val iss: String,
    override val sub: String,
    override val iat: Long,
    override val status: TrustStatementStatus?,
    override val exp: Long?,
    override val nbf: Long?,

    @SerialName("orgName")
    val orgName: Map<String, String>,
    @SerialName("logoUri")
    val logoUri: Map<String, String>?,
    @SerialName("prefLang")
    val preferredLanguage: String,
    @SerialName("_sd_alg")
    val sdAlg: String,
) : MetadataTrustStatement
