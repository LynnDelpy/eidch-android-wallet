package ch.admin.foitt.wallet.platform.credential.domain.usecase.implementation.mock

import ch.admin.foitt.wallet.platform.nonCompliance.domain.model.NonComplianceData
import ch.admin.foitt.wallet.platform.nonCompliance.domain.model.NonComplianceReasonDisplay
import ch.admin.foitt.wallet.platform.nonCompliance.domain.model.NonComplianceState

object MockNonComplianceData {
    val nonComplianceData = NonComplianceData(
        state = NonComplianceState.REPORTED,
        reasonDisplays = listOf(
            NonComplianceReasonDisplay(
                locale = "en",
                reason = "reason"
            )
        )
    )
}
