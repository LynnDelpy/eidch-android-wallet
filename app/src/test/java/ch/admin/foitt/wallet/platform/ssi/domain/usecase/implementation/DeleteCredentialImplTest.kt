package ch.admin.foitt.wallet.platform.ssi.domain.usecase.implementation

import ch.admin.foitt.wallet.platform.database.domain.model.Credential
import ch.admin.foitt.wallet.platform.database.domain.model.CredentialKeyBindingEntity
import ch.admin.foitt.wallet.platform.database.domain.model.CredentialWithKeyBinding
import ch.admin.foitt.wallet.platform.database.domain.model.VerifiableCredentialEntity
import ch.admin.foitt.wallet.platform.ssi.domain.model.SsiError
import ch.admin.foitt.wallet.platform.ssi.domain.repository.CredentialRepo
import ch.admin.foitt.wallet.platform.ssi.domain.repository.CredentialWithKeyBindingRepository
import ch.admin.foitt.wallet.platform.ssi.domain.usecase.DeleteCredential
import ch.admin.foitt.wallet.util.assertErrorType
import ch.admin.foitt.wallet.util.assertOk
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.unmockkAll
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class DeleteCredentialImplTest {

    @MockK
    private lateinit var mockCredentialRepository: CredentialRepo

    @MockK
    private lateinit var mockCredentialWithKeyBindingRepository: CredentialWithKeyBindingRepository

    @MockK
    private lateinit var mockCredential: Credential

    @MockK
    private lateinit var mockVerifiableCredential: VerifiableCredentialEntity

    @MockK
    private lateinit var mockKeyBinding: CredentialKeyBindingEntity

    private lateinit var useCase: DeleteCredential

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)

        every { mockKeyBinding.id } returns PRIVATE_KEY_IDENTIFIER

        val credentialWithKeyBinding = CredentialWithKeyBinding(
            credential = mockCredential,
            keyBinding = mockKeyBinding,
            verifiableCredential = mockVerifiableCredential,
        )
        coEvery {
            mockCredentialWithKeyBindingRepository.getByCredentialId(
                CREDENTIAL_ID
            )
        } returns Ok(credentialWithKeyBinding)
        coEvery { mockCredentialRepository.deleteById(CREDENTIAL_ID) } returns Ok(Unit)

        useCase = DeleteCredentialImpl(
            credentialRepo = mockCredentialRepository,
            credentialWithKeyBindingRepository = mockCredentialWithKeyBindingRepository,
        )
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `Deleting a credential succeeds and runs specific steps`() = runTest {
        useCase(CREDENTIAL_ID).assertOk()

        coVerify(exactly = 1) {
            mockCredentialWithKeyBindingRepository.getByCredentialId(CREDENTIAL_ID)
            mockCredentialRepository.deleteById(CREDENTIAL_ID)
        }
    }

    @Test
    fun `Deleting a credential maps errors from getting the credential`() = runTest {
        coEvery {
            mockCredentialWithKeyBindingRepository.getByCredentialId(CREDENTIAL_ID)
        } returns Err(SsiError.Unexpected(Exception()))

        useCase(CREDENTIAL_ID).assertErrorType(SsiError.Unexpected::class)
    }

    @Test
    fun `Deleting a credential maps errors from deleting the credential`() = runTest {
        coEvery { mockCredentialRepository.deleteById(CREDENTIAL_ID) } returns Err(SsiError.Unexpected(Exception()))

        useCase(CREDENTIAL_ID).assertErrorType(SsiError.Unexpected::class)
    }

    private companion object {
        const val CREDENTIAL_ID = 1L
        const val PRIVATE_KEY_IDENTIFIER = "privateKeyIdentifier"
    }
}
