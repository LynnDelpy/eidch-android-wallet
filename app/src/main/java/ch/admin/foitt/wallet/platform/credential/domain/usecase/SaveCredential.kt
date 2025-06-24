package ch.admin.foitt.wallet.platform.credential.domain.usecase

import ch.admin.foitt.openid4vc.domain.model.anycredential.AnyCredential
import ch.admin.foitt.wallet.platform.credential.domain.model.AnyDisplays
import ch.admin.foitt.wallet.platform.credential.domain.model.SaveCredentialError
import ch.admin.foitt.wallet.platform.database.domain.model.RawCredentialData
import com.github.michaelbull.result.Result

fun interface SaveCredential {
    suspend operator fun invoke(
        anyCredential: AnyCredential,
        anyDisplays: AnyDisplays,
        rawCredentialData: RawCredentialData
    ): Result<Long, SaveCredentialError>
}
