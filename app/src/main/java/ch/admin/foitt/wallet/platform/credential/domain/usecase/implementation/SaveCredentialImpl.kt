package ch.admin.foitt.wallet.platform.credential.domain.usecase.implementation

import ch.admin.foitt.openid4vc.domain.model.anycredential.AnyCredential
import ch.admin.foitt.wallet.platform.credential.domain.model.AnyDisplays
import ch.admin.foitt.wallet.platform.credential.domain.model.SaveCredentialError
import ch.admin.foitt.wallet.platform.credential.domain.model.toSaveCredentialError
import ch.admin.foitt.wallet.platform.credential.domain.usecase.SaveCredential
import ch.admin.foitt.wallet.platform.database.domain.model.RawCredentialData
import ch.admin.foitt.wallet.platform.ssi.domain.model.CredentialOfferRepositoryError
import ch.admin.foitt.wallet.platform.ssi.domain.repository.CredentialOfferRepository
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.coroutines.coroutineBinding
import com.github.michaelbull.result.mapError
import javax.inject.Inject

class SaveCredentialImpl @Inject constructor(
    private val credentialOfferRepository: CredentialOfferRepository,
) : SaveCredential {

    override suspend fun invoke(
        anyCredential: AnyCredential,
        anyDisplays: AnyDisplays,
        rawCredentialData: RawCredentialData
    ): Result<Long, SaveCredentialError> = coroutineBinding {
        credentialOfferRepository.saveCredentialOffer(
            keyBinding = anyCredential.keyBinding,
            payload = anyCredential.payload,
            format = anyCredential.format,
            validFrom = anyCredential.validFromInstant?.epochSecond,
            validUntil = anyCredential.validUntilInstant?.epochSecond,
            issuer = anyCredential.issuer,
            issuerDisplays = anyDisplays.issuerDisplays,
            credentialDisplays = anyDisplays.credentialDisplays,
            clusters = anyDisplays.clusters,
            rawCredentialData = rawCredentialData
        )
            .mapError(CredentialOfferRepositoryError::toSaveCredentialError)
            .bind()
    }
}
