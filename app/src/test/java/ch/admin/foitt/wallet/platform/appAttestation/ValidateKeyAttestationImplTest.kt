package ch.admin.foitt.wallet.platform.appAttestation

import ch.admin.foitt.openid4vc.domain.model.jwt.Jwt
import ch.admin.foitt.openid4vc.domain.model.keyBinding.Jwk
import ch.admin.foitt.openid4vc.domain.model.vcSdJwt.VcSdJwtError
import ch.admin.foitt.openid4vc.domain.usecase.VerifyJwtSignature
import ch.admin.foitt.wallet.platform.appAttestation.domain.model.KeyAttestation
import ch.admin.foitt.wallet.platform.appAttestation.domain.model.KeyAttestationJwt
import ch.admin.foitt.wallet.platform.appAttestation.domain.usecase.ValidateKeyAttestation
import ch.admin.foitt.wallet.platform.appAttestation.domain.usecase.implementation.ValidateKeyAttestationImpl
import ch.admin.foitt.wallet.platform.appAttestation.mock.KeyAttestationMocks
import ch.admin.foitt.wallet.platform.environmentSetup.domain.repository.EnvironmentSetupRepository
import ch.admin.foitt.wallet.util.SafeJsonTestInstance
import ch.admin.foitt.wallet.util.assertErr
import ch.admin.foitt.wallet.util.assertOk
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.getOrThrow
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import io.mockk.mockkConstructor
import io.mockk.spyk
import io.mockk.unmockkAll
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.Instant

class ValidateKeyAttestationImplTest {

    @MockK
    private lateinit var mockEnvironmentSetupRepository: EnvironmentSetupRepository

    @MockK
    private lateinit var mockVerifyJwtSignature: VerifyJwtSignature

    private var safeJson = spyk(SafeJsonTestInstance.safeJson)

    private val keyStoreAlias = "keyStoreAlias"
    private val keyAttestionJwt = Jwt(KeyAttestationMocks.jwtAttestation01.value)
    private val keyAttestation = KeyAttestation(keyStoreAlias, keyAttestionJwt)
    private val jwk = Jwk.fromEcKey(KeyAttestationMocks.jwkEcP256_01, null).getOrThrow()
    private val issuerDid = "Did"

    private val attestationJwt = KeyAttestationMocks.jwtAttestation01

    private lateinit var useCase: ValidateKeyAttestation

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        mockkConstructor(Jwt::class)
        mockkConstructor(Jwk::class)

        useCase = ValidateKeyAttestationImpl(
            environmentSetupRepo = mockEnvironmentSetupRepository,
            verifyJwtSignature = mockVerifyJwtSignature,
            safeJson = safeJson,
        )

        coEvery { anyConstructed<Jwt>().iss } returns issuerDid
        coEvery { anyConstructed<Jwt>().keyId } returns "$issuerDid#key01"
        coEvery { anyConstructed<Jwt>().expInstant } returns Instant.MAX
        coEvery { mockEnvironmentSetupRepository.attestationsServiceTrustedDids } returns listOf(issuerDid)
        coEvery { mockVerifyJwtSignature(any(), any(), any()) } returns Ok(Unit)
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `valid key attestation passes validation`() = runTest {
        val result = useCase(keyStoreAlias, jwk, attestationJwt)

        val attestation = result.assertOk()

        assertEquals(keyAttestation.keyStoreAlias, attestation.keyStoreAlias)
        assertEquals(keyAttestation.attestation.rawJwt, attestation.attestation.rawJwt)
    }

    @Test
    fun `invalid Jwt fails validation`() = runTest {
        val wrongJwt = KeyAttestationJwt("wrongJwt")

        val result = useCase(keyStoreAlias, jwk, wrongJwt)

        result.assertErr()

        coVerify(exactly = 0) {
            anyConstructed<Jwt>().iss
            mockEnvironmentSetupRepository.attestationsServiceTrustedDids
            mockVerifyJwtSignature.invoke(any(), any(), any())
        }
    }

    @Test
    fun `missing issuer fails validation`() = runTest {
        coEvery { anyConstructed<Jwt>().iss } returns null

        val result = useCase(keyStoreAlias, jwk, attestationJwt)

        result.assertErr()

        coVerify(exactly = 1) { anyConstructed<Jwt>().iss }

        coVerify(exactly = 0) {
            mockEnvironmentSetupRepository.attestationsServiceTrustedDids
            mockVerifyJwtSignature.invoke(any(), any(), any())
        }
    }

    @Test
    fun `unexpected did fails validation`() = runTest {
        coEvery { anyConstructed<Jwt>().iss } returns "otherDid"
        val result = useCase(keyStoreAlias, jwk, attestationJwt)

        result.assertErr()

        coVerify(exactly = 1) {
            anyConstructed<Jwt>().iss
            mockEnvironmentSetupRepository.attestationsServiceTrustedDids
        }

        coVerify(exactly = 0) {
            mockVerifyJwtSignature.invoke(any(), any(), any())
        }
    }

    @Test
    fun `missing kid fails validation`() = runTest {
        coEvery { anyConstructed<Jwt>().keyId } returns null
        val result = useCase(keyStoreAlias, jwk, attestationJwt)

        result.assertErr()

        coVerify(exactly = 1) {
            anyConstructed<Jwt>().keyId
        }

        coVerify(exactly = 0) {
            mockVerifyJwtSignature.invoke(any(), any(), any())
        }
    }

