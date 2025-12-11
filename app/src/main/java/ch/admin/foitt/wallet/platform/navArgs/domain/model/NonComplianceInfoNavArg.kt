package ch.admin.foitt.wallet.platform.navArgs.domain.model

import ch.admin.foitt.wallet.platform.nonCompliance.domain.model.NonComplianceReportReason

data class NonComplianceInfoNavArg(
    val nonComplianceReportReason: NonComplianceReportReason,
)
