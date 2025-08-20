package ch.admin.foitt.openid4vc.domain.usecase.implementation

import android.annotation.SuppressLint
import ch.admin.foitt.openid4vc.domain.model.credentialoffer.CredentialOfferError
import ch.admin.foitt.openid4vc.domain.model.credentialoffer.JWSKeyPair
import ch.admin.foitt.openid4vc.domain.model.credentialoffer.metadata.CredentialFormat
import ch.admin.foitt.openid4vc.domain.model.keyBinding.KeyBindingType
import ch.admin.foitt.openid4vc.domain.repository.CredentialOfferRepository
import ch.admin.foitt.openid4vc.domain.usecase.CreateCredentialRequestProofJwt
import ch.admin.foitt.openid4vc.domain.usecase.DeleteKeyPair
import ch.admin.foitt.openid4vc.domain.usecase.FetchVerifiableCredential
import ch.admin.foitt.openid4vc.domain.usecase.implementation.mock.MockCredentialOffer.jwtProof
import ch.admin.foitt.openid4vc.domain.usecase.implementation.mock.MockCredentialOffer.offerWithPreAuthorizedCode
import ch.admin.foitt.openid4vc.domain.usecase.implementation.mock.MockCredentialOffer.offerWithoutPreAuthorizedCode
import ch.admin.foitt.openid4vc.domain.usecase.implementation.mock.MockCredentialOffer.validCredentialResponse
import ch.admin.foitt.openid4vc.domain.usecase.implementation.mock.MockCredentialOffer.validIssuerConfig
import ch.admin.foitt.openid4vc.domain.usecase.implementation.mock.MockCredentialOffer.validIssuerCredentialInfo
import ch.admin.foitt.openid4vc.domain.usecase.implementation.mock.MockCredentialOffer.validTokenResponse
import ch.admin.foitt.openid4vc.domain.usecase.implementation.mock.MockCredentialOffer.verifiableCredentialParamsHardwareBinding
import ch.admin.foitt.openid4vc.domain.usecase.implementation.mock.MockCredentialOffer.verifiableCredentialParamsSoftwareBinding
import ch.admin.foitt.openid4vc.domain.usecase.implementation.mock.MockCredentialOffer.verifiableCredentialParamsWithoutBinding
import ch.admin.foitt.openid4vc.domain.usecase.implementation.mock.MockIssuerCredentialConfiguration.vcSdJwtCredentialConfiguration
import ch.admin.foitt.openid4vc.domain.usecase.implementation.mock.MockKeyPairs.VALID_KEY_PAIR_HARDWARE
import ch.admin.foitt.openid4vc.domain.usecase.implementation.mock.MockKeyPairs.VALID_KEY_PAIR_SOFTWARE
import ch.admin.foitt.openid4vc.util.assertErrorType
import ch.admin.foitt.openid4vc.util.assertOk
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import io.mockk.MockKAnnotations
import io.mockk.Ordering
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import io.mockk.unmockkAll
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class FetchVerifiableCredentialImplTest {

    @MockK
    private lateinit var mockCredentialOfferRepository: CredentialOfferRepository

    @MockK
    private lateinit var mockCreateCredentialRequestProofJwt: CreateCredentialRequestProofJwt

    @MockK
    private lateinit var mockDeleteKeyPair: DeleteKeyPair

    private lateinit var fetchCredentialUseCase: FetchVerifiableCredential

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)

        initDefaultMocks()

        fetchCredentialUseCase = FetchVerifiableCredentialImpl(
            mockCredentialOfferRepository,
            mockCreateCredentialRequestProofJwt,
            mockDeleteKeyPair,
        )
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `when credential has software key binding it returns a VerifiableCredential with a software key binding`() = runTest {
        val result = fetchCredentialUseCase(
            verifiableCredentialParams = verifiableCredentialParamsSoftwareBinding,
            keyPair = VALID_KEY_PAIR_SOFTWARE,
            attestationJwt = null,
        ).assertOk()

        assertEquals(CredentialFormat.VC_SD_JWT, result.format)
        assertEquals("credential", result.credential)
        assertEquals(VALID_KEY_PAIR_SOFTWARE.keyId, result.keyBinding?.identifier)
        assertEquals(VALID_KEY_PAIR_SOFTWARE.algorithm, result.keyBinding?.algorithm)
        assertEquals(KeyBindingType.SOFTWARE, result.keyBinding?.bindingType)
        assertNotNull(result.keyBinding?.publicKey)
        assertNotNull(result.keyBinding?.privateKey)

        verifySuccessCalls(VALID_KEY_PAIR_SOFTWARE)
    }

    @SuppressLint("CheckResult")
    @Test
    fun `when credential has hardware key binding it returns a VerifiableCredential with a hardware key binding`() = runTest {
        val result = fetchCredentialUseCase(
            verifiableCredentialParams = verifiableCredentialParamsHardwareBinding,
            keyPair = VALID_KEY_PAIR_HARDWARE,
            attestationJwt = null,
        ).assertOk()

        assertEquals(CredentialFormat.VC_SD_JWT, result.format)
        assertEquals("credential", result.credential)
        assertEquals(VALID_KEY_PAIR_HARDWARE.keyId, result.keyBinding?.identifier)
        assertEquals(VALID_KEY_PAIR_HARDWARE.algorithm, result.keyBinding?.algorithm)
        assertEquals(KeyBindingType.HARDWARE, result.keyBinding?.bindingType)
        assertNull(result.keyBinding?.publicKey)
        assertNull(result.keyBinding?.privateKey)

        verifySuccessCalls(VALID_KEY_PAIR_HARDWARE)
    }

    @SuppressLint("CheckResult")
    @Test
    fun `when credential has no key binding (empty proof type) it returns a VerifiableCredential without key binding`() = runTest {
        val result = fetchCredentialUseCase(
            verifiableCredentialParams = verifiableCredentialParamsWithoutBinding,
            keyPair = null,
            attestationJwt = null,
        ).assertOk()

        coVerify(exactly = 0) {
            mockCreateCredentialRequestProofJwt(any(), any(), any(), any())
        }

        assertEquals(CredentialFormat.VC_SD_JWT, result.format)
        assertEquals("credential", result.credential)
        assertEquals(null, result.keyBinding)
    }

    @SuppressLint("CheckResult")
    @Test
    fun `when fetching the token fails return an invalid credential offer error`() = runTest {
        coEvery {
            mockCredentialOfferRepository.fetchAccessToken(any(), any())
        } returns Err(CredentialOfferError.InvalidCredentialOffer)

        fetchCredentialUseCase(
            verifiableCredentialParams = verifiableCredentialParamsSoftwareBinding,
            keyPair = VALID_KEY_PAIR_SOFTWARE,
            attestationJwt = null,
        ).assertErrorType(CredentialOfferError.InvalidCredentialOffer::class)

        coVerify(exactly = 0) {
            mockDeleteKeyPair(VALID_KEY_PAIR_SOFTWARE.keyId)
        }
    }

    @SuppressLint("CheckResult")
    @Test
    fun `when fetching the token fails return an invalid credential offer error and delete the key pair for a hardware bound credential`() = runTest {
        coEvery {
            mockCredentialOfferRepository.fetchAccessToken(any(), any())
        } returns Err(CredentialOfferError.InvalidCredentialOffer)

        fetchCredentialUseCase(
            verifiableCredentialParams = verifiableCredentialParamsHardwareBinding,
            keyPair = VALID_KEY_PAIR_HARDWARE,
            attestationJwt = null,
        ).assertErrorType(CredentialOfferError.InvalidCredentialOffer::class)

        coVerify(exactly = 1) {
            mockDeleteKeyPair(VALID_KEY_PAIR_HARDWARE.keyId)
        }
    }

    @SuppressLint("CheckResult")
    @Test
    fun `credential offer without pre-authorized code should return an unsupported grant type error, token not fetched`() = runTest {
        fetchCredentialUseCase(
            verifiableCredentialParams = verifiableCredentialParamsSoftwareBinding.copy(grants = offerWithoutPreAuthorizedCode.grants),
            keyPair = VALID_KEY_PAIR_SOFTWARE,
            attestationJwt = null,
        ).assertErrorType(CredentialOfferError.UnsupportedGrantType::class)

        coVerify(exactly = 0) {
            mockCredentialOfferRepository.fetchAccessToken(any(), any())
        }
    }

    @SuppressLint("CheckResult")
    @Test
    fun `when creating the CredentialRequestProof fails return an error`() = runTest {
        coEvery {
            mockCreateCredentialRequestProofJwt(any(), any(), any(), any())
        } returns Err(CredentialOfferError.UnsupportedCryptographicSuite)

        fetchCredentialUseCase(
            verifiableCredentialParams = verifiableCredentialParamsSoftwareBinding,
            keyPair = VALID_KEY_PAIR_SOFTWARE,
            attestationJwt = null,
        ).assertErrorType(CredentialOfferError.UnsupportedCryptographicSuite::class)

        coVerify(exactly = 0) {
            mockDeleteKeyPair(VALID_KEY_PAIR_SOFTWARE.keyId)
        }
    }

    @SuppressLint("CheckResult")
    @Test
    fun `when creating the CredentialRequestProof fails return error and delete the key pair`() = runTest {
        coEvery {
            mockCreateCredentialRequestProofJwt(any(), any(), any(), any())
        } returns Err(CredentialOfferError.UnsupportedCryptographicSuite)

        fetchCredentialUseCase(
            verifiableCredentialParams = verifiableCredentialParamsHardwareBinding,
            keyPair = VALID_KEY_PAIR_HARDWARE,
            attestationJwt = null,
        ).assertErrorType(CredentialOfferError.UnsupportedCryptographicSuite::class)

        coVerify(exactly = 1) {
            mockDeleteKeyPair(VALID_KEY_PAIR_HARDWARE.keyId)
        }
    }

    private fun initDefaultMocks() {
        coEvery {
            mockCreateCredentialRequestProofJwt(any(), any(), any(), any())
        } returns Ok(jwtProof)

        coEvery {
            mockCredentialOfferRepository.getIssuerCredentialInfo(offerWithPreAuthorizedCode.credentialIssuer)
        } returns Ok(validIssuerCredentialInfo)

        coEvery {
            mockCredentialOfferRepository.fetchIssuerConfiguration(offerWithPreAuthorizedCode.credentialIssuer)
        } returns Ok(validIssuerConfig)

        coEvery {
            mockCredentialOfferRepository.fetchAccessToken(validIssuerConfig.tokenEndpoint, any())
        } returns Ok(validTokenResponse)

        coEvery {
            mockCredentialOfferRepository.fetchCredential(
                issuerEndpoint = validIssuerCredentialInfo.credentialEndpoint,
                credentialConfiguration = any(),
                proof = any(),
                accessToken = any()
            )
        } returns Ok(validCredentialResponse)

        coEvery {
            mockDeleteKeyPair(any())
        } returns Ok(Unit)
    }

    @SuppressLint("CheckResult")
    private fun verifySuccessCalls(keyPair: JWSKeyPair?) {
        coVerify(ordering = Ordering.SEQUENCE) {
            mockCredentialOfferRepository.fetchAccessToken(
                tokenEndpoint = validIssuerConfig.tokenEndpoint,
                preAuthorizedCode = offerWithPreAuthorizedCode.grants.preAuthorizedCode!!.preAuthorizedCode
            )
            keyPair?.let {
                mockCreateCredentialRequestProofJwt(
                    keyPair = it,
                    attestationJwt = null,
                    issuer = offerWithPreAuthorizedCode.credentialIssuer,
                    cNonce = validTokenResponse.cNonce
                )
            }
            mockCredentialOfferRepository.fetchCredential(
                issuerEndpoint = validIssuerCredentialInfo.credentialEndpoint,
                credentialConfiguration = vcSdJwtCredentialConfiguration,
                proof = jwtProof,
                accessToken = validTokenResponse.accessToken
            )
        }

        coVerify(exactly = 0) {
            mockDeleteKeyPair(any())
        }
    }
}
