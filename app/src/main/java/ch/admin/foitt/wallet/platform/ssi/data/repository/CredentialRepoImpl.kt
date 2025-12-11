package ch.admin.foitt.wallet.platform.ssi.data.repository

import ch.admin.foitt.wallet.platform.database.data.dao.CredentialDao
import ch.admin.foitt.wallet.platform.database.data.dao.DaoProvider
import ch.admin.foitt.wallet.platform.database.data.dao.ImageEntityDao
import ch.admin.foitt.wallet.platform.database.data.dao.VerifiableCredentialDao
import ch.admin.foitt.wallet.platform.database.domain.model.Credential
import ch.admin.foitt.wallet.platform.database.domain.model.CredentialStatus
import ch.admin.foitt.wallet.platform.di.IoDispatcher
import ch.admin.foitt.wallet.platform.ssi.domain.model.CredentialRepositoryError
import ch.admin.foitt.wallet.platform.ssi.domain.model.SsiError
import ch.admin.foitt.wallet.platform.ssi.domain.repository.CredentialRepo
import ch.admin.foitt.wallet.platform.utils.suspendUntilNonNull
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.coroutines.runSuspendCatching
import com.github.michaelbull.result.mapError
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.time.Instant
import javax.inject.Inject

class CredentialRepoImpl @Inject constructor(
    daoProvider: DaoProvider,
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) : CredentialRepo {

    override suspend fun getAll(): Result<List<Credential>, CredentialRepositoryError> = runSuspendCatching {
        withContext(ioDispatcher) {
            credentialDao().getAll()
        }
    }.mapError { throwable ->
        Timber.e(throwable)
        SsiError.Unexpected(throwable)
    }

    override suspend fun getAllIds(): Result<List<Long>, CredentialRepositoryError> = runSuspendCatching {
        withContext(ioDispatcher) {
            credentialDao().getAllIds()
        }
    }.mapError { throwable ->
        Timber.e(throwable)
        SsiError.Unexpected(throwable)
    }

    override suspend fun getById(id: Long): Result<Credential, CredentialRepositoryError> = runSuspendCatching {
        withContext(ioDispatcher) {
            credentialDao().getById(id)
        }
    }.mapError { throwable ->
        Timber.e(throwable)
        SsiError.Unexpected(throwable)
    }

    override suspend fun deleteById(id: Long): Result<Unit, CredentialRepositoryError> = runSuspendCatching {
        withContext(ioDispatcher) {
            credentialDao().deleteById(id)
            // also clean up images without children
            imageDao().deleteImagesWithoutChildren()
        }
    }.mapError { throwable ->
        Timber.e(throwable)
        SsiError.Unexpected(throwable)
    }

    override suspend fun updateStatusByCredentialId(credentialId: Long, status: CredentialStatus): Result<Int, CredentialRepositoryError> =
        runSuspendCatching {
            withContext(ioDispatcher) {
                verifiableCredentialDao().updateStatusByCredentialId(
                    id = credentialId,
                    status = status,
                    updatedAt = Instant.now().epochSecond,
                )
            }
        }.mapError { throwable ->
            Timber.e(throwable)
            SsiError.Unexpected(throwable)
        }

    private suspend fun credentialDao(): CredentialDao = suspendUntilNonNull { credentialDaoFlow.value }
    private suspend fun verifiableCredentialDao(): VerifiableCredentialDao = suspendUntilNonNull {
        verifiableCredentialDaoFlow.value
    }
    private suspend fun imageDao(): ImageEntityDao = suspendUntilNonNull { imageDaoFlow.value }

    private val credentialDaoFlow = daoProvider.credentialDaoFlow
    private val verifiableCredentialDaoFlow = daoProvider.verifiableCredentialDaoFlow
    private val imageDaoFlow = daoProvider.imageEntityDao
}
