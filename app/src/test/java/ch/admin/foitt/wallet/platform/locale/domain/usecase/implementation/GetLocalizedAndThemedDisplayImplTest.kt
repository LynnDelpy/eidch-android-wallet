package ch.admin.foitt.wallet.platform.locale.domain.usecase.implementation

import ch.admin.foitt.wallet.platform.locale.domain.usecase.GetCurrentAppLocale
import ch.admin.foitt.wallet.platform.locale.domain.usecase.GetLocalizedAndThemedDisplay
import ch.admin.foitt.wallet.platform.locale.domain.usecase.implementation.LocalizedAndThemedDisplayTestData.noSupportedLocaleAndNoFallback
import ch.admin.foitt.wallet.platform.locale.domain.usecase.implementation.LocalizedAndThemedDisplayTestData.withFallbackAndNoSupportedLocale
import ch.admin.foitt.wallet.platform.locale.domain.usecase.implementation.LocalizedAndThemedDisplayTestData.withSupportedLocaleNoCountryCode
import ch.admin.foitt.wallet.platform.locale.domain.usecase.implementation.LocalizedAndThemedDisplayTestData.withSupportedLocaleWithCountryCode
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import io.mockk.unmockkAll
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.Locale

class GetLocalizedAndThemedDisplayImplTest {
    @MockK
    private lateinit var mockGetCurrentAppLocale: GetCurrentAppLocale

    private lateinit var getLocalizedAndThemedDisplay: GetLocalizedAndThemedDisplay

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        getLocalizedAndThemedDisplay = GetLocalizedAndThemedDisplayImpl(
            getCurrentAppLocale = mockGetCurrentAppLocale,
        )
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `App locale with supported country code returns CredentialDisplay in requested theme with best matching locale`() = runTest {
        coEvery { mockGetCurrentAppLocale() } returns Locale("de", "CH")

        val displayWithSupportedLocaleWithCountryCode = getLocalizedAndThemedDisplay(
            credentialDisplays = withSupportedLocaleWithCountryCode,
            preferredTheme = "dark"
        )
        assertEquals("de-CH", displayWithSupportedLocaleWithCountryCode?.locale)
        assertEquals("dark", displayWithSupportedLocaleWithCountryCode?.theme)

        val displayWithSupportedLocaleNoCountryCode = getLocalizedAndThemedDisplay(
            credentialDisplays = withSupportedLocaleNoCountryCode,
            preferredTheme = "dark"
        )
        assertEquals("en", displayWithSupportedLocaleNoCountryCode?.locale)
        assertEquals("dark", displayWithSupportedLocaleNoCountryCode?.theme)

        val displayNoSupportedLocaleAndNoFallback = getLocalizedAndThemedDisplay(
            credentialDisplays = noSupportedLocaleAndNoFallback,
            preferredTheme = "dark"
        )
        assertEquals("xx", displayNoSupportedLocaleAndNoFallback?.locale)
        assertEquals("dark", displayNoSupportedLocaleAndNoFallback?.theme)

        val displayWithFallbackAndNoSupportedLocale = getLocalizedAndThemedDisplay(
            credentialDisplays = withFallbackAndNoSupportedLocale,
            preferredTheme = "dark"
        )
        assertEquals("fallback", displayWithFallbackAndNoSupportedLocale?.locale)
        assertEquals("dark", displayWithFallbackAndNoSupportedLocale?.theme)
    }

