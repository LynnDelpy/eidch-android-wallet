package ch.admin.foitt.wallet.platform.trustRegistry.domain.model

data class TrustCheckResult(
    val actorTrustStatement: TrustStatement?,
    val vcSchemaTrustStatus: VcSchemaTrustStatus,
)
