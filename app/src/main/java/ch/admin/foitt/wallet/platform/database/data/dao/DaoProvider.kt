package ch.admin.foitt.wallet.platform.database.data.dao

import kotlinx.coroutines.flow.StateFlow

interface DaoProvider {
    val credentialDaoFlow: StateFlow<CredentialDao?>
    val credentialDisplayDaoFlow: StateFlow<CredentialDisplayDao?>
    val credentialClaimDaoFlow: StateFlow<CredentialClaimDao?>
    val credentialClaimDisplayDaoFlow: StateFlow<CredentialClaimDisplayDao?>
    val credentialIssuerDisplayDaoFlow: StateFlow<CredentialIssuerDisplayDao?>
    val credentialWithDisplaysAndClustersDaoFlow: StateFlow<CredentialWithDisplaysAndClustersDao?>
    val credentialClaimClusterEntityDao: StateFlow<CredentialClaimClusterEntityDao?>
    val credentialClaimClusterDisplayEntityDao: StateFlow<CredentialClaimClusterDisplayEntityDao?>
    val credentialKeyBindingEntityDaoFlow: StateFlow<CredentialKeyBindingEntityDao?>
    val credentialWithKeyBindingDaoFlow: StateFlow<CredentialWithKeyBindingDao?>
    val eIdRequestCaseDaoFlow: StateFlow<EIdRequestCaseDao?>
    val eIdRequestStateDaoFlow: StateFlow<EIdRequestStateDao?>
    val eIdRequestCaseWithStateDaoFlow: StateFlow<EIdRequestCaseWithStateDao?>
    val eIdRequestFileDaoFlow: StateFlow<EIdRequestFileDao?>
    val rawCredentialDataDao: StateFlow<RawCredentialDataDao?>
    val clientAttestationDaoFlow: StateFlow<ClientAttestationDao?>
}
