package ch.admin.foitt.wallet.platform.nonCompliance.domain.usecase.implementation.mock

import ch.admin.foitt.wallet.platform.nonCompliance.domain.model.NonComplianceReasonDisplay
import ch.admin.foitt.wallet.platform.nonCompliance.domain.model.NonComplianceResponse
import ch.admin.foitt.wallet.platform.nonCompliance.domain.model.NonCompliantActor

object NonComplianceMocks {
    const val REPORTED_ACTOR_DID = "reported actor did"
    const val NON_REPORTED_ACTOR_DID = "non reported actor did"
    const val NON_COMPLIANCE_REASON_EN = "reason en"
    val nonComplianceReasonDisplayEn = NonComplianceReasonDisplay(locale = "en", NON_COMPLIANCE_REASON_EN)

    val nonComplianceReasonDisplays = listOf(
        nonComplianceReasonDisplayEn,
        NonComplianceReasonDisplay(locale = "de", "reason de"),
        NonComplianceReasonDisplay(locale = "fr", "reason fr"),
    )

    val nonComplianceResponseSuccess = NonComplianceResponse(
        nonCompliantActors = listOf(
            NonCompliantActor(
                did = REPORTED_ACTOR_DID,
                flaggedAsNonCompliantAt = "01.02.2025",
                reason = mapOf(
                    "en" to "reason en",
                    "de" to "reason de",
                    "fr" to "reason fr",
                )
            )
        )
    )
}