    @Test
    fun `App locale with supported country code returns CredentialDisplay in not requested theme with best matching locale`() = runTest {
        coEvery { mockGetCurrentAppLocale() } returns Locale("de", "CH")
        val displayWithSupportedLocaleWithCountryCode = getLocalizedAndThemedDisplay(
            credentialDisplays = withSupportedLocaleWithCountryCode,
            preferredTheme = "light"
        )
        assertEquals("de-CH", displayWithSupportedLocaleWithCountryCode?.locale)
        assertEquals("dark", displayWithSupportedLocaleWithCountryCode?.theme)

        val displayWithSupportedLocaleNoCountryCode = getLocalizedAndThemedDisplay(
            credentialDisplays = withSupportedLocaleNoCountryCode,
            preferredTheme = "light"
        )
        assertEquals("en", displayWithSupportedLocaleNoCountryCode?.locale)
        assertEquals("dark", displayWithSupportedLocaleNoCountryCode?.theme)

        val displayNoSupportedLocaleAndNoFallback = getLocalizedAndThemedDisplay(
            credentialDisplays = noSupportedLocaleAndNoFallback,
            preferredTheme = "light"
        )
        assertEquals("xx", displayNoSupportedLocaleAndNoFallback?.locale)
        assertEquals("dark", displayNoSupportedLocaleAndNoFallback?.theme)

        val displayWithFallbackAndNoSupportedLocale = getLocalizedAndThemedDisplay(
            credentialDisplays = withFallbackAndNoSupportedLocale,
            preferredTheme = "light"
        )
        assertEquals("fallback", displayWithFallbackAndNoSupportedLocale?.locale)
        assertEquals("dark", displayWithFallbackAndNoSupportedLocale?.theme)
    }

    @Test
    fun `App locale with unsupported country code returns CredentialDisplay in requested theme with best matching locale`() = runTest {
        coEvery { mockGetCurrentAppLocale() } returns Locale("de", "XX")
        val displayWithSupportedLocaleWithCountryCode = getLocalizedAndThemedDisplay(
            credentialDisplays = withSupportedLocaleWithCountryCode,
            preferredTheme = "dark"
        )
        assertEquals("de-CH", displayWithSupportedLocaleWithCountryCode?.locale)
        assertEquals("dark", displayWithSupportedLocaleWithCountryCode?.theme)

        val displayWithSupportedLocaleNoCountryCode = getLocalizedAndThemedDisplay(
            credentialDisplays = withSupportedLocaleNoCountryCode,
            preferredTheme = "dark"
        )
        assertEquals("en", displayWithSupportedLocaleNoCountryCode?.locale)
        assertEquals("dark", displayWithSupportedLocaleNoCountryCode?.theme)

        val displayNoSupportedLocaleAndNoFallback = getLocalizedAndThemedDisplay(
            credentialDisplays = noSupportedLocaleAndNoFallback,
            preferredTheme = "dark"
        )
        assertEquals("xx", displayNoSupportedLocaleAndNoFallback?.locale)
        assertEquals("dark", displayNoSupportedLocaleAndNoFallback?.theme)

        val displayWithFallbackAndNoSupportedLocale = getLocalizedAndThemedDisplay(
            credentialDisplays = withFallbackAndNoSupportedLocale,
            preferredTheme = "dark"
        )
        assertEquals("fallback", displayWithFallbackAndNoSupportedLocale?.locale)
        assertEquals("dark", displayWithFallbackAndNoSupportedLocale?.theme)
    }

    @Test
    fun `App locale with unsupported country code returns CredentialDisplay in not requested theme with best matching locale`() = runTest {
        coEvery { mockGetCurrentAppLocale() } returns Locale("de", "XX")
        val displayWithSupportedLocaleWithCountryCode = getLocalizedAndThemedDisplay(
            credentialDisplays = withSupportedLocaleWithCountryCode,
            preferredTheme = "light"
        )
        assertEquals("de-CH", displayWithSupportedLocaleWithCountryCode?.locale)
        assertEquals("dark", displayWithSupportedLocaleWithCountryCode?.theme)

        val displayWithSupportedLocaleNoCountryCode = getLocalizedAndThemedDisplay(
            credentialDisplays = withSupportedLocaleNoCountryCode,
            preferredTheme = "light"
        )
        assertEquals("en", displayWithSupportedLocaleNoCountryCode?.locale)
        assertEquals("dark", displayWithSupportedLocaleNoCountryCode?.theme)

        val displayNoSupportedLocaleAndNoFallback = getLocalizedAndThemedDisplay(
            credentialDisplays = noSupportedLocaleAndNoFallback,
            preferredTheme = "light"
        )
        assertEquals("xx", displayNoSupportedLocaleAndNoFallback?.locale)
        assertEquals("dark", displayNoSupportedLocaleAndNoFallback?.theme)

        val displayWithFallbackAndNoSupportedLocale = getLocalizedAndThemedDisplay(
            credentialDisplays = withFallbackAndNoSupportedLocale,
            preferredTheme = "light"
        )
        assertEquals("fallback", displayWithFallbackAndNoSupportedLocale?.locale)
        assertEquals("dark", displayWithFallbackAndNoSupportedLocale?.theme)
    }

