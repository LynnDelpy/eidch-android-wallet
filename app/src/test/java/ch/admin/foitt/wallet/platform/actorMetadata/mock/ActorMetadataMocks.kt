package ch.admin.foitt.wallet.platform.actorMetadata.mock

import ch.admin.foitt.wallet.platform.actorMetadata.domain.model.ActorDisplayData
import ch.admin.foitt.wallet.platform.actorMetadata.domain.model.ActorField
import ch.admin.foitt.wallet.platform.actorMetadata.domain.model.ActorType
import ch.admin.foitt.wallet.platform.nonCompliance.domain.model.NonComplianceData
import ch.admin.foitt.wallet.platform.nonCompliance.domain.model.NonComplianceReasonDisplay
import ch.admin.foitt.wallet.platform.nonCompliance.domain.model.NonComplianceState
import ch.admin.foitt.wallet.platform.trustRegistry.domain.model.TrustStatus
import ch.admin.foitt.wallet.platform.trustRegistry.domain.model.VcSchemaTrustStatus

object ActorMetadataMocks {
    val nonComplianceState = NonComplianceState.REPORTED
    val nonComplianceReasons = listOf(
        ActorField(locale = "en", value = "reason")
    )

    val nonComplianceData = NonComplianceData(
        state = NonComplianceState.REPORTED,
        reasonDisplays = listOf(
            NonComplianceReasonDisplay(
                locale = "en",
                reason = "reason"
            )
        )
    )

    val mockActorDisplayData01 = ActorDisplayData(
        name = listOf(
            ActorField(
                value = "test_name",
                locale = "de"
            )
        ),
        image = listOf(
            ActorField(
                value = "test_image",
                locale = "de"
            )
        ),
        preferredLanguage = "test_de",
        trustStatus = TrustStatus.TRUSTED,
        vcSchemaTrustStatus = VcSchemaTrustStatus.TRUSTED,
        actorType = ActorType.ISSUER,
        nonComplianceState = NonComplianceState.NOT_REPORTED,
        nonComplianceReason = null,
    )
}
