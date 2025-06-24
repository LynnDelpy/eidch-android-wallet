package ch.admin.foitt.openid4vc.domain.usecase.implementation

import ch.admin.foitt.openid4vc.domain.model.credentialoffer.metadata.RawAndParsedIssuerCredentialInfo
import ch.admin.foitt.openid4vc.domain.repository.CredentialOfferRepository
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

class FetchRawAndParsedIssuerCredentialInfoImplTest {

    @MockK
    private lateinit var mockCredentialOfferRepository: CredentialOfferRepository

    @MockK
    private lateinit var mockRawAndParsedIssuerCredentialInfo: RawAndParsedIssuerCredentialInfo

    private lateinit var useCase: FetchRawAndParsedIssuerCredentialInfoImpl

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)

        useCase = FetchRawAndParsedIssuerCredentialInfoImpl(credentialOfferRepository = mockCredentialOfferRepository)

        coEvery {
            useCase(issuerEndpoint = any())
        } returns Ok(mockRawAndParsedIssuerCredentialInfo)
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `UseCase should call the CredentialOffer repository `() = runTest {
        val issuerEndpoint = "issuerEndpoint"
        useCase(issuerEndpoint = issuerEndpoint)
        coVerify(exactly = 1) {
            mockCredentialOfferRepository.fetchRawAndParsedIssuerCredentialInformation(
                issuerEndpoint = issuerEndpoint
            )
        }
    }
}