    @Test
    fun `App locale without country code returns CredentialDisplay in requested theme with best matching locale`() = runTest {
        coEvery { mockGetCurrentAppLocale() } returns Locale("de")
        val displayWithSupportedLocaleWithCountryCode = getLocalizedAndThemedDisplay(
            credentialDisplays = withSupportedLocaleWithCountryCode,
            preferredTheme = "dark"
        )
        assertEquals("de-CH", displayWithSupportedLocaleWithCountryCode?.locale)
        assertEquals("dark", displayWithSupportedLocaleWithCountryCode?.theme)

        val displayWithSupportedLocaleNoCountryCode = getLocalizedAndThemedDisplay(
            credentialDisplays = withSupportedLocaleNoCountryCode,
            preferredTheme = "dark"
        )
        assertEquals("en", displayWithSupportedLocaleNoCountryCode?.locale)
        assertEquals("dark", displayWithSupportedLocaleNoCountryCode?.theme)

        val displayNoSupportedLocaleAndNoFallback = getLocalizedAndThemedDisplay(
            credentialDisplays = noSupportedLocaleAndNoFallback,
            preferredTheme = "dark"
        )
        assertEquals("xx", displayNoSupportedLocaleAndNoFallback?.locale)
        assertEquals("dark", displayNoSupportedLocaleAndNoFallback?.theme)

        val displayWithFallbackAndNoSupportedLocale = getLocalizedAndThemedDisplay(
            credentialDisplays = withFallbackAndNoSupportedLocale,
            preferredTheme = "dark"
        )
        assertEquals("fallback", displayWithFallbackAndNoSupportedLocale?.locale)
        assertEquals("dark", displayWithFallbackAndNoSupportedLocale?.theme)
    }

    @Test
    fun `App locale without country code returns CredentialDisplay in not requested theme with best matching locale`() = runTest {
        coEvery { mockGetCurrentAppLocale() } returns Locale("de")
        val displayWithSupportedLocaleWithCountryCode = getLocalizedAndThemedDisplay(
            credentialDisplays = withSupportedLocaleWithCountryCode,
            preferredTheme = "light"
        )
        assertEquals("de-CH", displayWithSupportedLocaleWithCountryCode?.locale)
        assertEquals("dark", displayWithSupportedLocaleWithCountryCode?.theme)

        val displayWithSupportedLocaleNoCountryCode = getLocalizedAndThemedDisplay(
            credentialDisplays = withSupportedLocaleNoCountryCode,
            preferredTheme = "light"
        )
        assertEquals("en", displayWithSupportedLocaleNoCountryCode?.locale)
        assertEquals("dark", displayWithSupportedLocaleNoCountryCode?.theme)

        val displayNoSupportedLocaleAndNoFallback = getLocalizedAndThemedDisplay(
            credentialDisplays = noSupportedLocaleAndNoFallback,
            preferredTheme = "light"
        )
        assertEquals("xx", displayNoSupportedLocaleAndNoFallback?.locale)
        assertEquals("dark", displayNoSupportedLocaleAndNoFallback?.theme)

        val displayWithFallbackAndNoSupportedLocale = getLocalizedAndThemedDisplay(
            credentialDisplays = withFallbackAndNoSupportedLocale,
            preferredTheme = "light"
        )
        assertEquals("fallback", displayWithFallbackAndNoSupportedLocale?.locale)
        assertEquals("dark", displayWithFallbackAndNoSupportedLocale?.theme)
    }

    @Test
    fun `Empty input list returns empty list`() = runTest {
        coEvery { mockGetCurrentAppLocale() } returns Locale("de", "CH")

        assertNull(getLocalizedAndThemedDisplay(emptyList()))
    }
}
