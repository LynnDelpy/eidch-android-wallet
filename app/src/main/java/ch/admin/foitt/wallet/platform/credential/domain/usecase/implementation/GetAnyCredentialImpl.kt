package ch.admin.foitt.wallet.platform.credential.domain.usecase.implementation

import ch.admin.foitt.openid4vc.domain.model.anycredential.AnyCredential
import ch.admin.foitt.wallet.platform.credential.domain.model.AnyCredentialError
import ch.admin.foitt.wallet.platform.credential.domain.model.GetAnyCredentialError
import ch.admin.foitt.wallet.platform.credential.domain.model.toAnyCredential
import ch.admin.foitt.wallet.platform.credential.domain.model.toGetAnyCredentialError
import ch.admin.foitt.wallet.platform.credential.domain.usecase.GetAnyCredential
import ch.admin.foitt.wallet.platform.ssi.domain.model.CredentialWithKeyBindingRepositoryError
import ch.admin.foitt.wallet.platform.ssi.domain.repository.CredentialWithKeyBindingRepository
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.andThen
import com.github.michaelbull.result.coroutines.coroutineBinding
import com.github.michaelbull.result.mapError
import javax.inject.Inject

class GetAnyCredentialImpl @Inject constructor(
    private val credentialWithKeyBindingRepository: CredentialWithKeyBindingRepository
) : GetAnyCredential {
    override suspend fun invoke(credentialId: Long): Result<AnyCredential, GetAnyCredentialError> =
        credentialWithKeyBindingRepository.getByCredentialId(credentialId)
            .mapError(CredentialWithKeyBindingRepositoryError::toGetAnyCredentialError)
            .andThen { credentialWithKeyBinding ->
                coroutineBinding {
                    credentialWithKeyBinding.toAnyCredential().bind()
                }.mapError(AnyCredentialError::toGetAnyCredentialError)
            }
}
