package ch.admin.foitt.wallet.platform.holderBinding.domain.usecase

import androidx.annotation.CheckResult
import ch.admin.foitt.openid4vc.domain.model.credentialoffer.metadata.ProofTypeConfig
import ch.admin.foitt.wallet.platform.holderBinding.domain.model.BindingKeyPair
import ch.admin.foitt.wallet.platform.holderBinding.domain.model.GenerateKeyPairError
import com.github.michaelbull.result.Result

fun interface GenerateKeyPair {
    @CheckResult
    suspend operator fun invoke(
        proofTypeConfig: ProofTypeConfig,
    ): Result<BindingKeyPair, GenerateKeyPairError>
}
