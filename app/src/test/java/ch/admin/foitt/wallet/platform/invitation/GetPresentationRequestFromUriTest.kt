package ch.admin.foitt.wallet.platform.invitation

import ch.admin.foitt.openid4vc.domain.model.jwt.Jwt
import ch.admin.foitt.openid4vc.domain.model.presentationRequest.PresentationRequestContainer
import ch.admin.foitt.openid4vc.domain.model.presentationRequest.PresentationRequestError
import ch.admin.foitt.openid4vc.domain.usecase.FetchPresentationRequest
import ch.admin.foitt.wallet.platform.invitation.domain.model.InvitationError
import ch.admin.foitt.wallet.platform.invitation.domain.usecase.GetPresentationRequestFromUri
import ch.admin.foitt.wallet.platform.invitation.domain.usecase.implementation.GetPresentationRequestFromUriImpl
import ch.admin.foitt.wallet.util.assertOk
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.getError
import io.mockk.MockKAnnotations
import io.mockk.Ordering
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.unmockkAll
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import java.net.URI
import java.net.URL

class GetPresentationRequestFromUriTest {

    @MockK
    private lateinit var mockFetchPresentationRequest: FetchPresentationRequest

    @MockK
    private lateinit var mockPresentationRequestContainerJwt: PresentationRequestContainer.Jwt

    @MockK
    private lateinit var mockJwt: Jwt

    private lateinit var getPresentationRequestFromUri: GetPresentationRequestFromUri

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)

        getPresentationRequestFromUri = GetPresentationRequestFromUriImpl(
            fetchPresentationRequest = mockFetchPresentationRequest,
        )

        coEvery {
            mockFetchPresentationRequest.invoke(url = any())
        } returns Ok(mockPresentationRequestContainerJwt)
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @ParameterizedTest
    @MethodSource("getSuccessMappings")
    fun `A valid presentation request should return a success`(input: Triple<URI, URL, String?>) = runTest {
        coEvery {
            mockFetchPresentationRequest(url = input.second)
        } returns Ok(mockPresentationRequestContainerJwt)
        every { mockPresentationRequestContainerJwt.jwt } returns mockJwt
        every { mockPresentationRequestContainerJwt.clientId } returns input.third

        val result = getPresentationRequestFromUri(
            uri = input.first,
        )

        coVerify(ordering = Ordering.ORDERED) {
            mockFetchPresentationRequest.invoke(url = any())
        }

        val presentationRequestContainer = result.assertOk()

        assertEquals(input.third, presentationRequestContainer.clientId)
    }

    @Test
    fun `A repository network error should return an error`() = runTest {
        coEvery {
            mockFetchPresentationRequest.invoke(url = any())
        } returns Err(PresentationRequestError.NetworkError)

        val useCaseResult = getPresentationRequestFromUri(
            uri = validPresentationUri,
        )

        coVerify(ordering = Ordering.ORDERED) {
            mockFetchPresentationRequest.invoke(url = any())
        }

        assertTrue(useCaseResult.getError() is InvitationError.NetworkError)
    }

    @Test
    fun `A presentation unexpected error should return an InvalidInvitation error`() = runTest {
        coEvery {
            mockFetchPresentationRequest.invoke(url = any())
        } returns Err(PresentationRequestError.Unexpected(Exception()))

        val useCaseResult = getPresentationRequestFromUri(
            uri = validPresentationUri,
        )

        coVerify(ordering = Ordering.ORDERED) {
            mockFetchPresentationRequest.invoke(url = any())
        }

        assertTrue(useCaseResult.getError() is InvitationError.InvalidPresentationRequest)
    }

    @Test
    fun `An invalid URL should return an error`() = runTest {
        val useCaseResult = getPresentationRequestFromUri(
            uri = URI("invalid://invalid.com"),
        )

        coVerify(exactly = 0) {
            mockFetchPresentationRequest.invoke(url = any())
        }

        assertTrue(useCaseResult.getError() is InvitationError.InvalidUri)
    }

    companion object {
        private val validPresentationUri =
            URI("https://example.org/get_request_object/88cf0d95-54c9-465f-9b97-0ba782314700")
        private val validOIDUri =
            URI("openid4vp://?client_id=https%3A%2F%2Frp.example&request_uri=https%3A%2F%2Frp.example%2Fresource_location.jwt")
        private val validOIDDecodedRequestUrl = URL("https://rp.example/resource_location.jwt")
        private val validSwiyuUri =
            URI("swiyu-verify://?client_id=https%3A%2F%2Frp.example&request_uri=https%3A%2F%2Frp.example%2Fresource_location.jwt")
        private val validSwiyuDecodedRequestUrl = URL("https://rp.example/resource_location.jwt")

        @JvmStatic
        fun getSuccessMappings() = listOf(
            // Triple of presentation uri, request_uri as URL, expected client_id
            Triple(validPresentationUri, validPresentationUri.toURL(), null),
            Triple(validOIDUri, validOIDDecodedRequestUrl, "https://rp.example"),
            Triple(validSwiyuUri, validSwiyuDecodedRequestUrl, "https://rp.example")
        )
    }
}
