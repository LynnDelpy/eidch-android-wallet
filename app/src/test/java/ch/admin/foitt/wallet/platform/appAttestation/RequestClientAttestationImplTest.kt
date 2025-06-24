package ch.admin.foitt.wallet.platform.appAttestation

import android.annotation.SuppressLint
import ch.admin.foitt.openid4vc.domain.model.JwkError
import ch.admin.foitt.openid4vc.domain.model.KeyPairError
import ch.admin.foitt.openid4vc.domain.model.credentialoffer.JWSKeyPair
import ch.admin.foitt.openid4vc.domain.model.credentialoffer.metadata.SigningAlgorithm
import ch.admin.foitt.openid4vc.domain.model.jwt.Jwt
import ch.admin.foitt.openid4vc.domain.usecase.CreateJWSKeyPair
import ch.admin.foitt.openid4vc.domain.usecase.CreateJwk
import ch.admin.foitt.wallet.platform.appAttestation.domain.model.AttestationChallengeResponse
import ch.admin.foitt.wallet.platform.appAttestation.domain.model.AttestationError
import ch.admin.foitt.wallet.platform.appAttestation.domain.model.ClientAttestation
import ch.admin.foitt.wallet.platform.appAttestation.domain.model.ClientAttestationResponse
import ch.admin.foitt.wallet.platform.appAttestation.domain.model.IntegrityToken
import ch.admin.foitt.wallet.platform.appAttestation.domain.repository.AppAttestationRepository
import ch.admin.foitt.wallet.platform.appAttestation.domain.repository.AppIntegrityRepository
import ch.admin.foitt.wallet.platform.appAttestation.domain.usecase.RequestClientAttestation
import ch.admin.foitt.wallet.platform.appAttestation.domain.usecase.ValidateClientAttestation
import ch.admin.foitt.wallet.platform.appAttestation.domain.usecase.implementation.RequestClientAttestationImpl
import ch.admin.foitt.wallet.platform.appAttestation.domain.util.getBase64CertificateChain
import ch.admin.foitt.wallet.platform.appAttestation.mock.ClientAttestationMocks
import ch.admin.foitt.wallet.platform.appAttestation.mock.KeyAttestationMocks
import ch.admin.foitt.wallet.util.assertErrorType
import ch.admin.foitt.wallet.util.assertOk
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.coVerifyOrder
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.runs
import io.mockk.unmockkAll
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.security.KeyPair
import java.security.Signature

class RequestClientAttestationImplTest {
    private val challengeResponse = AttestationChallengeResponse("challenge")
    private var keyPair: KeyPair = KeyPair(mockk(), mockk())
    private val keyStoreAlias = "keyAlias"
    private val signingAlgorithm = SigningAlgorithm.ES256
    private val jwsKeyPair = JWSKeyPair(signingAlgorithm, keyPair, keyStoreAlias)
    private val jwk = KeyAttestationMocks.jwkEcP256_02
    private val clientAttestationRawJwt = ClientAttestationMocks.jwtAttestation01
    private val clientAttestationJwt = Jwt(clientAttestationRawJwt)
    private val clientAttestation = ClientAttestation(keyStoreAlias, clientAttestationJwt)
    private val integrityToken = IntegrityToken("integrityToken")
    private val signedChallenge = byteArrayOf(1, 2, 3)

    @MockK
    private lateinit var mockAppAttestationRepository: AppAttestationRepository

    @MockK
    private lateinit var mockAppIntegrityRepository: AppIntegrityRepository

    @MockK
    private lateinit var mockCreateJWSKeyPair: CreateJWSKeyPair

    @MockK
    private lateinit var mockCreateJwk: CreateJwk

    @MockK
    private lateinit var mockValidateClientAttestation: ValidateClientAttestation

    @MockK
    private lateinit var mockSignature: Signature

