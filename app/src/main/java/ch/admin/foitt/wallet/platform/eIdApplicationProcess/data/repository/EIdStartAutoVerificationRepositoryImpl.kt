package ch.admin.foitt.wallet.platform.eIdApplicationProcess.data.repository

import ch.admin.foitt.wallet.platform.eIdApplicationProcess.domain.model.AutoVerificationResponse
import ch.admin.foitt.wallet.platform.eIdApplicationProcess.domain.repository.EIdStartAutoVerificationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

class EIdStartAutoVerificationRepositoryImpl @Inject constructor() : EIdStartAutoVerificationRepository {
    private val _packageResult = MutableStateFlow<AutoVerificationResponse?>(null)
    override val packageResult = _packageResult.asStateFlow()
    override fun setPackageResult(packageResult: AutoVerificationResponse) {
        _packageResult.value = packageResult
    }
}
