package ch.admin.foitt.wallet.platform.oca.domain.usecase.implementation

import ch.admin.foitt.wallet.platform.credential.domain.model.AnyClaimDisplay
import ch.admin.foitt.wallet.platform.credential.domain.model.AnyCredentialDisplay
import ch.admin.foitt.wallet.platform.database.domain.model.Cluster
import ch.admin.foitt.wallet.platform.database.domain.model.CredentialClaim
import ch.admin.foitt.wallet.platform.database.domain.model.DisplayLanguage
import ch.admin.foitt.wallet.platform.oca.domain.model.AttributeType
import ch.admin.foitt.wallet.platform.oca.domain.model.CaptureBase
import ch.admin.foitt.wallet.platform.oca.domain.model.DateTimePattern
import ch.admin.foitt.wallet.platform.oca.domain.model.OcaBundle
import ch.admin.foitt.wallet.platform.oca.domain.model.OcaClaimData
import ch.admin.foitt.wallet.platform.oca.domain.model.OcaCredentialData
import ch.admin.foitt.wallet.platform.oca.domain.model.OcaError
import ch.admin.foitt.wallet.platform.oca.domain.model.overlays.CharacterEncoding
import ch.admin.foitt.wallet.platform.oca.domain.model.overlays.Standard
import ch.admin.foitt.wallet.platform.oca.domain.usecase.GenerateOcaDisplays
import ch.admin.foitt.wallet.platform.oca.domain.usecase.GetRootCaptureBase
import ch.admin.foitt.wallet.platform.ssi.domain.model.ValueType
import ch.admin.foitt.wallet.util.assertErrorType
import ch.admin.foitt.wallet.util.assertOk
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.unmockkAll
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.junit.jupiter.params.provider.ValueSource
import java.util.stream.Stream

class GenerateOcaDisplaysImplTest {

    @MockK
    private lateinit var mockGetRootCaptureBase: GetRootCaptureBase

    @MockK
    private lateinit var mockOcaBundle: OcaBundle

    @MockK
    private lateinit var mockRootCaptureBase: CaptureBase

    private lateinit var useCase: GenerateOcaDisplays

