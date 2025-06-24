package ch.admin.foitt.wallet.feature.credentialOffer

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.GrantPermissionRule
import androidx.test.uiautomator.UiDevice
import ch.admin.foitt.wallet.app.MainActivity
import ch.admin.foitt.wallet.feature.credentialOffer.domain.usecase.CredentialOfferScreen
import ch.admin.foitt.wallet.feature.credentialOffer.domain.usecase.CredentialOfferWrongDataScreen
import ch.admin.foitt.wallet.feature.credentialOffer.domain.usecase.DeclineOfferScreen
import ch.admin.foitt.wallet.feature.home.screens.HomeScreen
import ch.admin.foitt.wallet.feature.qrscan.screens.QrScannerScreen
import ch.admin.foitt.wallet.platform.NavigationUtils
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class CredentialOfferScreenTest {

    @get:Rule(order = 0)
    var hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    var activityRule = createAndroidComposeRule<MainActivity>()

    @get:Rule(order = 2)
    val grantPermissionRule: GrantPermissionRule = GrantPermissionRule.grant(
        android.Manifest.permission.CAMERA
    )

    private val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())

    @Before
    fun init() {
        hiltRule.inject()
    }

    @Test
    fun test_credential_offer_not_onboarded_until_offer() = runTest {
        val navigation = NavigationUtils()
        navigation.goTroughOnboarding(activityRule)

        val homeScreen = HomeScreen(activityRule)
        homeScreen.isDisplayed()
        homeScreen.openScanner()

        val scannerScreen = QrScannerScreen(activityRule)
        scannerScreen.isDisplayed()

        val credentialOfferScreen = CredentialOfferScreen(activityRule)
        credentialOfferScreen.isDisplayed()
        credentialOfferScreen.acceptCredential()
        homeScreen.credentialListIsDisplayed()
    }

    @Test
    fun test_credential_offer_decline_offer() = runTest {
        val navigation = NavigationUtils()
        navigation.goTroughOnboarding(activityRule)

        val homeScreen = HomeScreen(activityRule)
        homeScreen.isDisplayed()
        homeScreen.openScanner()

        val scannerScreen = QrScannerScreen(activityRule)
        scannerScreen.isDisplayed()

        val credentialOfferScreen = CredentialOfferScreen(activityRule)
        credentialOfferScreen.isDisplayed()
        credentialOfferScreen.declineCredential()
        val declineOfferScreen = DeclineOfferScreen(activityRule)
        declineOfferScreen.isDisplayed()
        declineOfferScreen.confirmDecline()
        homeScreen.isDisplayed()
    }

    @Test
    fun test_credential_offer_sticky_button_accept() = runTest{
        val navigation = NavigationUtils()
        navigation.goTroughOnboarding(activityRule)

        val homeScreen = HomeScreen(activityRule)
        homeScreen.isDisplayed()
        homeScreen.openScanner()

        val scannerScreen = QrScannerScreen(activityRule)
        scannerScreen.isDisplayed()

        val credentialOfferScreen = CredentialOfferScreen(activityRule)
        credentialOfferScreen.isDisplayed()
        credentialOfferScreen.acceptStickyCredential()
    }

    @Test
    fun test_credential_offer_sticky_button_decline() = runTest{
        val navigation = NavigationUtils()
        navigation.goTroughOnboarding(activityRule)

        val homeScreen = HomeScreen(activityRule)
        homeScreen.isDisplayed()
        homeScreen.openScanner()

        val scannerScreen = QrScannerScreen(activityRule)
        scannerScreen.isDisplayed()

        val credentialOfferScreen = CredentialOfferScreen(activityRule)
        credentialOfferScreen.isDisplayed()
        credentialOfferScreen.declineStickyCredential()
        val declineOfferScreen = DeclineOfferScreen(activityRule)
        declineOfferScreen.isDisplayed()
        declineOfferScreen.confirmDecline()
        homeScreen.isDisplayed()
    }

    @Test
    fun test_credential_offer_trust_statement_displayed() = runTest{
        val navigation = NavigationUtils()
        navigation.goTroughOnboarding(activityRule)

        val homeScreen = HomeScreen(activityRule)
        homeScreen.isDisplayed()
        homeScreen.openScanner()

        val scannerScreen = QrScannerScreen(activityRule)
        scannerScreen.isDisplayed()

        val credentialOfferScreen = CredentialOfferScreen(activityRule)
        credentialOfferScreen.isDisplayed()
        credentialOfferScreen.isVerified()
        credentialOfferScreen.issuerDisplayed()
    }

    @Test
    fun test_credential_offer_repeat_decline() = runTest{
        val navigation = NavigationUtils()
        navigation.goTroughOnboarding(activityRule)

        val homeScreen = HomeScreen(activityRule)
        homeScreen.isDisplayed()
        homeScreen.openScanner()

        val scannerScreen = QrScannerScreen(activityRule)
        scannerScreen.isDisplayed()

        val credentialOfferScreen = CredentialOfferScreen(activityRule)
        credentialOfferScreen.isDisplayed()
        val declineOfferScreen = DeclineOfferScreen(activityRule)
        repeat(10){
            credentialOfferScreen.declineCredential()
            declineOfferScreen.isDisplayed()
            declineOfferScreen.cancelDecline()
            credentialOfferScreen.isDisplayed()
        }
        credentialOfferScreen.declineCredential()
        declineOfferScreen.isDisplayed()
        declineOfferScreen.confirmDecline()
        homeScreen.isDisplayed()
    }

    @Test
    fun test_credential_offer_repeat_decline_then_accept() = runTest{
        val navigation = NavigationUtils()
        navigation.goTroughOnboarding(activityRule)

        val homeScreen = HomeScreen(activityRule)
        homeScreen.isDisplayed()
        homeScreen.openScanner()

        val scannerScreen = QrScannerScreen(activityRule)
        scannerScreen.isDisplayed()

        val credentialOfferScreen = CredentialOfferScreen(activityRule)
        credentialOfferScreen.isDisplayed()
        val declineOfferScreen = DeclineOfferScreen(activityRule)
        repeat(10){
            credentialOfferScreen.declineCredential()
            declineOfferScreen.isDisplayed()
            declineOfferScreen.cancelDecline()
            credentialOfferScreen.isDisplayed()
        }
        credentialOfferScreen.acceptCredential()
        homeScreen.credentialListIsDisplayed()
    }

    @Test
    fun test_credential_offer_details_are_displayed() = runTest{
        val navigation = NavigationUtils()
        navigation.goTroughOnboarding(activityRule)

        val homeScreen = HomeScreen(activityRule)
        homeScreen.isDisplayed()
        homeScreen.openScanner()

        val scannerScreen = QrScannerScreen(activityRule)
        scannerScreen.isDisplayed()

        val credentialOfferScreen = CredentialOfferScreen(activityRule)
        credentialOfferScreen.isDisplayed()
        credentialOfferScreen.hasDetails()
    }

    @Test
    fun test_credential_offer_report_wrong_Data() = runTest{
        val navigation = NavigationUtils()
        navigation.goTroughOnboarding(activityRule)

        val homeScreen = HomeScreen(activityRule)
        homeScreen.isDisplayed()
        homeScreen.openScanner()

        val scannerScreen = QrScannerScreen(activityRule)
        scannerScreen.isDisplayed()

        val credentialOfferScreen = CredentialOfferScreen(activityRule)
        credentialOfferScreen.isDisplayed()
        credentialOfferScreen.reportWrongData()
        val wrongDataScreen = CredentialOfferWrongDataScreen(activityRule)
        wrongDataScreen.isDisplayed()
        wrongDataScreen.clickBack()
        credentialOfferScreen.scrollUp()
        credentialOfferScreen.isDisplayed()
    }
}
