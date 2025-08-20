package ch.admin.foitt.wallet.platform.credential.domain.usecase.implementation

import ch.admin.foitt.openid4vc.domain.model.anycredential.AnyCredential
import ch.admin.foitt.wallet.platform.credential.domain.model.GetAnyCredentialsError
import ch.admin.foitt.wallet.platform.credential.domain.model.toAnyCredential
import ch.admin.foitt.wallet.platform.credential.domain.model.toGetAnyCredentialsError
import ch.admin.foitt.wallet.platform.credential.domain.usecase.GetAnyCredentials
import ch.admin.foitt.wallet.platform.ssi.domain.model.CredentialWithKeyBindingRepositoryError
import ch.admin.foitt.wallet.platform.ssi.domain.repository.CredentialWithKeyBindingRepository
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.coroutines.coroutineBinding
import com.github.michaelbull.result.get
import com.github.michaelbull.result.mapError
import javax.inject.Inject

internal class GetAnyCredentialsImpl @Inject constructor(
    private val credentialWithKeyBindingRepository: CredentialWithKeyBindingRepository,
) : GetAnyCredentials {
    override suspend fun invoke(): Result<List<AnyCredential>, GetAnyCredentialsError> = coroutineBinding {
        val credentials = credentialWithKeyBindingRepository.getAll()
            .mapError(CredentialWithKeyBindingRepositoryError::toGetAnyCredentialsError)
            .bind()
        credentials.mapNotNull { credential ->
            credential.toAnyCredential().get()
        }
    }
}
