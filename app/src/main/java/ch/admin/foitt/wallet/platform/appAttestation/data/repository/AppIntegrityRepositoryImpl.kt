package ch.admin.foitt.wallet.platform.appAttestation.data.repository

import android.content.Context
import ch.admin.foitt.wallet.platform.appAttestation.domain.model.IntegrityToken
import ch.admin.foitt.wallet.platform.appAttestation.domain.model.toAppIntegrityRepositoryError
import ch.admin.foitt.wallet.platform.appAttestation.domain.repository.AppIntegrityRepository
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.coroutines.runSuspendCatching
import com.github.michaelbull.result.getOrThrow
import com.github.michaelbull.result.mapError
import com.google.android.gms.tasks.Task
import com.google.android.play.core.integrity.IntegrityManagerFactory
import com.google.android.play.core.integrity.IntegrityTokenRequest
import com.google.android.play.core.integrity.IntegrityTokenResponse
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class AppIntegrityRepositoryImpl @Inject constructor(
    @param:ApplicationContext private val appContext: Context,
) : AppIntegrityRepository {
    override suspend fun fetchIntegrityToken(tokenNonce: String) = runSuspendCatching<IntegrityToken> {
        val integrityManager = IntegrityManagerFactory.create(appContext)
        val integrityTokenRequest = IntegrityTokenRequest.builder()
            .setNonce(tokenNonce)
            .build()
        val integrityTokenResponse: Task<IntegrityTokenResponse> = integrityManager.requestIntegrityToken(integrityTokenRequest)

        val tokenResponse = integrityTokenResponse
            .awaitResult()
            .getOrThrow()

        IntegrityToken(tokenResponse.token())
    }.mapError { throwable ->
        throwable.toAppIntegrityRepositoryError("Fetch integrity token failed")
    }

    private suspend fun <T> Task<T>.awaitResult(): Result<T, Throwable> = runSuspendCatching {
        suspendCancellableCoroutine { continuation ->
            addOnSuccessListener { result ->
                continuation.resume(result)
            }
            addOnFailureListener { exception ->
                continuation.resumeWithException(exception)
            }
            addOnCanceledListener {
                continuation.cancel()
            }
        }
    }
}
