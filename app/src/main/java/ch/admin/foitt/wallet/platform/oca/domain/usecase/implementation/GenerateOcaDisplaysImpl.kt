package ch.admin.foitt.wallet.platform.oca.domain.usecase.implementation

import ch.admin.foitt.wallet.platform.credential.domain.model.AnyClaimDisplay
import ch.admin.foitt.wallet.platform.credential.domain.model.AnyCredentialDisplay
import ch.admin.foitt.wallet.platform.credential.domain.model.toAnyCredentialDisplay
import ch.admin.foitt.wallet.platform.credential.domain.util.addFallbackLanguageIfNecessary
import ch.admin.foitt.wallet.platform.database.domain.model.Cluster
import ch.admin.foitt.wallet.platform.database.domain.model.ClusterDisplay
import ch.admin.foitt.wallet.platform.database.domain.model.CredentialClaim
import ch.admin.foitt.wallet.platform.database.domain.model.DisplayLanguage
import ch.admin.foitt.wallet.platform.oca.domain.model.AttributeType
import ch.admin.foitt.wallet.platform.oca.domain.model.ClaimValueData
import ch.admin.foitt.wallet.platform.oca.domain.model.GenerateOcaDisplaysError
import ch.admin.foitt.wallet.platform.oca.domain.model.GetRootCaptureBaseError
import ch.admin.foitt.wallet.platform.oca.domain.model.OcaBundle
import ch.admin.foitt.wallet.platform.oca.domain.model.OcaClaimData
import ch.admin.foitt.wallet.platform.oca.domain.model.OcaDisplays
import ch.admin.foitt.wallet.platform.oca.domain.model.getReferenceValue
import ch.admin.foitt.wallet.platform.oca.domain.model.overlays.CharacterEncoding
import ch.admin.foitt.wallet.platform.oca.domain.model.overlays.Standard
import ch.admin.foitt.wallet.platform.oca.domain.model.toGenerateOcaDisplaysError
import ch.admin.foitt.wallet.platform.oca.domain.usecase.GenerateOcaDisplays
import ch.admin.foitt.wallet.platform.oca.domain.usecase.GetRootCaptureBase
import ch.admin.foitt.wallet.platform.oca.domain.util.parseIso8601ToClaimValueData
import ch.admin.foitt.wallet.platform.oca.domain.util.parseUnixtimeToClaimValueData
import ch.admin.foitt.wallet.platform.ssi.domain.model.ImageType
import ch.admin.foitt.wallet.platform.ssi.domain.model.ValueType
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.coroutines.coroutineBinding
import com.github.michaelbull.result.mapError
import javax.inject.Inject

