package ch.admin.foitt.wallet.platform.environmentSetup.domain.repository

interface EnvironmentSetupRepository {
    val appVersionEnforcementUrl: String
    val attestationsServiceUrl: String
    val attestationsServiceTrustedDids: List<String>
    val trustRegistryMapping: Map<String, String>
    val trustRegistryTrustedDids: List<String>
    val baseTrustDomainRegex: Regex
    val betaIdRequestEnabled: Boolean
    val eIdRequestEnabled: Boolean
    val eIdMockMrzEnabled: Boolean
    val sidBackendUrl: String
}