    @BeforeEach
    fun setup() {
        MockKAnnotations.init(this)

        useCase = GenerateOcaDisplaysImpl(
            getRootCaptureBase = mockGetRootCaptureBase,
        )

        setupDefaultMocks()
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `Generating valid Oca displays returns success`() = runTest {
        val result = useCase(credentialClaims, mockOcaBundle).assertOk()

        val expectedCredentialDisplays = listOf(
            AnyCredentialDisplay(
                locale = LANGUAGE_EN,
                name = "name",
                description = "description",
                logo = "logoData",
                backgroundColor = "backgroundColor",
                theme = "theme"
            ),
        )

        val expectedClaim = CredentialClaim(
            clusterId = -1,
            key = CLAIM_KEY,
            value = CLAIM_VALUE,
            valueType = ValueType.STRING.value,
            order = -1,
        )
        val expectedClaimDisplays = listOf(
            AnyClaimDisplay(locale = LANGUAGE_EN, name = CLAIM_VALUE_EN),
            AnyClaimDisplay(locale = DisplayLanguage.FALLBACK, name = CLAIM_KEY)
        )
        val expectedClaims = mapOf(expectedClaim to expectedClaimDisplays)
        val expectedCluster = Cluster(
            claims = expectedClaims
        )

        assertEquals(expectedCredentialDisplays, result.credentialDisplays)
        assertEquals(1, result.clusters.size)
        assertEquals(expectedCluster, result.clusters.first())
    }

    @Test
    fun `Generating Oca displays adds fallback language if empty`() = runTest {
        every { mockOcaBundle.getAttributes("digest") } returns listOf(ocaClaimDataNoLabel)

        val result = useCase(credentialClaims, mockOcaBundle).assertOk()

        val expectedClaim = CredentialClaim(
            clusterId = -1,
            key = CLAIM_KEY,
            value = CLAIM_VALUE,
            valueType = ValueType.STRING.value,
            order = -1,
        )
        val expectedClaimDisplays = listOf(
            AnyClaimDisplay(locale = DisplayLanguage.FALLBACK, name = CLAIM_KEY)
        )
        val expectedClaims = mapOf(expectedClaim to expectedClaimDisplays)
        val expectedCluster = Cluster(
            claims = expectedClaims,
        )

        assertEquals(1, result.clusters.size)
        assertEquals(expectedCluster, result.clusters.first())
    }

    @ParameterizedTest(name = "{index} should be {2}")
    @MethodSource("generateIso8601TestData")
    fun `Generating correct Oca displays for ISO6801 date times`(
        datetime: String,
        expectedValue: String,
        expectedValueDisplayInfo: String?
    ) = runTest {
        val ocaClaimData = OcaClaimData(
            captureBaseDigest = "digest",
            name = CLAIM_KEY,
            attributeType = AttributeType.DateTime,
            standard = Standard.Iso8601,
            dataSources = mapOf("vc+sd-jwt" to JSON_PATH)
        )
        every { mockOcaBundle.getAttributeForJsonPath(JSON_PATH) } returns ocaClaimData
        every { mockOcaBundle.getAttributes("digest") } returns listOf(ocaClaimData)

        val credentialClaims = mapOf(CLAIM_KEY to datetime)

        val result = useCase(credentialClaims, mockOcaBundle).assertOk()

        val expectedClaim = CredentialClaim(
            clusterId = -1,
            key = CLAIM_KEY,
            value = expectedValue,
            valueType = ValueType.DATETIME.value,
            valueDisplayInfo = expectedValueDisplayInfo
        )
        val expectedClaimDisplays = listOf(fallbackClaimDisplay)
        val expectedClaims = mapOf(expectedClaim to expectedClaimDisplays)
        val expectedCluster = Cluster(
            claims = expectedClaims
        )

        assertEquals(expectedCluster, result.clusters.first())
    }

    @ParameterizedTest
    @MethodSource("generateUnixtimeTestData")
    fun `Generating correct Oca displays for Unixtime date times`(
        datetime: String,
        expectedValue: String,
        expectedValueDisplayInfo: String?
    ) = runTest {
        val ocaClaimData = OcaClaimData(
            captureBaseDigest = "digest",
            name = CLAIM_KEY,
            attributeType = AttributeType.DateTime,
            standard = Standard.UnixTime,
            dataSources = mapOf("vc+sd-jwt" to JSON_PATH)
        )
        every { mockOcaBundle.getAttributeForJsonPath(JSON_PATH) } returns ocaClaimData
        every { mockOcaBundle.getAttributes("digest") } returns listOf(ocaClaimData)

        val credentialClaims = mapOf(CLAIM_KEY to datetime)

        val result = useCase(credentialClaims, mockOcaBundle).assertOk()

        val expectedClaim = CredentialClaim(
            clusterId = -1,
            key = CLAIM_KEY,
            value = expectedValue,
            valueType = ValueType.DATETIME.value,
            valueDisplayInfo = expectedValueDisplayInfo
        )
        val expectedClaimDisplays = listOf(fallbackClaimDisplay)
        val expectedClaims = mapOf(expectedClaim to expectedClaimDisplays)
        val expectedCluster = Cluster(
            claims = expectedClaims
        )

        assertEquals(expectedCluster, result.clusters.first())
    }

    @ParameterizedTest
    @ValueSource(
        strings = [
            "image/jpeg",
            "image/png"
        ]
    )
    fun `Generating Oca displays transforms Image DataUri`(mimetype: String) = runTest {
        val ocaClaimData = OcaClaimData(
            captureBaseDigest = "digest",
            name = CLAIM_KEY,
            attributeType = AttributeType.Text,
            standard = Standard.DataUrl,
            dataSources = mapOf("vc+sd-jwt" to JSON_PATH)
        )
        every { mockOcaBundle.getAttributes("digest") } returns listOf(ocaClaimData)

        val base64ImagePayload = "some base64 encoded image payload"
        val credentialClaims = mapOf(CLAIM_KEY to "data:$mimetype;base64,$base64ImagePayload")

        val result = useCase(credentialClaims, mockOcaBundle).assertOk()

        val expectedClaim = CredentialClaim(
            clusterId = -1,
            key = CLAIM_KEY,
            value = base64ImagePayload,
            valueType = mimetype
        )
        val expectedClaimDisplays = listOf(fallbackClaimDisplay)
        val expectedClaims = mapOf(expectedClaim to expectedClaimDisplays)
        val expectedCluster = Cluster(
            claims = expectedClaims
        )

        assertEquals(1, result.clusters.size)
        assertEquals(expectedCluster, result.clusters.first())
    }

    @ParameterizedTest
    @ValueSource(
        strings = [
            "image/jpeg",
            "image/png"
        ]
    )
    fun `Generating Oca displays return correct Binary images`(mimetype: String) = runTest {
        val ocaClaimData = OcaClaimData(
            captureBaseDigest = "digest",
            name = CLAIM_KEY,
            attributeType = AttributeType.Binary,
            characterEncoding = CharacterEncoding.Base64,
            format = mimetype,
            dataSources = mapOf("vc+sd-jwt" to JSON_PATH)
        )
        every { mockOcaBundle.getAttributes("digest") } returns listOf(ocaClaimData)

        val binaryImagePayload = "some binary image payload"
        val credentialClaims = mapOf(CLAIM_KEY to binaryImagePayload)

        val result = useCase(credentialClaims, mockOcaBundle).assertOk()

        val expectedClaim = CredentialClaim(
            clusterId = -1,
            key = CLAIM_KEY,
            value = binaryImagePayload,
            valueType = mimetype
        )
        val expectedClaimDisplays = listOf(fallbackClaimDisplay)
        val expectedClaims = mapOf(expectedClaim to expectedClaimDisplays)
        val expectedCluster = Cluster(
            order = -1,
            claims = expectedClaims,
            clusterDisplays = emptyList(),
            childClusters = emptyList(),
        )

        assertEquals(1, result.clusters.size)
        assertEquals(expectedCluster, result.clusters.first())
    }

    @Test
    fun `Generating Oca displays adds fallback language if not contained`() = runTest {
        val result = useCase(credentialClaims, mockOcaBundle).assertOk()

        val expectedClaim = CredentialClaim(
            clusterId = -1,
            key = CLAIM_KEY,
            value = CLAIM_VALUE,
            valueType = ValueType.STRING.value,
            order = -1,
        )
        val expectedClaimDisplays = listOf(
            AnyClaimDisplay(locale = LANGUAGE_EN, name = CLAIM_VALUE_EN),
            AnyClaimDisplay(locale = DisplayLanguage.FALLBACK, name = CLAIM_KEY)
        )
        val expectedClaims = mapOf(expectedClaim to expectedClaimDisplays)
        val expectedCluster = Cluster(
            claims = expectedClaims
        )

        assertEquals(1, result.clusters.size)
        assertEquals(expectedCluster, result.clusters.first())
    }

    @Test
    fun `Generating Oca displays maps errors from getting the root capture base`() = runTest {
        coEvery { mockGetRootCaptureBase(any()) } returns Err(OcaError.InvalidRootCaptureBase)

        useCase(credentialClaims, mockOcaBundle).assertErrorType(OcaError.InvalidRootCaptureBase::class)
    }

    private fun setupDefaultMocks() {
        every { mockRootCaptureBase.digest } returns "digest"
        val captureBases = listOf(mockRootCaptureBase)
        every { mockOcaBundle.getAttributeForJsonPath(JSON_PATH) } returns ocaClaimData
        every { mockOcaBundle.getAttributes("digest") } returns listOf(ocaClaimData)
        every { mockOcaBundle.ocaCredentialData } returns listOf(ocaCredentialData)
        every { mockOcaBundle.captureBases } returns captureBases

        coEvery { mockGetRootCaptureBase(captureBases) } returns Ok(mockRootCaptureBase)
    }

    private companion object {
        const val CLAIM_KEY = "claim_key"
        const val CLAIM_VALUE = "claim_value"
        const val LANGUAGE_EN = "en"
        const val CLAIM_VALUE_EN = "claim value en"
        const val JSON_PATH = "$.$CLAIM_KEY"

        val ocaClaimData = OcaClaimData(
            captureBaseDigest = "digest",
            name = "claim_key",
            attributeType = AttributeType.Text,
            labels = mapOf(LANGUAGE_EN to CLAIM_VALUE_EN),
            dataSources = mapOf("vc+sd-jwt" to JSON_PATH)
        )

        val ocaClaimDataNoLabel = ocaClaimData.copy(labels = emptyMap())

        val ocaCredentialData = OcaCredentialData(
            captureBaseDigest = "digest",
            locale = LANGUAGE_EN,
            name = "name",
            description = "description",
            logoData = "logoData",
            backgroundColor = "backgroundColor",
            theme = "theme"
        )

        val credentialClaims = mapOf(CLAIM_KEY to CLAIM_VALUE)
        val fallbackClaimDisplay = AnyClaimDisplay(name = CLAIM_KEY, locale = DisplayLanguage.FALLBACK)

        @JvmStatic
        fun generateUnixtimeTestData(): Stream<Arguments> = Stream.of(
            // Unixtime input string, expected output value, expected output pattern
            Arguments.of(
                "1749132605",
                "2025-06-05T14:10:05Z",
                DateTimePattern.DATE_TIME_TIMEZONE.name
            ),
            Arguments.of(
                "0",
                "1970-01-01T00:00Z",
                DateTimePattern.DATE_TIME_TIMEZONE.name
            ),
            Arguments.of(
                "invalid unixtime",
                "invalid unixtime",
                null
            ),
        )

        @JvmStatic
        fun generateIso8601TestData(): Stream<Arguments> = Stream.of(
            // ISO8601 input string, expected output value, expected output pattern
            Arguments.of(
                "2023-10-05T14:30:00.12Z",
                "2023-10-05T14:30:00.120Z",
                DateTimePattern.DATE_TIME_TIMEZONE_SECONDS_FRACTION.name
            ),
            Arguments.of(
                "2023-10-05T14:30:00.12-05:00",
                "2023-10-05T14:30:00.120-05:00",
                DateTimePattern.DATE_TIME_TIMEZONE_SECONDS_FRACTION.name
            ),
            Arguments.of(
                "2023-10-05T21:22:23.12+05:00",
                "2023-10-05T21:22:23.120+05:00",
                DateTimePattern.DATE_TIME_TIMEZONE_SECONDS_FRACTION.name,
            ),
            Arguments.of(
                "2023-10-05T14:30:00Z",
                "2023-10-05T14:30Z",
                DateTimePattern.DATE_TIME_TIMEZONE_SECONDS.name
            ),
            Arguments.of(
                "2023-10-05T14:30:00-05:00",
                "2023-10-05T14:30-05:00",
                DateTimePattern.DATE_TIME_TIMEZONE_SECONDS.name
            ),
            Arguments.of(
                "2022-12-31T23:59:59+00:00",
                "2022-12-31T23:59:59Z",
                DateTimePattern.DATE_TIME_TIMEZONE_SECONDS.name
            ),
            Arguments.of(
                "2023-10-05T21:22:23+05:00",
                "2023-10-05T21:22:23+05:00",
                DateTimePattern.DATE_TIME_TIMEZONE_SECONDS.name,
            ),
            Arguments.of(
                "2023-10-05T21:22:23Z",
                "2023-10-05T21:22:23Z",
                DateTimePattern.DATE_TIME_TIMEZONE_SECONDS.name,
            ),
            Arguments.of(
                "2023-10-05t21:22:23Z",
                "2023-10-05T21:22:23Z",
                DateTimePattern.DATE_TIME_TIMEZONE_SECONDS.name,
            ),
            Arguments.of(
                "2007-04-05T14:30Z",
                "2007-04-05T14:30Z",
                DateTimePattern.DATE_TIME_TIMEZONE.name
            ),
            Arguments.of(
                "2007-04-05T14:30-02:30",
                "2007-04-05T14:30-02:30",
                DateTimePattern.DATE_TIME_TIMEZONE.name
            ),
            Arguments.of(
                "2022-12-31T23:59:01.123",
                "2022-12-31T23:59:01.123Z",
                DateTimePattern.DATE_TIME_SECONDS_FRACTION.name
            ),
            Arguments.of(
                "2022-12-31T23:59:23.12",
                "2022-12-31T23:59:23.120Z",
                DateTimePattern.DATE_TIME_SECONDS_FRACTION.name
            ),
            Arguments.of(
                "2022-12-31T23:59:23",
                "2022-12-31T23:59:23Z",
                DateTimePattern.DATE_TIME_SECONDS.name
            ),
            Arguments.of(
                "2022-12-31T23:59:00",
                "2022-12-31T23:59Z",
                DateTimePattern.DATE_TIME_SECONDS.name
            ),
            Arguments.of(
                "2022-12-31T23:59",
                "2022-12-31T23:59Z",
                DateTimePattern.DATE_TIME.name
            ),
            Arguments.of(
                "2023-10-05+05:00",
                "2023-10-05T00:00+05:00",
                DateTimePattern.DATE_TIMEZONE.name,
            ),
            Arguments.of(
                "2023-10-05Z",
                "2023-10-05T00:00Z",
                DateTimePattern.DATE_TIMEZONE.name,
            ),
            Arguments.of(
                "2023-10-05",
                "2023-10-05T00:00Z",
                DateTimePattern.DATE.name
            ),

            Arguments.of(
                "10:10:10.12Z",
                "0000-01-01T10:10:10.120Z",
                DateTimePattern.TIME_TIMEZONE_SECONDS_FRACTION.name
            ),
            Arguments.of(
                "10:10:10.123+05:30",
                "0000-01-01T10:10:10.123+05:30",
                DateTimePattern.TIME_TIMEZONE_SECONDS_FRACTION.name
            ),
            Arguments.of(
                "10:10:10Z",
                "0000-01-01T10:10:10Z",
                DateTimePattern.TIME_TIMEZONE_SECONDS.name
            ),
            Arguments.of(
                "10:10:10-03:00",
                "0000-01-01T10:10:10-03:00",
                DateTimePattern.TIME_TIMEZONE_SECONDS.name
            ),
            Arguments.of(
                "10:10Z",
                "0000-01-01T10:10Z",
                DateTimePattern.TIME_TIMEZONE.name
            ),
            Arguments.of(
                "10:10+05:00",
                "0000-01-01T10:10+05:00",
                DateTimePattern.TIME_TIMEZONE.name
            ),
            Arguments.of(
                "10:10:10.12",
                "0000-01-01T10:10:10.120Z",
                DateTimePattern.TIME_SECONDS_FRACTION.name
            ),
            Arguments.of(
                "10:10:10.123456789",
                "0000-01-01T10:10:10.123456789Z",
                DateTimePattern.TIME_SECONDS_FRACTION.name
            ),
            Arguments.of(
                "10:10:10",
                "0000-01-01T10:10:10Z",
                DateTimePattern.TIME_SECONDS.name
            ),
            Arguments.of(
                "10:10:00",
                "0000-01-01T10:10Z",
                DateTimePattern.TIME_SECONDS.name
            ),
            Arguments.of(
                "10:10",
                "0000-01-01T10:10Z",
                DateTimePattern.TIME.name
            ),
            Arguments.of(
                "2023-10",
                "2023-10-01T00:00Z",
                DateTimePattern.YEAR_MONTH.name
            ),
            Arguments.of(
                "2023-10",
                "2023-10-01T00:00Z",
                DateTimePattern.YEAR_MONTH.name
            ),
            Arguments.of(
                "2023",
                "2023-01-01T00:00Z",
                DateTimePattern.YEAR.name
            ),
            Arguments.of(
                "-2023-10",
                "-2023-10-01T00:00Z",
                DateTimePattern.YEAR_MONTH.name
            ),
            // Too many decimal fraction
            Arguments.of(
                "10:10:10.1234567890",
                "10:10:10.1234567890",
                null
            ),
            Arguments.of(
                "invalid iso8601",
                "invalid iso8601",
                null
            )
        )
    }
}
