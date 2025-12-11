package ch.admin.foitt.wallet.platform.activityList.domain.usecase.implementation.mock

import ch.admin.foitt.wallet.platform.activityList.domain.model.ActivityDisplayData
import ch.admin.foitt.wallet.platform.activityList.domain.model.ActivityType
import ch.admin.foitt.wallet.platform.credential.domain.model.CredentialDisplayData
import ch.admin.foitt.wallet.platform.database.domain.model.ActivityActorDisplayEntity
import ch.admin.foitt.wallet.platform.database.domain.model.ActivityActorDisplayWithImage
import ch.admin.foitt.wallet.platform.database.domain.model.ActivityClaimEntity
import ch.admin.foitt.wallet.platform.database.domain.model.ActivityWithActorDisplays
import ch.admin.foitt.wallet.platform.database.domain.model.ActivityWithDetails
import ch.admin.foitt.wallet.platform.database.domain.model.ClusterWithDisplaysAndClaims
import ch.admin.foitt.wallet.platform.database.domain.model.CredentialActivityEntity
import ch.admin.foitt.wallet.platform.database.domain.model.CredentialClaim
import ch.admin.foitt.wallet.platform.database.domain.model.CredentialClaimClusterEntity
import ch.admin.foitt.wallet.platform.database.domain.model.CredentialClaimWithDisplays
import ch.admin.foitt.wallet.platform.database.domain.model.CredentialClusterWithDisplays
import ch.admin.foitt.wallet.platform.database.domain.model.CredentialDisplay
import ch.admin.foitt.wallet.platform.database.domain.model.VerifiableCredentialEntity
import ch.admin.foitt.wallet.platform.database.domain.model.VerifiableCredentialWithDisplaysAndClusters
import ch.admin.foitt.wallet.platform.locale.LocaleCompat
import ch.admin.foitt.wallet.platform.ssi.domain.model.CredentialClaimCluster
import ch.admin.foitt.wallet.platform.ssi.domain.model.CredentialClaimText
import ch.admin.foitt.wallet.platform.trustRegistry.domain.model.TrustStatus
import ch.admin.foitt.wallet.platform.trustRegistry.domain.model.VcSchemaTrustStatus
import io.mockk.mockk

object ActivityListMocks {
    const val CREDENTIAL_ID = 1L
    const val ACTIVITY_ID = 1L
    val locale = LocaleCompat.of("de", "CH")

    val activity = CredentialActivityEntity(
        id = 1,
        type = ActivityType.ISSUANCE,
        credentialId = CREDENTIAL_ID,
        actorTrust = TrustStatus.TRUSTED,
        vcSchemaTrust = VcSchemaTrustStatus.TRUSTED,
        nonComplianceData = null,
        createdAt = 1759837408,
    )

    val activityClaim = ActivityClaimEntity(
        id = 1,
        activityId = 1,
        claimId = 1,
    )

    val actorDisplay1 = ActivityActorDisplayWithImage(
        actorDisplay = ActivityActorDisplayEntity(
            id = 1,
            activityId = 1,
            imageHash = null,
            name = "issuerName de",
            locale = "de-CH",
        ),
        image = null
    )

    val actorDisplay2 = ActivityActorDisplayWithImage(
        actorDisplay = ActivityActorDisplayEntity(
            id = 2,
            activityId = 1,
            imageHash = null,
            name = "issuerName fr",
            locale = "fr-CH"
        ),
        image = null,
    )

    val activityWithActorDisplays = ActivityWithActorDisplays(
        activity = activity,
        actorDisplays = listOf(actorDisplay1, actorDisplay2),
    )

    val activityWithDetails = ActivityWithDetails(
        activity = activity,
        actorDisplays = listOf(actorDisplay1, actorDisplay2),
        claims = listOf(activityClaim),
    )

    val activityDisplayData = ActivityDisplayData(
        id = activity.id,
        activityType = activity.type,
        date = "07.10.2025 13:43",
        localizedActorName = actorDisplay1.actorDisplay.name
    )

    val mockVerifiableCredential = mockk<VerifiableCredentialEntity>()
    val mockCredentialDisplays = listOf(mockk<CredentialDisplay>())
    val claimWithDisplays1 = CredentialClaimWithDisplays(
        claim = CredentialClaim(
            id = 1,
            clusterId = 1,
            key = "claim 1",
            value = "claim 1 value",
            valueType = "string",
            valueDisplayInfo = null,
            order = 1,
            isSensitive = false,
        ),
        displays = emptyList(),
    )
    val claimWithDisplays2 = CredentialClaimWithDisplays(
        claim = CredentialClaim(
            id = 2,
            clusterId = 1,
            key = "claim 2",
            value = "claim 2 value",
            valueType = "string",
            valueDisplayInfo = null,
            order = 2,
            isSensitive = false,
        ),
        displays = emptyList(),
    )

    val claimsWithDisplays = listOf(claimWithDisplays1, claimWithDisplays2)
    val clusterWithDisplays = CredentialClusterWithDisplays(
        cluster = CredentialClaimClusterEntity(
            id = 1,
            verifiableCredentialId = 1,
            parentClusterId = null,
            order = 1,
        ),
        displays = emptyList()
    )
    val clusters = listOf(
        ClusterWithDisplaysAndClaims(
            clusterWithDisplays = clusterWithDisplays,
            claimsWithDisplays = claimsWithDisplays,
        )
    )

    val filteredClusters = listOf(
        ClusterWithDisplaysAndClaims(
            clusterWithDisplays = clusterWithDisplays,
            claimsWithDisplays = listOf(claimWithDisplays1)
        )
    )

    val verifiableCredentialWithDisplaysAndClusters = VerifiableCredentialWithDisplaysAndClusters(
        verifiableCredential = mockVerifiableCredential,
        credentialDisplays = mockCredentialDisplays,
        clusters = clusters,
    )

    val mockCredentialDisplayData = mockk<CredentialDisplayData>()

    val credentialClaimCluster = CredentialClaimCluster(
        id = 1,
        order = 1,
        localizedLabel = "cluster 1 label",
        isSensitive = false,
        parentId = null,
        items = mutableListOf(
            CredentialClaimText(
                id = 2,
                localizedLabel = "claim 2",
                order = 2,
                isSensitive = false,
                value = "claim 2 value",
            )
        ),
        numberOfNonClusterChildren = 1,
    )
}
