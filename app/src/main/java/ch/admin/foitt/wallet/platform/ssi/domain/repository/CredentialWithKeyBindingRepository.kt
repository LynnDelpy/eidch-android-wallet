package ch.admin.foitt.wallet.platform.ssi.domain.repository

import ch.admin.foitt.wallet.platform.database.domain.model.CredentialWithKeyBinding
import ch.admin.foitt.wallet.platform.ssi.domain.model.CredentialWithKeyBindingRepositoryError
import com.github.michaelbull.result.Result

interface CredentialWithKeyBindingRepository {
    suspend fun getAll(): Result<List<CredentialWithKeyBinding>, CredentialWithKeyBindingRepositoryError>
    suspend fun getByCredentialId(credentialId: Long): Result<CredentialWithKeyBinding, CredentialWithKeyBindingRepositoryError>
}
