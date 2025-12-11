package ch.admin.foitt.wallet.platform.activityList.domain.model

import ch.admin.foitt.wallet.platform.credential.domain.model.CredentialDisplayData
import ch.admin.foitt.wallet.platform.ssi.domain.model.CredentialClaimCluster

data class ActivityDetail(
    val activity: ActivityDisplayData,
    val credential: CredentialDisplayData,
    val claims: List<CredentialClaimCluster>,
)
