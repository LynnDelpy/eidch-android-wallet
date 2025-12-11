package ch.admin.foitt.wallet.platform.database.data.dao

import kotlinx.coroutines.flow.StateFlow

interface DaoProvider {
    val credentialDaoFlow: StateFlow<CredentialDao?>
    val verifiableCredentialDaoFlow: StateFlow<VerifiableCredentialDao?>
    val verifiableCredentialWithDisplaysAndClustersDaoFlow: StateFlow<VerifiableCredentialWithDisplaysAndClustersDao?>
    val credentialWithKeyBindingDaoFlow: StateFlow<CredentialWithKeyBindingDao?>
    val deferredCredentialDaoFlow: StateFlow<DeferredCredentialDao?>
    val credentialDisplayDaoFlow: StateFlow<CredentialDisplayDao?>
    val credentialClaimDaoFlow: StateFlow<CredentialClaimDao?>
    val credentialClaimDisplayDaoFlow: StateFlow<CredentialClaimDisplayDao?>
    val credentialIssuerDisplayDaoFlow: StateFlow<CredentialIssuerDisplayDao?>
    val credentialClaimClusterEntityDao: StateFlow<CredentialClaimClusterEntityDao?>
    val credentialClaimClusterDisplayEntityDao: StateFlow<CredentialClaimClusterDisplayEntityDao?>
    val credentialKeyBindingEntityDaoFlow: StateFlow<CredentialKeyBindingEntityDao?>
    val credentialActivityEntityDao: StateFlow<CredentialActivityEntityDao?>
    val activityClaimEntityDao: StateFlow<ActivityClaimEntityDao?>
    val activityActorDisplayEntityDao: StateFlow<ActivityActorDisplayEntityDao?>
    val activityWithDetailsDao: StateFlow<ActivityWithDetailsDao?>
    val activityWithDisplaysDao: StateFlow<ActivityWithDisplaysDao?>
    val imageEntityDao: StateFlow<ImageEntityDao?>
    val eIdRequestCaseDaoFlow: StateFlow<EIdRequestCaseDao?>
    val eIdRequestStateDaoFlow: StateFlow<EIdRequestStateDao?>
    val eIdRequestCaseWithStateDaoFlow: StateFlow<EIdRequestCaseWithStateDao?>
    val eIdRequestFileDaoFlow: StateFlow<EIdRequestFileDao?>
    val rawCredentialDataDao: StateFlow<RawCredentialDataDao?>
    val clientAttestationDaoFlow: StateFlow<ClientAttestationDao?>
}
