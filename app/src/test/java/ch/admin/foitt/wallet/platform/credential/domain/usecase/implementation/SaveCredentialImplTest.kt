package ch.admin.foitt.wallet.platform.credential.domain.usecase.implementation

import ch.admin.foitt.openid4vc.domain.model.credentialoffer.metadata.SigningAlgorithm
import ch.admin.foitt.openid4vc.domain.model.vcSdJwt.VcSdJwtCredential
import ch.admin.foitt.wallet.platform.credential.domain.model.AnyDisplays
import ch.admin.foitt.wallet.platform.credential.domain.model.CredentialError
import ch.admin.foitt.wallet.platform.credential.domain.usecase.SaveCredential
import ch.admin.foitt.wallet.platform.database.domain.model.RawCredentialData
import ch.admin.foitt.wallet.platform.ssi.domain.model.SsiError
import ch.admin.foitt.wallet.platform.ssi.domain.repository.CredentialOfferRepository
import ch.admin.foitt.wallet.util.assertErrorType
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

class SaveCredentialImplTest {

    @MockK
    private lateinit var mockCredentialOfferRepository: CredentialOfferRepository

    lateinit var saveCredentialUseCase: SaveCredential

    @BeforeEach
    fun setup() {
        MockKAnnotations.init(this)

        saveCredentialUseCase = SaveCredentialImpl(
            mockCredentialOfferRepository,
        )
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `should call CredentialOfferRepository#saveCredentialOffer exactly one time`() = runTest {
        coEvery {
            mockCredentialOfferRepository.saveCredentialOffer(
                keyBindingIdentifier = any(),
                keyBindingAlgorithm = any(),
                payload = any(),
                format = any(),
                issuer = any(),
                issuerDisplays = any(),
                credentialDisplays = any(),
                clusters = any(),
                validFrom = any(),
                validUntil = any(),
                rawCredentialData = any()
            )
        } returns Ok(1L)

        val result = saveCredentialUseCase(
            anyCredential = anyCredential,
            anyDisplays = anyDisplays,
            rawCredentialData = RawCredentialData(credentialId = -1)
        )

        result.assertOk()

        coVerify(exactly = 1) {
            mockCredentialOfferRepository.saveCredentialOffer(
                keyBindingIdentifier = any(),
                keyBindingAlgorithm = any(),
                payload = any(),
                format = any(),
                issuer = any(),
                issuerDisplays = any(),
                credentialDisplays = any(),
                clusters = any(),
                validFrom = any(),
                validUntil = any(),
                rawCredentialData = any()
            )
        }
    }

    @Test
    fun `Saving credential maps errors from repository`() = runTest {
        val exception = IllegalStateException()
        coEvery {
            mockCredentialOfferRepository.saveCredentialOffer(any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any())
        } returns Err(SsiError.Unexpected(exception))

        saveCredentialUseCase(
            anyCredential = anyCredential,
            anyDisplays = anyDisplays,
            rawCredentialData = rawCredentialData
        ).assertErrorType(CredentialError.Unexpected::class)
    }

    private companion object {
        val anyCredential = VcSdJwtCredential(
            keyBindingIdentifier = "",
            keyBindingAlgorithm = SigningAlgorithm.ES512,
            payload = "ewogICJhbGciOiJFUzUxMiIsCiAgInR5cCI6IkpXVCIsCiAgImtpZCI6ImtleUlkIgp9.ewogICJfc2QiOlsKICAgICJZUkxmNjA2Y2x3dDQtaGp5R3plNDl5U0ZpNlZDbXdiOW41aHdiNFZVSlNZIiwKICAgICJRaHV2SU1RZDVMeVg4Z09SM3dlVnpTWTB5R1pHR0hkVlhZMEUtTmhoVWZ3IgogIF0sCiAgIl9zZF9hbGciOiJzaGEtMjU2IiwKICAiaWF0IjoxNjk3ODA2NjcxLAogICJpc3MiOiJpc3N1ZXIiLAogICJ2Y3QiOiJ2Y3QiCn0.ZXdvZ0lDSmhiR2NpT2lKRlV6VXhNaUlzQ2lBZ0luUjVjQ0k2SWtwWFZDSXNDaUFnSW10cFpDSTZJbXRsZVVsa0lncDkuLkFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUdOd0wySDZua2ZjdFNPT0NSU21HY080d3NGczNVZDJWR3phYkFySnpMSGJBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFDOFY2cGlRbDc3RnYwUVlUbTU4TmxJczMwZnNRdjc4aXRFUzNCSzR6ZnZI",
        )

        val anyDisplays = AnyDisplays(
            issuerDisplays = emptyList(),
            credentialDisplays = emptyList(),
            clusters = emptyList()
        )

        val rawCredentialData = RawCredentialData(credentialId = -1)
    }
}
