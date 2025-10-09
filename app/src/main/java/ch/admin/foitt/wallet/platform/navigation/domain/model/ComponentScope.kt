package ch.admin.foitt.wallet.platform.navigation.domain.model

import ch.admin.foitt.walletcomposedestinations.destinations.BiometricLoginScreenDestination
import ch.admin.foitt.walletcomposedestinations.destinations.CredentialOfferDeclinedScreenDestination
import ch.admin.foitt.walletcomposedestinations.destinations.CredentialOfferScreenDestination
import ch.admin.foitt.walletcomposedestinations.destinations.DeclineCredentialOfferScreenDestination
import ch.admin.foitt.walletcomposedestinations.destinations.Destination
import ch.admin.foitt.walletcomposedestinations.destinations.EIdDocumentRecordingScreenDestination
import ch.admin.foitt.walletcomposedestinations.destinations.EIdDocumentSelectionScreenDestination
import ch.admin.foitt.walletcomposedestinations.destinations.EIdGuardianConsentResultScreenDestination
import ch.admin.foitt.walletcomposedestinations.destinations.EIdGuardianConsentScreenDestination
import ch.admin.foitt.walletcomposedestinations.destinations.EIdGuardianSelectionScreenDestination
import ch.admin.foitt.walletcomposedestinations.destinations.EIdGuardianshipScreenDestination
import ch.admin.foitt.walletcomposedestinations.destinations.EIdProcessDataScreenDestination
import ch.admin.foitt.walletcomposedestinations.destinations.EIdStartAutoVerificationScreenDestination
import ch.admin.foitt.walletcomposedestinations.destinations.EIdStartSelfieVideoScreenDestination
import ch.admin.foitt.walletcomposedestinations.destinations.MrzChooserScreenDestination
import ch.admin.foitt.walletcomposedestinations.destinations.MrzSubmissionScreenDestination
import ch.admin.foitt.walletcomposedestinations.destinations.OnboardingSuccessScreenDestination
import ch.admin.foitt.walletcomposedestinations.destinations.PassphraseLoginScreenDestination
import ch.admin.foitt.walletcomposedestinations.destinations.PresentationCredentialListScreenDestination
import ch.admin.foitt.walletcomposedestinations.destinations.PresentationDeclinedScreenDestination
import ch.admin.foitt.walletcomposedestinations.destinations.PresentationFailureScreenDestination
import ch.admin.foitt.walletcomposedestinations.destinations.PresentationInvalidCredentialErrorScreenDestination
import ch.admin.foitt.walletcomposedestinations.destinations.PresentationRequestScreenDestination
import ch.admin.foitt.walletcomposedestinations.destinations.PresentationSuccessScreenDestination
import ch.admin.foitt.walletcomposedestinations.destinations.PresentationValidationErrorScreenDestination
import ch.admin.foitt.walletcomposedestinations.destinations.PresentationVerificationErrorScreenDestination
import ch.admin.foitt.walletcomposedestinations.destinations.QrScannerScreenDestination
import ch.admin.foitt.walletcomposedestinations.destinations.SDKFaceScannerScreenDestination
import ch.admin.foitt.walletcomposedestinations.destinations.SDKScannerScreenDestination

sealed interface ComponentScope {
    val destinations: Set<Destination>

    data object CredentialIssuer : ComponentScope {
        override val destinations = setOf(
            OnboardingSuccessScreenDestination,
            QrScannerScreenDestination,
            CredentialOfferScreenDestination,
            CredentialOfferDeclinedScreenDestination,
            DeclineCredentialOfferScreenDestination,
            BiometricLoginScreenDestination,
            PassphraseLoginScreenDestination
        )
    }

    data object Verifier : ComponentScope {
        override val destinations = setOf(
            PresentationCredentialListScreenDestination,
            PresentationDeclinedScreenDestination,
            PresentationFailureScreenDestination,
            PresentationInvalidCredentialErrorScreenDestination,
            PresentationRequestScreenDestination,
            PresentationSuccessScreenDestination,
            PresentationValidationErrorScreenDestination,
            PresentationVerificationErrorScreenDestination,
            BiometricLoginScreenDestination,
            PassphraseLoginScreenDestination
        )
    }

    data object EidApplicationProcess : ComponentScope {
        override val destinations = setOf(
            EIdGuardianshipScreenDestination,
            EIdDocumentSelectionScreenDestination,
            MrzChooserScreenDestination,
            SDKScannerScreenDestination,
            MrzSubmissionScreenDestination,
        )
    }

    /**
     * [ComponentScope] for keeping the data related to the current SId case in memory.
     *
     * It has to contain all screens past the [MrzSubmissionScreenDestination]
     */
    data object EidCurrentSIdCase : ComponentScope {
        override val destinations = setOf(
            MrzSubmissionScreenDestination,
            EIdGuardianSelectionScreenDestination,
            EIdGuardianConsentScreenDestination,
            EIdGuardianConsentResultScreenDestination,
        )
    }

    /**
     * [ComponentScope] for keeping the result of the document scan in memory.
     *
     * Should be kept as small as possible, as the scan files are quite heavy.
     */
    data object EidDocumentScan : ComponentScope {
        override val destinations = setOf(
            MrzChooserScreenDestination,
            SDKScannerScreenDestination,
            MrzSubmissionScreenDestination,
        )
    }

    /**
     * [ComponentScope] for keeping the theAutoVerificationResponse in memory.
     *
     */
    data object EidOnlineSession : ComponentScope {
        override val destinations = setOf(
            EIdStartAutoVerificationScreenDestination,
            EIdDocumentRecordingScreenDestination,
            EIdStartSelfieVideoScreenDestination,
            SDKFaceScannerScreenDestination,
            EIdProcessDataScreenDestination
        )
    }
}
