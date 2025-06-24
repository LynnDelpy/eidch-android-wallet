package ch.admin.foitt.wallet.platform

import androidx.compose.ui.test.ComposeTimeoutException
import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.printToLog
import ch.admin.foitt.wallet.feature.home.screens.HomeScreen
import ch.admin.foitt.wallet.feature.onboarding.screens.OnboardingBiometricScreen
import ch.admin.foitt.wallet.feature.onboarding.screens.OnboardingIntroScreen
import ch.admin.foitt.wallet.feature.onboarding.screens.OnboardingLocalDataScreen
import ch.admin.foitt.wallet.feature.onboarding.screens.OnboardingPasswordExplanationScreen
import ch.admin.foitt.wallet.feature.onboarding.screens.OnboardingPresentScreen
import ch.admin.foitt.wallet.feature.onboarding.screens.OnboardingSuccessScreen
import ch.admin.foitt.wallet.feature.onboarding.screens.OnboardingUserPrivacyScreen
import ch.admin.foitt.wallet.feature.onboarding.screens.PasswordConfirmationScreen
import ch.admin.foitt.wallet.feature.onboarding.screens.PasswordEntryScreen
import ch.admin.foitt.wallet.platform.eIdApplicationProcess.screens.EIdRequestIntroScreen

class NavigationUtils {

    public fun goTroughOnboarding(activityRule: ComposeContentTestRule) {
        val introScreen = OnboardingIntroScreen(activityRule)
        introScreen.isDisplayed()
        introScreen.nextScreen()
        val localDataScreen = OnboardingLocalDataScreen(activityRule)
        localDataScreen.isDisplayed()
        localDataScreen.nextScreen()
        val presentScreen = OnboardingPresentScreen(activityRule)
        presentScreen.isDisplayed()
        presentScreen.nextScreen()
        val userPrivacyScreen = OnboardingUserPrivacyScreen(activityRule)
        userPrivacyScreen.isDisplayed()
        userPrivacyScreen.accept()
        val pinExplanationScreen = OnboardingPasswordExplanationScreen(activityRule)
        pinExplanationScreen.isDisplayed()
        pinExplanationScreen.nextScreen()
        val passphrase = "123456"
        val passwordEntryScreen = PasswordEntryScreen(activityRule)
        passwordEntryScreen.enterPin(passphrase)
        passwordEntryScreen.nextScreen()
        val passwordConfirmationScreen = PasswordConfirmationScreen(activityRule)
        passwordConfirmationScreen.enterPin(passphrase)
        passwordEntryScreen.nextScreen()
        val biometricScreen = OnboardingBiometricScreen(activityRule)
        biometricScreen.isDisplayed()
        biometricScreen.noBiometric()

        val successScreen = OnboardingSuccessScreen(activityRule)
        successScreen.isDisplayed()
        successScreen.nextScreen()

        val eidIntroScreen = EIdRequestIntroScreen(activityRule)
        eidIntroScreen.isDisplayed()
        eidIntroScreen.nextScreen()

        val homeScreen = HomeScreen(activityRule)
        homeScreen.isDisplayed()
    }
}
