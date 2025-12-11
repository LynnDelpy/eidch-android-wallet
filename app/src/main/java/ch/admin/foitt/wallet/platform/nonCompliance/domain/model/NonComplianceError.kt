package ch.admin.foitt.wallet.platform.nonCompliance.domain.model

import ch.admin.foitt.wallet.platform.trustRegistry.domain.model.GetTrustDomainFromDidError
import ch.admin.foitt.wallet.platform.trustRegistry.domain.model.TrustRegistryError
import timber.log.Timber

sealed interface NonComplianceError {
    data class Unexpected(val throwable: Throwable?) :
        NonComplianceRepositoryError,
        FetchNonComplianceDataError
}

sealed interface FetchNonComplianceDataError
sealed interface NonComplianceRepositoryError

fun Throwable.toNonComplianceRepositoryError(message: String): NonComplianceRepositoryError {
    Timber.e(t = this, message = message)
    return NonComplianceError.Unexpected(this)
}

fun GetTrustDomainFromDidError.toFetchNonComplianceDataError(): FetchNonComplianceDataError = when (this) {
    is TrustRegistryError.Unexpected -> NonComplianceError.Unexpected(cause)
    is GetTrustDomainFromDidError.NoTrustRegistryMapping -> {
        Timber.w(message = this.message)
        NonComplianceError.Unexpected(null)
    }
}