    private lateinit var useCase: RequestClientAttestation

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)

        useCase = RequestClientAttestationImpl(
            appAttestationRepository = mockAppAttestationRepository,
            appIntegrityRepository = mockAppIntegrityRepository,
            validateClientAttestation = mockValidateClientAttestation,
            createJWSKeyPair = mockCreateJWSKeyPair,
            createJwk = mockCreateJwk,
        )

        coEvery { mockAppAttestationRepository.fetchChallenge() } returns Ok(challengeResponse)

        coEvery { mockCreateJWSKeyPair(any(), any(), any()) } returns Ok(jwsKeyPair)

        coEvery { mockCreateJwk(any(), any(), any()) } returns Ok(jwk)

        mockkStatic(JWSKeyPair::getBase64CertificateChain)
        coEvery { any<JWSKeyPair>().getBase64CertificateChain() } returns Ok(listOf("base64Certificate"))

        coEvery { mockAppAttestationRepository.fetchClientAttestation(any(), any()) } returns Ok(
            ClientAttestationResponse(
                clientAttestationRawJwt
            )
        )

        coEvery { mockAppIntegrityRepository.fetchIntegrityToken(any()) } returns Ok(integrityToken)
        coEvery { mockValidateClientAttestation(any(), any(), any()) } returns Ok(clientAttestation)
        coEvery { mockAppAttestationRepository.saveClientAttestation(clientAttestation) } returns Ok(0L)
        // Mock signature instance
        mockkStatic(Signature::class)
        coEvery { Signature.getInstance(any()) } returns mockSignature
        coEvery { mockSignature.initSign(any()) } just runs
        coEvery { mockSignature.update(any<ByteArray>()) } just runs
        coEvery { mockSignature.sign() } returns signedChallenge
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @SuppressLint("CheckResult")
    @Test
    fun `A success saves a valid ClientAttestation and follows specific steps`() = runTest {
        val result = useCase(signingAlgorithm)
        result.assertOk()

        coVerifyOrder {
            mockAppAttestationRepository.fetchChallenge()
            mockCreateJWSKeyPair.invoke(any(), any(), any())
            mockCreateJwk.invoke(keyPair = keyPair, any(), any())
            mockAppIntegrityRepository.fetchIntegrityToken(any())
            mockAppAttestationRepository.fetchClientAttestation(integrityToken = integrityToken, any())
            mockValidateClientAttestation.invoke(any(), any(), any())
            mockAppAttestationRepository.saveClientAttestation(clientAttestation)
        }
    }

    @Test
    fun `A network failure while getting the challenge is propagated`() = runTest {
        coEvery { mockAppAttestationRepository.fetchChallenge() } returns Err(AttestationError.NetworkError)
        val result = useCase()
        result.assertErrorType(AttestationError.NetworkError::class)

        coVerify(exactly = 1) {
            mockAppAttestationRepository.fetchChallenge()
        }

        coVerify(exactly = 0) {
            mockCreateJWSKeyPair(any(), any(), any())
        }
    }

    @Test
    fun `A JWSKeyPair creation failure is propagated`() = runTest {
        val exception = Exception("myException")
        coEvery { mockCreateJWSKeyPair.invoke(any(), any(), any()) } returns Err(KeyPairError.Unexpected(exception))
        val result = useCase()
        val error = result.assertErrorType(AttestationError.Unexpected::class)
        assertEquals(exception, error.throwable)
    }

    @Test
    fun `A Jwk string creation failure is propagated`() = runTest {
        val exception = Exception("myException")
        coEvery { mockCreateJwk.invoke(any(), any(), any()) } returns Err(JwkError.Unexpected(exception))
        val result = useCase()
        val error = result.assertErrorType(AttestationError.Unexpected::class)
        assertEquals(exception, error.throwable)
    }

    @Test
    fun `A certificate creation failure is propagated`() = runTest {
        val exception = Exception("myException")
        coEvery { any<JWSKeyPair>().getBase64CertificateChain() } returns Err(exception)
        val result = useCase()
        val error = result.assertErrorType(AttestationError.Unexpected::class)
        assertEquals(exception, error.throwable)
    }

    @Test
    fun `An integrity token fetching failure is propagated`() = runTest {
        val exception = Exception("myException")
        coEvery { mockAppIntegrityRepository.fetchIntegrityToken(any()) } returns Err(AttestationError.Unexpected(exception))
        val result = useCase()
        val error = result.assertErrorType(AttestationError.Unexpected::class)
        assertEquals(exception, error.throwable)
    }

    @Test
    fun `A client attestation fetching failure is propagated`() = runTest {
        val exception = Exception("myException")
        coEvery {
            mockAppAttestationRepository.fetchClientAttestation(any(), any())
        } returns Err(AttestationError.Unexpected(exception))
        val result = useCase()
        val error = result.assertErrorType(AttestationError.Unexpected::class)
        assertEquals(exception, error.throwable)
    }

    @Test
    fun `A client attestation validation failure is propagated`() = runTest {
        val exception = Exception("myException")
        coEvery {
            mockValidateClientAttestation.invoke(any(), any(), any())
        } returns Err(AttestationError.Unexpected(exception))
        val result = useCase()
        val error = result.assertErrorType(AttestationError.Unexpected::class)
        assertEquals(exception, error.throwable)
    }

    @Test
    fun `A client attestation saving failure is propagated`() = runTest {
        val exception = Exception("myException")
        coEvery {
            mockAppAttestationRepository.saveClientAttestation(any())
        } returns Err(AttestationError.Unexpected(exception))
        val result = useCase()
        val error = result.assertErrorType(AttestationError.Unexpected::class)
        assertEquals(exception, error.throwable)
    }
}
