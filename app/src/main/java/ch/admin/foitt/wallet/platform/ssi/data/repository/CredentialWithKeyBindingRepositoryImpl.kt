package ch.admin.foitt.wallet.platform.ssi.data.repository

import ch.admin.foitt.wallet.platform.database.data.dao.CredentialWithKeyBindingDao
import ch.admin.foitt.wallet.platform.database.data.dao.DaoProvider
import ch.admin.foitt.wallet.platform.database.domain.model.CredentialWithKeyBinding
import ch.admin.foitt.wallet.platform.di.IoDispatcher
import ch.admin.foitt.wallet.platform.ssi.domain.model.CredentialWithKeyBindingRepositoryError
import ch.admin.foitt.wallet.platform.ssi.domain.model.SsiError
import ch.admin.foitt.wallet.platform.ssi.domain.repository.CredentialWithKeyBindingRepository
import ch.admin.foitt.wallet.platform.utils.suspendUntilNonNull
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.coroutines.runSuspendCatching
import com.github.michaelbull.result.mapError
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

class CredentialWithKeyBindingRepositoryImpl @Inject constructor(
    daoProvider: DaoProvider,
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) : CredentialWithKeyBindingRepository {

    override suspend fun getAll():
        Result<List<CredentialWithKeyBinding>, CredentialWithKeyBindingRepositoryError> = runSuspendCatching {
        withContext(ioDispatcher) {
            dao().getAll()
        }
    }.mapError { throwable ->
        Timber.e(throwable)
        SsiError.Unexpected(throwable)
    }

    override suspend fun getByCredentialId(
        credentialId: Long
    ): Result<CredentialWithKeyBinding, CredentialWithKeyBindingRepositoryError> = runSuspendCatching {
        withContext(ioDispatcher) {
            dao().getCredentialWithKeyBindingById(credentialId)
        }
    }.mapError { throwable ->
        Timber.e(throwable)
        SsiError.Unexpected(throwable)
    }

    private suspend fun dao(): CredentialWithKeyBindingDao = suspendUntilNonNull { daoFlow.value }
    private val daoFlow = daoProvider.credentialWithKeyBindingDaoFlow
}
