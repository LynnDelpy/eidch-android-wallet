package ch.admin.foitt.wallet.platform.trustRegistry

import ch.admin.foitt.openid4vc.domain.model.vcSdJwt.VcSdJwtError
import ch.admin.foitt.openid4vc.domain.usecase.VerifyJwtSignature
import ch.admin.foitt.wallet.platform.credentialStatus.domain.model.CredentialStatusError
import ch.admin.foitt.wallet.platform.credentialStatus.domain.usecase.FetchCredentialStatus
import ch.admin.foitt.wallet.platform.database.domain.model.CredentialStatus
import ch.admin.foitt.wallet.platform.environmentSetup.domain.repository.EnvironmentSetupRepository
import ch.admin.foitt.wallet.platform.trustRegistry.domain.usecase.ValidateTrustStatement
import ch.admin.foitt.wallet.platform.trustRegistry.domain.usecase.implementation.ValidateTrustStatementImpl
import ch.admin.foitt.wallet.util.SafeJsonTestInstance
import ch.admin.foitt.wallet.util.assertErr
import ch.admin.foitt.wallet.util.assertOk
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import io.mockk.unmockkAll
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class ValidateTrustStatementImplTest {

    @MockK
    private lateinit var mockEnvironmentSetup: EnvironmentSetupRepository

    @MockK
    private lateinit var mockVerifyJwtSignature: VerifyJwtSignature

    @MockK
    private lateinit var mockFetchCredentialStatus: FetchCredentialStatus

    private val testSafeJson = SafeJsonTestInstance.safeJson

    private lateinit var useCase: ValidateTrustStatement

    @BeforeEach
    fun setup() {
        MockKAnnotations.init(this)
        useCase = ValidateTrustStatementImpl(
            environmentSetupRepo = mockEnvironmentSetup,
            verifyJwtSignature = mockVerifyJwtSignature,
            safeJson = testSafeJson,
            fetchCredentialStatus = mockFetchCredentialStatus
        )

        coEvery {
            mockEnvironmentSetup.trustRegistryTrustedDids
        } returns trustedDids

        coEvery { mockVerifyJwtSignature.invoke(did = any(), kid = any(), jwt = any()) } returns Ok(Unit)
        coEvery {
            mockFetchCredentialStatus.invoke(credentialIssuer = any(), properties = any())
        } returns Ok(CredentialStatus.VALID)
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `A valid trust statement pass validation`(): Unit = runTest {
        useCase(VALID_TRUST_STATEMENT, VALID_ACTOR_DID).assertOk()

        coVerify(exactly = 1) {
            mockEnvironmentSetup.trustRegistryTrustedDids
            mockVerifyJwtSignature.invoke(did = any(), kid = any(), jwt = any())
        }
    }

    @Test
    fun `A trust statement not whitelisted fails validation`(): Unit = runTest {
        coEvery { mockEnvironmentSetup.trustRegistryTrustedDids } returns listOf("did:twd:bbb")

        useCase(VALID_TRUST_STATEMENT, VALID_ACTOR_DID).assertErr()

        coVerify(exactly = 1) {
            mockEnvironmentSetup.trustRegistryTrustedDids
        }
        coVerify(exactly = 0) {
            mockVerifyJwtSignature.invoke(did = any(), kid = any(), jwt = any())
        }
    }

    @Test
    fun `A trust statement declaring the wrong type fails validation`(): Unit = runTest {
        useCase(WRONG_TYPE_TRUST_STATEMENT, VALID_ACTOR_DID).assertErr()
    }

    @Test
    fun `A trust statement declaring the wrong algorithm fails validation`(): Unit = runTest {
        useCase(WRONG_ALGO_TRUST_STATEMENT, VALID_ACTOR_DID).assertErr()
    }

    @Test
    fun `A trust statement with an invalid signature fails validation`(): Unit = runTest {
        coEvery {
            mockVerifyJwtSignature.invoke(did = any(), kid = any(), jwt = any())
        } returns Err(VcSdJwtError.InvalidJwt)

        useCase(VALID_TRUST_STATEMENT, VALID_ACTOR_DID).assertErr()

        coVerify(exactly = 1) {
            mockEnvironmentSetup.trustRegistryTrustedDids
            mockVerifyJwtSignature.invoke(did = any(), kid = any(), jwt = any())
        }
    }

    @ParameterizedTest
    @ValueSource(
        strings = [
            MISSING_IAT,
            MISSING_EXP,
            MISSING_NBF,
            MISSING_ISS,
            MISSING_SUB
        ]
    )
    fun `A trust statement missing a mandatory reserved claim fails validation`(sdJwt: String): Unit = runTest {
        useCase(sdJwt, VALID_ACTOR_DID).assertErr()
    }

    @Test
    fun `A trust statement where the subject does not match the actor did fails validation`() = runTest {
        useCase(INVALID_SUBJECT, VALID_ACTOR_DID).assertErr()
        useCase(VALID_TRUST_STATEMENT, "invalid actor did").assertErr()
    }

    @Test
    fun `A trust statement with an invalid validity fails validation`(): Unit = runTest {
        useCase(EXPIRED, VALID_ACTOR_DID).assertErr()
    }

    @Test
    fun `A trust statement with an unsupported vct claim value fails validation`(): Unit = runTest {
        useCase(WRONG_VCT_VALUE, VALID_ACTOR_DID).assertErr()
    }

    @ParameterizedTest
    @ValueSource(
        strings = [
            MISSING_ORG_NAME,
            MISSING_PREF_LANG,
        ]
    )
    fun `A trust statement missing a mandatory claim fails validation`(sdJwt: String): Unit = runTest {
        useCase(sdJwt, VALID_ACTOR_DID).assertErr()
    }

    @Test
    fun `A trust statement with an unexpected type fails validation`(): Unit = runTest {
        useCase(WRONG_FIELD_TYPES, VALID_ACTOR_DID).assertErr()
    }

    @Test
    fun `A trust statement missing status properties fails validation`(): Unit = runTest {
        useCase(MISSING_STATUS_PROPERTIES, VALID_ACTOR_DID).assertErr()
        coVerify(exactly = 0) {
            mockFetchCredentialStatus.invoke(any(), any())
        }
    }

    @Test
    fun `A failed status list call fails validation`(): Unit = runTest {
        coEvery { mockFetchCredentialStatus.invoke(any(), any()) } returns Err(CredentialStatusError.NetworkError)
        useCase(VALID_TRUST_STATEMENT, VALID_ACTOR_DID).assertErr()
        coVerify(exactly = 1) {
            mockFetchCredentialStatus.invoke(any(), any())
        }
    }

    @Test
    fun `A trust statement with any other status than valid fails validation`(): Unit = runTest {
        coEvery { mockFetchCredentialStatus.invoke(any(), any()) } returns Ok(CredentialStatus.SUSPENDED)
        useCase(VALID_TRUST_STATEMENT, VALID_ACTOR_DID).assertErr()
        coVerify(exactly = 1) {
            mockFetchCredentialStatus.invoke(any(), any())
        }
    }

    private val trustedDids = listOf(
        "did:tdw:aaa",
        "did:tdw:abc",
    )

    companion object {
        private const val VALID_ACTOR_DID = "did:tdw:abcd"

        private const val VALID_TRUST_STATEMENT =
            "eyJhbGciOiJFUzI1NiIsInR5cCI6InZjK3NkLWp3dCIsImtpZCI6ImRpZDp0ZHc6YWJjI2tleTAxIn0.eyJpc3MiOiJkaWQ6dGR3OmFiYyIsIm5iZiI6MCwiZXhwIjo5OTk5OTk5OTk5LCJpYXQiOjAsIl9zZF9hbGciOiJzaGEtMjU2Iiwic3ViIjoiZGlkOnRkdzphYmNkIiwib3JnTmFtZSI6eyJlbiI6Im9yZ05hbWUgRW4iLCJkZS1DSCI6Im9yZ05hbWUgRGUifSwicHJlZkxhbmciOiJkZSIsInZjdCI6IlRydXN0U3RhdGVtZW50TWV0YWRhdGFWMSIsImxvZ29VcmkiOnsiZW4iOiJsb2dvVXJpRW4iLCJkZSI6ImxvZ29VcmlEZSJ9LCJzdGF0dXMiOnsic3RhdHVzX2xpc3QiOnsiaWR4IjowLCJ1cmkiOiJ1cmkifX19.vKbMQpbERKJEyDGbsxJ_X0Y92ye_oxP9l0l_uZiwO9Uy2rQZe0F9EN8SFnTDQ8Qdd3Bk1aQ6eiYVGfngDcN9Ag"
        private const val WRONG_TYPE_TRUST_STATEMENT =
            "eyJhbGciOiJFUzI1NiIsInR5cCI6InNkLWp3dCIsImtpZCI6ImRpZDp0ZHc6YWJjI2tleTAxIn0.eyJpc3MiOiJkaWQ6dGR3OmFiYyIsIm5iZiI6MCwiZXhwIjo5OTk5OTk5OTk5LCJpYXQiOjAsIl9zZF9hbGciOiJzaGEtMjU2Iiwic3ViIjoiZGlkOnRkdzphYmNkIiwib3JnTmFtZSI6eyJlbiI6Im9yZ05hbWUgRW4iLCJkZS1DSCI6Im9yZ05hbWUgRGUifSwicHJlZkxhbmciOiJkZSIsInZjdCI6IlRydXN0U3RhdGVtZW50TWV0YWRhdGFWMSIsImxvZ29VcmkiOnsiZW4iOiJsb2dvVXJpRW4iLCJkZSI6ImxvZ29VcmlEZSJ9fQ.7QSBHnU2q1YEqdazQjhWND0rX_5JyUzXQ9mhW4DjLjRGreRG_F-24s49UgqPsJqQ3pjw2aJriJfG9BZJ6JJkSQ"
        private const val WRONG_ALGO_TRUST_STATEMENT =
            "eyJhbGciOiJIUzI1NiIsInR5cCI6InZjK3NkLWp3dCIsImtpZCI6ImRpZDp0ZHc6YWJjI2tleTAxIn0.eyJpc3MiOiJkaWQ6dGR3OmFiYyIsIm5iZiI6MCwiZXhwIjo5OTk5OTk5OTk5LCJpYXQiOjAsIl9zZF9hbGciOiJzaGEtMjU2Iiwic3ViIjoiZGlkOnRkdzphYmNkIiwib3JnTmFtZSI6eyJlbiI6Im9yZ05hbWUgRW4iLCJkZS1DSCI6Im9yZ05hbWUgRGUifSwicHJlZkxhbmciOiJkZSIsInZjdCI6IlRydXN0U3RhdGVtZW50TWV0YWRhdGFWMSIsImxvZ29VcmkiOnsiZW4iOiJsb2dvVXJpRW4iLCJkZSI6ImxvZ29VcmlEZSJ9fQ.kSvO__9qdqXb4hSc8y6nFMZ8CxuQAW3seMl4vIDtcjI"
        private const val MISSING_IAT =
            "eyJhbGciOiJFUzI1NiIsInR5cCI6InZjK3NkLWp3dCIsImtpZCI6ImRpZDp0ZHc6YWJjI2tleTAxIn0.eyJpc3MiOiJkaWQ6dGR3OmFiYyIsIm5iZiI6MCwiZXhwIjo5OTk5OTk5OTk5LCJfc2RfYWxnIjoic2hhLTI1NiIsInN1YiI6ImRpZDp0ZHc6YWJjZCIsIm9yZ05hbWUiOnsiZW4iOiJvcmdOYW1lIEVuIiwiZGUtQ0giOiJvcmdOYW1lIERlIn0sInByZWZMYW5nIjoiZGUiLCJ2Y3QiOiJUcnVzdFN0YXRlbWVudE1ldGFkYXRhVjEiLCJsb2dvVXJpIjp7ImVuIjoibG9nb1VyaUVuIiwiZGUiOiJsb2dvVXJpRGUifX0.NiDzA3tUhPAPp7qw5mCTTt4muAFv4lgTBND0jfI_V-KkeIW1qslFDBuMIUGx4m6nNyjGjjZ2LCILPrUbO1fgWQ"
        private const val MISSING_NBF =
            "eyJhbGciOiJFUzI1NiIsInR5cCI6InZjK3NkLWp3dCIsImtpZCI6ImRpZDp0ZHc6YWJjI2tleTAxIn0.eyJpc3MiOiJkaWQ6dGR3OmFiYyIsImV4cCI6OTk5OTk5OTk5OSwiaWF0IjowLCJfc2RfYWxnIjoic2hhLTI1NiIsInN1YiI6ImRpZDp0ZHc6YWJjZCIsIm9yZ05hbWUiOnsiZW4iOiJvcmdOYW1lIEVuIiwiZGUtQ0giOiJvcmdOYW1lIERlIn0sInByZWZMYW5nIjoiZGUiLCJ2Y3QiOiJUcnVzdFN0YXRlbWVudE1ldGFkYXRhVjEiLCJsb2dvVXJpIjp7ImVuIjoibG9nb1VyaUVuIiwiZGUiOiJsb2dvVXJpRGUifX0.HBzmUa9ISihmMXTBd6Npd3_iVAuwQIZpB6dokfbsbPRCEMNRdXMLZzpZQf-8oVVrSTc4QPJtNjOkVxWlJo1v2Q"
        private const val MISSING_EXP =
            "eyJhbGciOiJFUzI1NiIsInR5cCI6InZjK3NkLWp3dCIsImtpZCI6ImRpZDp0ZHc6YWJjI2tleTAxIn0.eyJpc3MiOiJkaWQ6dGR3OmFiYyIsIm5iZiI6MCwiaWF0IjowLCJfc2RfYWxnIjoic2hhLTI1NiIsInN1YiI6ImRpZDp0ZHc6YWJjZCIsIm9yZ05hbWUiOnsiZW4iOiJvcmdOYW1lIEVuIiwiZGUtQ0giOiJvcmdOYW1lIERlIn0sInByZWZMYW5nIjoiZGUiLCJ2Y3QiOiJUcnVzdFN0YXRlbWVudE1ldGFkYXRhVjEiLCJsb2dvVXJpIjp7ImVuIjoibG9nb1VyaUVuIiwiZGUiOiJsb2dvVXJpRGUifX0.fRh-6Rur8iM3v9Wzc-ua6qe2whqR9AEf50BHXWWr72qX4p7Y5fHbOVTZUm9vfH92bPD7j7AqkvzK3IPvK6FnZA"
        private const val MISSING_ISS =
            "eyJhbGciOiJFUzI1NiIsInR5cCI6InZjK3NkLWp3dCIsImtpZCI6ImRpZDp0ZHc6YWJjI2tleTAxIn0.eyJuYmYiOjAsImV4cCI6OTk5OTk5OTk5OSwiaWF0IjowLCJfc2RfYWxnIjoic2hhLTI1NiIsInN1YiI6ImRpZDp0ZHc6YWJjZCIsIm9yZ05hbWUiOnsiZW4iOiJvcmdOYW1lIEVuIiwiZGUtQ0giOiJvcmdOYW1lIERlIn0sInByZWZMYW5nIjoiZGUiLCJ2Y3QiOiJUcnVzdFN0YXRlbWVudE1ldGFkYXRhVjEiLCJsb2dvVXJpIjp7ImVuIjoibG9nb1VyaUVuIiwiZGUiOiJsb2dvVXJpRGUifX0.sKESDlYr_gYfVjHUL715GUbvQvbDXfqPgcUpl-ps_ktEvBujaOrYiDSxSI6jldv7fNx5zVtPP6vUQiIpOxnUtQ"
        private const val MISSING_SUB =
            "ewogICJhbGciOiJFUzI1NiIsCiAgInR5cCI6InZjK3NkLWp3dCIsCiAgImtpZCI6ImRpZDp0ZHc6YWJjI2tleTAxIgp9.ewogICJpc3MiOiJkaWQ6dGR3OmFiYyIsCiAgIm5iZiI6MCwKICAiZXhwIjo5OTk5OTk5OTk5LAogICJpYXQiOjAsCiAgIl9zZF9hbGciOiJzaGEtMjU2IiwKICAib3JnTmFtZSI6ewogICAgImVuIjoib3JnTmFtZSBFbiIsCiAgICAiZGUtQ0giOiJvcmdOYW1lIERlIgogIH0sCiAgInByZWZMYW5nIjoiZGUiLAogICJ2Y3QiOiJUcnVzdFN0YXRlbWVudE1ldGFkYXRhVjEiLAogICJsb2dvVXJpIjp7CiAgICAiZW4iOiJsb2dvVXJpRW4iLAogICAgImRlIjoibG9nb1VyaURlIgogIH0sCiAgInN0YXR1cyI6ewogICAgInN0YXR1c19saXN0Ijp7CiAgICAgICJpZHgiOjAsCiAgICAgICJ1cmkiOiJ1cmkiCiAgICB9CiAgfQp9.ZXdvZ0lDSmhiR2NpT2lKRlV6STFOaUlzQ2lBZ0luUjVjQ0k2SW5aakszTmtMV3AzZENJc0NpQWdJbXRwWkNJNkltUnBaRHAwWkhjNllXSmpJMnRsZVRBeElncDkuLmpnVjJoaDA4cUFjOTZ2TlpTZURGdE45NmMzWW9IQVhIMldBdEYtbGhJU3RHcjRsY0pWeklsRlBnY2dFenljOUVyTUJvX0tEOVhYN201NmpMaWFOcnR3"
        private const val INVALID_SUBJECT =
            "ewogICJhbGciOiJFUzI1NiIsCiAgInR5cCI6InZjK3NkLWp3dCIsCiAgImtpZCI6ImRpZDp0ZHc6YWJjI2tleTAxIgp9.ewogICJpc3MiOiJkaWQ6dGR3OmFiYyIsCiAgIm5iZiI6MCwKICAiZXhwIjo5OTk5OTk5OTk5LAogICJpYXQiOjAsCiAgIl9zZF9hbGciOiJzaGEtMjU2IiwKICAic3ViIjoiZGlkOnRkdzppbnZhbGlkIiwKICAib3JnTmFtZSI6ewogICAgImVuIjoib3JnTmFtZSBFbiIsCiAgICAiZGUtQ0giOiJvcmdOYW1lIERlIgogIH0sCiAgInByZWZMYW5nIjoiZGUiLAogICJ2Y3QiOiJUcnVzdFN0YXRlbWVudE1ldGFkYXRhVjEiLAogICJsb2dvVXJpIjp7CiAgICAiZW4iOiJsb2dvVXJpRW4iLAogICAgImRlIjoibG9nb1VyaURlIgogIH0sCiAgInN0YXR1cyI6ewogICAgInN0YXR1c19saXN0Ijp7CiAgICAgICJpZHgiOjAsCiAgICAgICJ1cmkiOiJ1cmkiCiAgICB9CiAgfQp9.ZXdvZ0lDSmhiR2NpT2lKRlV6STFOaUlzQ2lBZ0luUjVjQ0k2SW5aakszTmtMV3AzZENJc0NpQWdJbXRwWkNJNkltUnBaRHAwWkhjNllXSmpJMnRsZVRBeElncDkuLkd2M3dPdk91NGlJNXVzUk5SbE1XUGtLclFQSHNQV1FleE55LTlPUWxSMzBSSms5NXlGcERUWXBHb0QzVEl0cVZlU0dqcUZ3bU9DWEt1dkh1WGQzNWVn"
        private const val EXPIRED =
            "eyJhbGciOiJFUzI1NiIsInR5cCI6InZjK3NkLWp3dCIsImtpZCI6ImRpZDp0ZHc6YWJjI2tleTAxIn0.eyJpc3MiOiJkaWQ6dGR3OmFiYyIsIm5iZiI6MCwiZXhwIjoxLCJpYXQiOjAsIl9zZF9hbGciOiJzaGEtMjU2Iiwic3ViIjoiZGlkOnRkdzphYmNkIiwib3JnTmFtZSI6eyJlbiI6Im9yZ05hbWUgRW4iLCJkZS1DSCI6Im9yZ05hbWUgRGUifSwicHJlZkxhbmciOiJkZSIsInZjdCI6IlRydXN0U3RhdGVtZW50TWV0YWRhdGFWMSIsImxvZ29VcmkiOnsiZW4iOiJsb2dvVXJpRW4iLCJkZSI6ImxvZ29VcmlEZSJ9fQ.4rSXYbpAxBpXaGfIky_fCtU1PweXURI4-Cvea6EDLv8Kf3CBrmpoZb7pDTyhb0VEedu14rp45k-wF5R2Gzjpzw"
        private const val WRONG_VCT_VALUE =
            "eyJhbGciOiJFUzI1NiIsInR5cCI6InZjK3NkLWp3dCIsImtpZCI6ImRpZDp0ZHc6YWJjI2tleTAxIn0.eyJpc3MiOiJkaWQ6dGR3OmFiYyIsIm5iZiI6MCwiZXhwIjo5OTk5OTk5OTk5LCJpYXQiOjAsIl9zZF9hbGciOiJzaGEtMjU2Iiwic3ViIjoiZGlkOnRkdzphYmNkIiwib3JnTmFtZSI6eyJlbiI6Im9yZ05hbWUgRW4iLCJkZS1DSCI6Im9yZ05hbWUgRGUifSwicHJlZkxhbmciOiJkZSIsInZjdCI6IlNvbWVUeXBlIiwibG9nb1VyaSI6eyJlbiI6ImxvZ29VcmlFbiIsImRlIjoibG9nb1VyaURlIn19.WQauSJEOIljNcojD59vhRwDKQiR2nes7PIDXqI14wVJ_njt6jD8mNsqoJko_rammXxwwaL_Zo1QOlfnkXIoWew"
        private const val MISSING_ORG_NAME =
            "eyJhbGciOiJFUzI1NiIsInR5cCI6InZjK3NkLWp3dCIsImtpZCI6ImRpZDp0ZHc6YWJjI2tleTAxIn0.eyJpc3MiOiJkaWQ6dGR3OmFiYyIsIm5iZiI6MCwiZXhwIjo5OTk5OTk5OTk5LCJpYXQiOjAsIl9zZF9hbGciOiJzaGEtMjU2Iiwic3ViIjoiZGlkOnRkdzphYmNkIiwib3JnTmFtZSI6eyJlbiI6Im9yZ05hbWUgRW4iLCJkZS1DSCI6Im9yZ05hbWUgRGUifSwicHJlZkxhbmciOiJkZSIsInZjdCI6IlRydXN0U3RhdGVtZW50TWV0YWRhdGFWMSJ9.sLm7z9FSmaeTBXA9pgmkaa62TOMs6kHpjH4QEdyhHv7bqpi-O82ngyvb_R7yDgI6Gv_sRhH-h4E1EwSEO3lYIA"
        private const val MISSING_PREF_LANG =
            "eyJhbGciOiJFUzI1NiIsInR5cCI6InZjK3NkLWp3dCIsImtpZCI6ImRpZDp0ZHc6YWJjI2tleTAxIn0.eyJpc3MiOiJkaWQ6dGR3OmFiYyIsIm5iZiI6MCwiZXhwIjo5OTk5OTk5OTk5LCJpYXQiOjAsIl9zZF9hbGciOiJzaGEtMjU2Iiwic3ViIjoiZGlkOnRkdzphYmNkIiwib3JnTmFtZSI6eyJlbiI6Im9yZ05hbWUgRW4iLCJkZS1DSCI6Im9yZ05hbWUgRGUifSwidmN0IjoiVHJ1c3RTdGF0ZW1lbnRNZXRhZGF0YVYxIiwibG9nb1VyaSI6eyJlbiI6ImxvZ29VcmlFbiIsImRlIjoibG9nb1VyaURlIn19.NRMDJN5yR_MaL0Ca27TYeRY_1th-fiZu3COI36oXSRjWmZWqXH0_ru9M2c3SvrefwLGxaDPTO7ciLAVVTuY1ag"
        private const val WRONG_FIELD_TYPES =
            "eyJhbGciOiJFUzI1NiIsInR5cCI6InZjK3NkLWp3dCIsImtpZCI6ImRpZDp0ZHc6YWJjI2tleTAxIn0.eyJpc3MiOiJkaWQ6dGR3OmFiYyIsIm5iZiI6MCwiZXhwIjo5OTk5OTk5OTk5LCJpYXQiOjAsIl9zZF9hbGciOiJzaGEtMjU2Iiwic3ViIjoiZGlkOnRkdzphYmNkIiwib3JnTmFtZSI6Im15IG9yZyBuYW1lIiwicHJlZkxhbmciOiJkZSIsInZjdCI6IlRydXN0U3RhdGVtZW50TWV0YWRhdGFWMSIsImxvZ29VcmkiOiJteSBsb2dvIFVyaSJ9.gOkip7Y-9KK8PtdO9-mIG6t1h0Yhe1g3Uo4XgJtyQeCGfzcQnRiC79JbNC1iMNIqcdaPNWAqp5ZnbzdXUeXs3Q"
        private const val MISSING_STATUS_PROPERTIES =
            "eyJhbGciOiJFUzI1NiIsInR5cCI6InZjK3NkLWp3dCIsImtpZCI6ImRpZDp0ZHc6YWJjI2tleTAxIn0.eyJpc3MiOiJkaWQ6dGR3OmFiYyIsIm5iZiI6MCwiZXhwIjo5OTk5OTk5OTk5LCJpYXQiOjAsIl9zZF9hbGciOiJzaGEtMjU2Iiwic3ViIjoiZGlkOnRkdzphYmNkIiwib3JnTmFtZSI6eyJlbiI6Im9yZ05hbWUgRW4iLCJkZS1DSCI6Im9yZ05hbWUgRGUifSwicHJlZkxhbmciOiJkZSIsInZjdCI6IlRydXN0U3RhdGVtZW50TWV0YWRhdGFWMSIsImxvZ29VcmkiOnsiZW4iOiJsb2dvVXJpRW4iLCJkZSI6ImxvZ29VcmlEZSJ9fQ.TLMEnH5lq5cXyrwetzd2wNoIkapb-cIWMG-2zvKFfRneMo_llk7wwKV-7opQ96OGtx9degPq_wD9t0aLRZQI0g"
        //region Trust statement source

/* Trust statement content
header:

{
  "alg": "ES256",
  "typ": "vc+sd-jwt",
  "kid": "did:tdw:abc#key01"
}

payload
{
  "iss": "did:tdw:abc",
  "nbf": 0,
  "exp": 9999999999,
  "iat": 0,
  "_sd_alg": "sha-256",
  "sub": "did:tdw:abcd",
  "orgName": {
    "en": "orgName En",
    "de-CH": "orgName De"
  },
  "prefLang": "de",
  "vct": "TrustStatementMetadataV1",
  "logoUri": {
    "en": "logoUriEn",
    "de": "logoUriDe"
  },
  "status":{
      "status_list":{
         "idx":0,
         "uri":"uri"
      }
   }
}

-----BEGIN PUBLIC KEY-----
MFkwEwYHKoZIzj0CAQYIKoZIzj0DAQcDQgAEEVs/o5+uQbTjL3chynL4wXgUg2R9
q9UU8I5mEovUf86QZ7kOBIjJwqnzD1omageEHWwHdBO6B+dFabmdT9POxg==
-----END PUBLIC KEY-----
-----BEGIN PRIVATE KEY-----
MIGHAgEAMBMGByqGSM49AgEGCCqGSM49AwEHBG0wawIBAQQgevZzL1gdAFr88hb2
OF/2NxApJCzGCEDdfSp6VQO30hyhRANCAAQRWz+jn65BtOMvdyHKcvjBeBSDZH2r
1RTwjmYSi9R/zpBnuQ4EiMnCqfMPWiZqB4QdbAd0E7oH50VpuZ1P087G
-----END PRIVATE KEY-----
 */
        //endregion
    }
}
