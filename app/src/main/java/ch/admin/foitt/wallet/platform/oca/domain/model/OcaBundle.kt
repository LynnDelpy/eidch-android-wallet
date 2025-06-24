package ch.admin.foitt.wallet.platform.oca.domain.model

import ch.admin.foitt.wallet.platform.oca.domain.model.overlays.Overlay
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

typealias AttributeKey = String
typealias Locale = String
typealias DataSourceFormat = String
typealias JsonPath = String

@Serializable
data class OcaBundle(
    @SerialName("capture_bases") val captureBases: List<CaptureBase>,
    @SerialName("overlays") val overlays: List<Overlay>,
    @Transient val ocaClaimData: List<OcaClaimData> = emptyList(),
    @Transient val ocaCredentialData: List<OcaCredentialData> = emptyList(),
) {

    /**
     * Retrieves a specific Overlay Bundle attribute from the Capture Base.
     *
     * @param name The name of the Capture Base attribute.
     * @param digest An CESR digest of the associated Capture Base.
     * @return A [OcaClaimData] representing the Capture Base attributes with supplementary information from Overlays. When attribute is not found, `null` is returned.
     */
    fun getAttribute(name: String, digest: String): OcaClaimData? {
        return ocaClaimData.firstOrNull { it.captureBaseDigest == digest && it.name == name }
    }

    /**
     * Retrieves all Overlay Bundle attributes.
     *
     * @param digest An optional CESR digest of the associated Capture Base. Default: attributes for all Capture Base digests are considered.
     * @return A list of [OcaClaimData] representing the Capture Base attributes with supplementary information from Overlays.
     */
    fun getAttributes(digest: String? = null): List<OcaClaimData> {
        return if (digest == null) {
            ocaClaimData
        } else {
            ocaClaimData.filter { it.captureBaseDigest == digest }
        }
    }

    /**
     * Retrieves Overlay Bundle attribute associated to their JSONPath for the data source format.
     *
     * @param dataSourceFormat The format identifier for data source mapping, e.g. "vc+sd-jwt"
     * @param digest An optional CESR digest of the associated Capture Base. Default: attributes for all Capture Base digests are considered.
     * @return A map of JSONPath (pointing to data source property) to [OcaClaimData] (for associated Capture Base)
     */
    fun getAttributesForDataSourceFormat(
        dataSourceFormat: DataSourceFormat,
        digest: String?
    ): Map<JsonPath, OcaClaimData> {
        return getAttributes(digest = digest)
            .mapNotNull { attribute ->
                val jsonPath = attribute.dataSources[dataSourceFormat] ?: return@mapNotNull null
                jsonPath to attribute
            }.toMap()
    }

    fun getAttributeForJsonPath(jsonPath: String) = getAttributes().find { attribute ->
        val normalizedJsonPath = jsonPath.toDotNotation()
        attribute.dataSources.values.any { dataSourceJsonPath: String ->
            val normalizedDataSourceJsonPath = dataSourceJsonPath.toDotNotation()
            normalizedDataSourceJsonPath == normalizedJsonPath || validateJsonPaths(normalizedJsonPath, normalizedDataSourceJsonPath)
        }
    }

    private fun String.toDotNotation(): String {
        return this
            // Replace all bracket notations with dot notation, e.g. foo["bar"] -> foo.bar
            .replace(regex = bracketNotationRegex, replacement = ".\${selector}")
            // Replace all wildcard dot notations with array notation, e.g. foo.* -> foo[*]
            .replace(regex = wildCardRegex, replacement = "[\${wildcard}]")
    }

    @Suppress("ReturnCount")
    private fun validateJsonPaths(inputJsonPath: String, validationJsonPath: String): Boolean {
        val inputPathSplit = inputJsonPath.split(".")
        val validationPathSplit = validationJsonPath.split(".")
        if (inputPathSplit.size != validationPathSplit.size) {
            return false
        }
        inputPathSplit.forEachIndexed { index, inputPathPart ->
            val validationPathPart = validationPathSplit[index]
            if (inputPathPart != validationPathPart && !areSameArray(inputPathPart, validationPathPart)) return false
        }
        return true
    }

    @Suppress("ReturnCount")
    private fun areSameArray(inputJsonPathArray: String, validationJsonPathArray: String): Boolean {
        val matchInputArray = arrayRegex.matchEntire(inputJsonPathArray) ?: return false
        val matchValidationArray = arrayWildcardRegex.matchEntire(validationJsonPathArray) ?: return false

        return matchInputArray.groups[1] == matchValidationArray.groups[1] && matchValidationArray.groups[2]?.value == "*"
    }

    companion object {
        // matches: arrayName[*]
        private val arrayWildcardRegex = Regex("""(\w+)\[(\*)]""")

        // matches: arrayName[index]
        private val arrayRegex = Regex("""(\w+)\[(\d+)]""")

        // matches: bracket notation
        private val bracketNotationRegex = """\[(?<quote>["'])(?<selector>\w+)\k<quote>]""".toRegex()

        // matches: dot notation wildcard
        private val wildCardRegex = """\.(?<wildcard>\*)""".toRegex()
    }
}
