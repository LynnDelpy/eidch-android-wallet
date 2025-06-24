package ch.admin.foitt.wallet.platform.appAttestation.domain.repository

import ch.admin.foitt.wallet.platform.appAttestation.domain.model.AppIntegrityRepositoryError
import ch.admin.foitt.wallet.platform.appAttestation.domain.model.IntegrityToken
import com.github.michaelbull.result.Result

interface AppIntegrityRepository {
    suspend fun fetchIntegrityToken(tokenNonce: String): Result<IntegrityToken, AppIntegrityRepositoryError>
}