class GenerateOcaDisplaysImpl @Inject constructor(
    private val getRootCaptureBase: GetRootCaptureBase
) : GenerateOcaDisplays {

    override suspend fun invoke(
        credentialClaims: Map<String, String>,
        ocaBundle: OcaBundle,
    ): Result<OcaDisplays, GenerateOcaDisplaysError> = coroutineBinding {
        val rootCaptureBase = getRootCaptureBase(ocaBundle.captureBases)
            .mapError(GetRootCaptureBaseError::toGenerateOcaDisplaysError)
            .bind()

        val credentialDisplays = createLocalizedCredentialDisplays(ocaBundle)
        val clusters = createClusters(credentialClaims, ocaBundle, rootCaptureBase.digest)

        OcaDisplays(
            credentialDisplays = credentialDisplays,
            clusters = clusters,
        )
    }

    private fun createLocalizedCredentialDisplays(ocaBundle: OcaBundle): List<AnyCredentialDisplay> {
        return ocaBundle.ocaCredentialData.map { credentialData ->
            credentialData.toAnyCredentialDisplay()
        }
    }

    private fun createClusters(
        credentialClaims: Map<String, String>,
        ocaBundle: OcaBundle,
        rootCaptureBaseDigest: String,
    ): List<Cluster> {
        val clusters = mutableListOf<Cluster>()
        val rootCluster = createCluster(credentialClaims, ocaBundle, rootCaptureBaseDigest)
        if (rootCluster.claims.isEmpty() && rootCluster.childClusters.isNotEmpty()) {
            clusters.addAll(rootCluster.childClusters)
        } else {
            clusters.add(rootCluster)
        }

        val claimsWithoutOca = credentialClaims.filterKeys { key ->
            ocaBundle.getAttributeForJsonPath("$.$key") == null
        }.map { claim ->
            createClaim(claim.toPair(), null)
        }.toMap()

        if (claimsWithoutOca.isNotEmpty()) {
            val cluster = Cluster(claims = claimsWithoutOca)
            clusters.add(cluster)
        }

        return clusters
    }

    private fun createCluster(
        credentialClaims: Map<String, String>,
        ocaBundle: OcaBundle,
        captureBaseDigest: String,
        labels: Map<String, String> = emptyMap(),
        order: Int? = null,
    ): Cluster {
        val attributes = ocaBundle.getAttributes(captureBaseDigest)
        val referenceAttributes = attributes
            .filter { it.attributeType.getReferenceValue() != null }
            .toSet()
        val childClusters = createChildClusters(credentialClaims, ocaBundle, referenceAttributes)
        val otherAttributes = attributes - referenceAttributes
        val claims = createClaims(credentialClaims, otherAttributes)
        val clusterDisplays = createClusterDisplays(labels)
        return Cluster(claims = claims, clusterDisplays = clusterDisplays, childClusters = childClusters, order = order ?: -1)
    }

    private fun createChildClusters(
        credentialClaims: Map<String, String>,
        ocaBundle: OcaBundle,
        attributes: Collection<OcaClaimData>
    ): List<Cluster> {
        return attributes.mapNotNull { attribute ->
            if (attribute.attributeType is AttributeType.Reference) {
                val referenceDigest = attribute.attributeType.getReferenceValue() ?: return@mapNotNull null
                createCluster(credentialClaims, ocaBundle, referenceDigest, attribute.labels, attribute.order)
            } else {
                null
            }
        }
    }

    private fun createClaims(
        credentialClaims: Map<String, String>,
        attributes: List<OcaClaimData>
    ): Map<CredentialClaim, List<AnyClaimDisplay>> {
        return attributes.mapNotNull { attribute ->
            val claim = credentialClaims.entries.find { (key, _) ->
                "$.$key" in attribute.dataSources.values
            }?.toPair()

            if (claim == null) {
                null
            } else {
                createClaim(claim, attribute)
            }
        }.toMap()
    }

    private fun createClaim(
        credentialClaim: Pair<String, String>,
        attribute: OcaClaimData?
    ): Pair<CredentialClaim, List<AnyClaimDisplay>> {
        val valueData = getValueData(value = credentialClaim.second, claim = attribute)

        val claim = CredentialClaim(
            clusterId = UNKNOWN_CLUSTER_DISPLAY_ID,
            key = credentialClaim.first,
            value = valueData.value,
            valueType = valueData.valueType,
            valueDisplayInfo = valueData.valueDisplayInfo,
            order = attribute?.order ?: -1
        )
        val claimDisplays = createClaimDisplays(ocaClaim = attribute, defaultClaimKey = credentialClaim.first)

        return claim to claimDisplays
    }

    private fun getValueData(value: String, claim: OcaClaimData?): ClaimValueData {
        return when {
            // Unixtime DateTime
            claim?.attributeType == AttributeType.DateTime && claim.standard == Standard.UnixTime -> {
                parseUnixtimeToClaimValueData(input = value)
            }

            // ISO 8601 DateTime
            // Claims with AttributeType.DateTime and no Standard are treated as ISO8601, too
            claim?.attributeType == AttributeType.DateTime && (claim.standard == Standard.Iso8601 || claim.standard == null) -> {
                parseIso8601ToClaimValueData(input = value)
            }

            // Image Data Uri
            claim?.attributeType == AttributeType.Text && claim.standard == Standard.DataUrl && ImageType.entries.any {
                value.startsWith("data:${it.mimeType};base64,")
            } -> ClaimValueData(
                value = value.split(";base64,").last(),
                valueType = ImageType.entries.first { value.startsWith("data:${it.mimeType};base64,") }.mimeType,
                valueDisplayInfo = null
            )

            // Binary Image
            claim?.attributeType == AttributeType.Binary && claim.characterEncoding == CharacterEncoding.Base64 &&
                ImageType.entries.any { it.mimeType == claim.format } -> ClaimValueData(
                value = value,
                valueType = ImageType.entries.first { it.mimeType == claim.format }.mimeType,
                valueDisplayInfo = null
            )

            else -> ClaimValueData(
                value = value,
                valueType = getValueType(claim = claim),
                valueDisplayInfo = null
            )
        }
    }

    private fun getValueType(claim: OcaClaimData?): String {
        if (claim == null) {
            // default to string
            return ValueType.STRING.value
        }

        return when (claim.attributeType) {
            AttributeType.Boolean -> ValueType.BOOLEAN.value
            AttributeType.DateTime -> ValueType.DATETIME.value
            AttributeType.Numeric -> ValueType.NUMERIC.value
            AttributeType.Text, AttributeType.Binary, is AttributeType.Reference, is AttributeType.Array -> ValueType.STRING.value
        }
    }

    private fun createClaimDisplays(
        ocaClaim: OcaClaimData?,
        defaultClaimKey: String
    ) = ocaClaim?.labels?.map { (locale, label) ->
        AnyClaimDisplay(
            name = label,
            locale = locale
        )
    }.addFallbackLanguageIfNecessary {
        AnyClaimDisplay(name = defaultClaimKey, locale = DisplayLanguage.FALLBACK)
    }

    private fun createClusterDisplays(labels: Map<String, String>) = labels.map { (locale, label) ->
        ClusterDisplay(name = label, locale = locale)
    }

    companion object {
        private const val UNKNOWN_CLUSTER_DISPLAY_ID = -1L
    }
}
