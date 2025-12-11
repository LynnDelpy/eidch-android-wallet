package ch.admin.foitt.wallet.platform.oca

import ch.admin.foitt.wallet.platform.oca.domain.model.AttributeType
import ch.admin.foitt.wallet.platform.oca.domain.model.OcaClaimData
import ch.admin.foitt.wallet.platform.oca.domain.model.overlays.DataSourceOverlay1x0
import ch.admin.foitt.wallet.platform.oca.domain.usecase.GenerateOcaClaimData
import ch.admin.foitt.wallet.platform.oca.domain.usecase.implementation.GenerateOcaClaimDataImpl
import ch.admin.foitt.wallet.platform.oca.mock.ocaMocks.OcaMocks.ATTRIBUTE_KEY_ADDRESS_STREET
import ch.admin.foitt.wallet.platform.oca.mock.ocaMocks.OcaMocks.ATTRIBUTE_KEY_AGE
import ch.admin.foitt.wallet.platform.oca.mock.ocaMocks.OcaMocks.ATTRIBUTE_KEY_FIRSTNAME
import ch.admin.foitt.wallet.platform.oca.mock.ocaMocks.OcaMocks.ATTRIBUTE_KEY_NAME
import ch.admin.foitt.wallet.platform.oca.mock.ocaMocks.OcaMocks.ATTRIBUTE_LABEL_AGE_DE
import ch.admin.foitt.wallet.platform.oca.mock.ocaMocks.OcaMocks.ATTRIBUTE_LABEL_AGE_EN
import ch.admin.foitt.wallet.platform.oca.mock.ocaMocks.OcaMocks.ATTRIBUTE_LABEL_FIRSTNAME_DE
import ch.admin.foitt.wallet.platform.oca.mock.ocaMocks.OcaMocks.ATTRIBUTE_LABEL_FIRSTNAME_EN
import ch.admin.foitt.wallet.platform.oca.mock.ocaMocks.OcaMocks.CREDENTIAL_FORMAT
import ch.admin.foitt.wallet.platform.oca.mock.ocaMocks.OcaMocks.DIGEST
import ch.admin.foitt.wallet.platform.oca.mock.ocaMocks.OcaMocks.JSON_PATH_AGE
import ch.admin.foitt.wallet.platform.oca.mock.ocaMocks.OcaMocks.JSON_PATH_FIRSTNAME
import ch.admin.foitt.wallet.platform.oca.mock.ocaMocks.OcaMocks.LANGUAGE_DE
import ch.admin.foitt.wallet.platform.oca.mock.ocaMocks.OcaMocks.LANGUAGE_EN
import ch.admin.foitt.wallet.platform.oca.mock.ocaMocks.OcaMocks.ocaNested
import ch.admin.foitt.wallet.platform.oca.mock.ocaMocks.OcaMocks.ocaSimple
import io.mockk.MockKAnnotations
import io.mockk.unmockkAll
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class OcaBundleTest {

    private lateinit var generateOcaClaimData: GenerateOcaClaimData

    @BeforeEach
    fun setup() {
        MockKAnnotations.init(this)

        generateOcaClaimData = GenerateOcaClaimDataImpl()
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `OcaBundle correctly gets attributes for data source format`() = runTest {
        val result = ocaSimple.getAttributesForDataSourceFormat(CREDENTIAL_FORMAT, null)

        val expectedLabelsFirstname = mapOf(
            LANGUAGE_EN to ATTRIBUTE_LABEL_FIRSTNAME_EN,
            LANGUAGE_DE to ATTRIBUTE_LABEL_FIRSTNAME_DE,
        )

        val expectedDataSourcesFirstname = mapOf(
            CREDENTIAL_FORMAT to JSON_PATH_FIRSTNAME
        )

        val expectedLabelsAge = mapOf(
            LANGUAGE_EN to ATTRIBUTE_LABEL_AGE_EN,
            LANGUAGE_DE to ATTRIBUTE_LABEL_AGE_DE,
        )

        val expectedDataSourcesAge = mapOf(
            CREDENTIAL_FORMAT to JSON_PATH_AGE
        )

        val expected = mapOf(
            JSON_PATH_FIRSTNAME to OcaClaimData(
                captureBaseDigest = DIGEST,
                name = ATTRIBUTE_KEY_FIRSTNAME,
                attributeType = AttributeType.Text,
                labels = expectedLabelsFirstname,
                dataSources = expectedDataSourcesFirstname,
                isSensitive = false
            ),
            JSON_PATH_AGE to OcaClaimData(
                captureBaseDigest = DIGEST,
                name = ATTRIBUTE_KEY_AGE,
                attributeType = AttributeType.Numeric,
                labels = expectedLabelsAge,
                dataSources = expectedDataSourcesAge,
                isSensitive = false
            )
        )

        assertEquals(expected, result)
    }

    @ParameterizedTest
    @ValueSource(
        strings = ["$.firstname", """$["firstname"]""", "$['firstname']"]
    )
    fun `OcaBundle getAttributeForJsonPath with one level path returns attributes`(jsonPath: String) = runTest {
        val result = ocaSimple.getAttributeForJsonPath(jsonPath = jsonPath)

        assertEquals(ATTRIBUTE_KEY_FIRSTNAME, result?.name)
    }

    @ParameterizedTest
    @ValueSource(
        strings = ["$.address.street", """$["address"].street""", "$['address'].street", """$["address"]['street']"""]
    )
    fun `OcaBundle getAttributeForJsonPath with multi level path returns attributes`(jsonPath: String) = runTest {
        val result = ocaNested.getAttributeForJsonPath(jsonPath = jsonPath)

        assertEquals(ATTRIBUTE_KEY_ADDRESS_STREET, result?.name)
    }

    @ParameterizedTest
    @ValueSource(
        strings = ["$.pets[0].name", """$["pets"][0].name""", "$['pets'][0]['name']"]
    )
    fun `OcaBundle getAttributeForJsonPath with array index path returns attributes`(jsonPath: String) = runTest {
        val result = ocaNested.getAttributeForJsonPath(jsonPath = jsonPath)

        assertEquals(ATTRIBUTE_KEY_NAME, result?.name)
    }

    @ParameterizedTest
    @ValueSource(
        strings = ["$.foobar.firstname", """$.foobar["firstname"]""", "$['foobar']['firstname']", """$["foobar"]['firstname']"""]
    )
    fun `OcaBundle getAttributeForJsonPath with different jsonPath notations in DataSource Overlay returns correct attributes`(
        jsonPath: String
    ) = runTest {
        val overlays = listOf(
            DataSourceOverlay1x0(
                captureBaseDigest = DIGEST,
                format = CREDENTIAL_FORMAT,
                attributeSources = mapOf(
                    ATTRIBUTE_KEY_FIRSTNAME to jsonPath,
                )
            )
        )
        val attributes = generateOcaClaimData(overlays = overlays, captureBases = ocaSimple.captureBases)
        val oca = ocaSimple.copy(ocaClaimData = attributes)
        val result = oca.getAttributeForJsonPath(jsonPath = "$.foobar.firstname")

        assertEquals(ATTRIBUTE_KEY_FIRSTNAME, result?.name)
    }

    @ParameterizedTest
    @ValueSource(
        strings = ["${'$'}foobar.firstname", "$..firstname", "$..['firstname']", """"$.foobar[*]""", "$[*]['firstname']", "$.*.firstname"]
    )
    fun `OcaBundle getAttributeForJsonPath with unsupported jsonPath in DataSource Overlay returns null`(
        jsonPath: String
    ) = runTest {
        val overlays = listOf(
            DataSourceOverlay1x0(
                captureBaseDigest = DIGEST,
                format = CREDENTIAL_FORMAT,
                attributeSources = mapOf(
                    ATTRIBUTE_KEY_FIRSTNAME to jsonPath,
                )
            )
        )
        val oca = ocaSimple.copy(overlays = overlays, captureBases = ocaSimple.captureBases)
        assertNull(oca.getAttributeForJsonPath(jsonPath = "$.foobar.firstname"))
    }

    @Test
    fun `OcaBundle getAttributeForJsonPath with array wildcard path returns attributes`() = runTest {
        val result = ocaNested.getAttributeForJsonPath("$.pets[*].name")

        assertEquals(ATTRIBUTE_KEY_NAME, result?.name)
    }

    @ParameterizedTest
    @ValueSource(
        strings = ["", "$", "$.", "invalid", "$.invalid", "$..", "$.*", "$[*]", "$..*"]
    )
    fun `OcaBundle getAttributeForJsonPath with invalid path returns null`(path: String) = runTest {
        assertNull(ocaNested.getAttributeForJsonPath(path))
    }
}