    @Test
    fun `unexpected kid fails validation`() = runTest {
        coEvery { anyConstructed<Jwt>().keyId } returns "kid#key01"
        val result = useCase(keyStoreAlias, jwk, attestationJwt)

        result.assertErr()

        coVerify(exactly = 1) {
            anyConstructed<Jwt>().keyId
        }

        coVerify(exactly = 0) {
            mockVerifyJwtSignature.invoke(any(), any(), any())
        }
    }

    @Test
    fun `failed signature verification fails validation`() = runTest {
        coEvery { mockVerifyJwtSignature.invoke(any(), any(), any()) } returns Err(VcSdJwtError.IssuerValidationFailed)

        val result = useCase(keyStoreAlias, jwk, attestationJwt)

        result.assertErr()

        coVerify(exactly = 1) {
            mockVerifyJwtSignature.invoke(any(), any(), any())
        }
    }

    @Test
    fun `unsupported algorithm fails validation`() = runTest {
        coEvery { anyConstructed<Jwt>().algorithm } returns "unsupportedAlgorithm"

        val result = useCase(keyStoreAlias, jwk, attestationJwt)

        result.assertErr()

        coVerify(atLeast = 1) {
            anyConstructed<Jwt>().algorithm
        }

        coVerify(exactly = 0) {
            mockVerifyJwtSignature.invoke(any(), any(), any())
        }
    }

    @Test
    fun `unsupported attestation type fails validation`() = runTest {
        coEvery { anyConstructed<Jwt>().type } returns "unsupportedType"

        val result = useCase(keyStoreAlias, jwk, attestationJwt)

        result.assertErr()

        coVerify(exactly = 1) {
            anyConstructed<Jwt>().type
        }

        coVerify(exactly = 0) {
            mockVerifyJwtSignature.invoke(any(), any(), any())
        }
    }

    @Test
    fun `missing iat fails validation`() = runTest {
        coEvery { anyConstructed<Jwt>().issuedAt } returns null

        val result = useCase(keyStoreAlias, jwk, attestationJwt)

        result.assertErr()

        coVerify(exactly = 1) {
            anyConstructed<Jwt>().issuedAt
        }
    }

    @Test
    fun `missing exp fails validation`() = runTest {
        coEvery { anyConstructed<Jwt>().expInstant } returns null

        val result = useCase(keyStoreAlias, jwk, attestationJwt)

        result.assertErr()

        coVerify(exactly = 1) {
            anyConstructed<Jwt>().expInstant
        }
    }

    @Test
    fun `expired exp fails validation`() = runTest {
        coEvery { anyConstructed<Jwt>().expInstant } returns Instant.ofEpochSecond(0)

        val result = useCase(keyStoreAlias, jwk, attestationJwt)

        result.assertErr()

        coVerify(exactly = 1) {
            anyConstructed<Jwt>().expInstant
        }
    }

    @Test
    fun `missing attested keys field fails validation`() = runTest {
        coEvery { anyConstructed<Jwt>().payloadJson } returns JsonObject(mapOf())

        val result = useCase(keyStoreAlias, jwk, attestationJwt)

        result.assertErr()

        coVerify(exactly = 1) {
            anyConstructed<Jwt>().payloadJson
        }

        coVerify(exactly = 0) {
            safeJson.safeDecodeElementTo<Jwk>(any())
        }
    }

    @Test
    fun `empty attested keys fails validation`() = runTest {
        coEvery { anyConstructed<Jwt>().payloadJson } returns JsonObject(
            mapOf(
                "attested_keys" to JsonArray(listOf())
            )
        )

        val result = useCase(keyStoreAlias, jwk, attestationJwt)

        result.assertErr()

        coVerify(exactly = 1) {
            anyConstructed<Jwt>().payloadJson
        }
        coVerify(exactly = 0) {
            safeJson.safeDecodeElementTo<Jwk>(any())
        }
    }

    @Test
    fun `attested key with missing parameters fails validation`() = runTest {
        val jwkString = JsonObject(
            mapOf(
                "z" to JsonPrimitive("x"),
                "y" to JsonPrimitive("x"),
                "crv" to JsonPrimitive("x"),
                "kty" to JsonPrimitive("x"),
            )
        )

        coEvery { anyConstructed<Jwt>().payloadJson } returns JsonObject(
            mapOf(
                "attested_keys" to JsonArray(listOf(jwkString))
            )
        )

        val result = useCase(keyStoreAlias, jwk, attestationJwt)

        result.assertErr()

        coVerify(exactly = 1) {
            anyConstructed<Jwt>().payloadJson
        }
    }

    @Test
    fun `attested key different from original fails validation`() = runTest {
        coEvery { safeJson.safeDecodeElementTo<Jwk>(any()) } returns Ok(
            Jwk.fromEcKey(KeyAttestationMocks.jwkEcP256_02, null).getOrThrow().copy(x = "x")
        )

        val result = useCase(keyStoreAlias, jwk, attestationJwt)

        result.assertErr()

        coVerify(exactly = 1) {
            safeJson.safeDecodeElementTo<Jwk>(any())
        }
    }

    @Test
    fun `missing key storage value fails validation`() = runTest {
        val result = useCase(keyStoreAlias, jwk, KeyAttestationMocks.jwtAttestation02NoStorage)
        result.assertErr()
    }

    @Test
    fun `attested key with unknown key storage value fails validation`() = runTest {
        val result = useCase(keyStoreAlias, jwk, KeyAttestationMocks.jwtAttestation03UnknownStorage)
        result.assertErr()
    }
}
