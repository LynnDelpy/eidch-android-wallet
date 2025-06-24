package ch.admin.foitt.wallet.platform.ssi.domain.usecase.implementation

import ch.admin.foitt.wallet.platform.database.domain.model.CredentialClaimDisplay
import ch.admin.foitt.wallet.platform.locale.domain.usecase.GetCurrentAppLocale
import ch.admin.foitt.wallet.platform.locale.domain.usecase.GetLocalizedDisplay
import ch.admin.foitt.wallet.platform.ssi.domain.model.CredentialClaimImage
import ch.admin.foitt.wallet.platform.ssi.domain.model.CredentialClaimText
import ch.admin.foitt.wallet.platform.ssi.domain.model.MapToCredentialClaimDataError
import ch.admin.foitt.wallet.platform.ssi.domain.usecase.MapToCredentialClaimData
import ch.admin.foitt.wallet.platform.ssi.domain.usecase.implementation.mock.MockCredentialClaim.buildClaimWithDisplays
import ch.admin.foitt.wallet.platform.ssi.domain.usecase.implementation.mock.MockCredentialClaim.credentialClaimDisplay
import ch.admin.foitt.wallet.platform.ssi.domain.usecase.implementation.mock.MockCredentialClaim.credentialClaimDisplays
import ch.admin.foitt.wallet.platform.utils.base64NonUrlStringToByteArray
import ch.admin.foitt.wallet.util.assertErrorType
import ch.admin.foitt.wallet.util.assertOk
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import java.util.Locale

class MapToCredentialClaimDataImplTest {

    private lateinit var mapToCredentialClaimData: MapToCredentialClaimData

    @MockK
    private lateinit var mockGetLocalizedDisplay: GetLocalizedDisplay

    @MockK
    private lateinit var mockGetCurrentAppLocale: GetCurrentAppLocale

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)

        mockkStatic("ch.admin.foitt.wallet.platform.utils.StringUtilsKt")
        every { base64NonUrlStringToByteArray(any()) } returns byteArrayOf()

        mockkStatic("android.text.format.DateFormat")

        mapToCredentialClaimData = MapToCredentialClaimDataImpl(
            getLocalizedDisplay = mockGetLocalizedDisplay,
            getCurrentAppLocale = mockGetCurrentAppLocale
        )

        coEvery { mockGetLocalizedDisplay(displays = credentialClaimDisplays) } returns credentialClaimDisplay
        coEvery { mockGetCurrentAppLocale() } returns Locale.ENGLISH
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @ParameterizedTest
    @ValueSource(
        strings = [
            "bool",
            "datetime",
            "numeric",
            "string"
        ]
    )
    fun `Claim with supported string type should return CredentialClaimText`(valueTypeString: String) = runTest {
        val claimWithDisplays = buildClaimWithDisplays(valueTypeString)

        val data = mapToCredentialClaimData(claimWithDisplays).assertOk()
        assertTrue(data is CredentialClaimText, "string valueType should return ${CredentialClaimText::class.simpleName}")
        assertEquals(credentialClaimDisplay.name, data.localizedKey)
        assertEquals(claimWithDisplays.claim.value, (data as CredentialClaimText).value)
    }

    @ParameterizedTest
    @ValueSource(
        strings = [
            "image/png",
            "image/jpeg",
        ]
    )
    fun `Claim with supported image mime type should return correct data`(imageMimeType: String) = runTest {
        val claimWithDisplays = buildClaimWithDisplays(imageMimeType)

        val data = mapToCredentialClaimData(claimWithDisplays).assertOk()
        assertTrue(data is CredentialClaimImage, "$imageMimeType mime type should return ${CredentialClaimImage::class.simpleName}")
        assertEquals(credentialClaimDisplay.name, data.localizedKey)
        assertEquals(base64NonUrlStringToByteArray(claimWithDisplays.claim.value), (data as CredentialClaimImage).imageData)
    }

    @ParameterizedTest
    @ValueSource(
        strings = [
            "boolean",
            "image",
            "image/jp",
            "image/jp2",
            "image/jpg",
            "imageDataUri",
        ]
    )
    fun `Claim with invalid valueType should return an error`(valueTypeString: String) = runTest {
        mapToCredentialClaimData(buildClaimWithDisplays(valueTypeString))
            .assertErrorType(MapToCredentialClaimDataError::class)
    }

    @Test
    fun `Claim with null valueType should return an error`() = runTest {
        mapToCredentialClaimData(buildClaimWithDisplays("null"))
            .assertErrorType(MapToCredentialClaimDataError::class)
    }

    @Test
    fun `Claim with empty valueType should return an error`() = runTest {
        mapToCredentialClaimData(buildClaimWithDisplays(""))
            .assertErrorType(MapToCredentialClaimDataError::class)
    }

    @Test
    fun `Claim with string valueType but no displays should return an error`() = runTest {
        coEvery { mockGetLocalizedDisplay(displays = any<List<CredentialClaimDisplay>>()) } returns null
        val claimWithDisplays = buildClaimWithDisplays("string", emptyList())

        mapToCredentialClaimData(claimWithDisplays)
            .assertErrorType(MapToCredentialClaimDataError::class)
    }
}
