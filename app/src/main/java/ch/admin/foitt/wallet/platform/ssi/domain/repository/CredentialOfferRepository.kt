@file:Suppress("LongParameterList")

package ch.admin.foitt.wallet.platform.ssi.domain.repository

import ch.admin.foitt.openid4vc.domain.model.credentialoffer.metadata.CredentialFormat
import ch.admin.foitt.openid4vc.domain.model.credentialoffer.metadata.SigningAlgorithm
import ch.admin.foitt.wallet.platform.credential.domain.model.AnyCredentialDisplay
import ch.admin.foitt.wallet.platform.credential.domain.model.AnyIssuerDisplay
import ch.admin.foitt.wallet.platform.database.domain.model.Cluster
import ch.admin.foitt.wallet.platform.database.domain.model.RawCredentialData
import ch.admin.foitt.wallet.platform.ssi.domain.model.CredentialOfferRepositoryError
import com.github.michaelbull.result.Result

interface CredentialOfferRepository {
    suspend fun saveCredentialOffer(
        keyBindingIdentifier: String?,
        keyBindingAlgorithm: SigningAlgorithm?,
        payload: String,
        format: CredentialFormat,
        validFrom: Long?,
        validUntil: Long?,
        issuer: String?,
        issuerDisplays: List<AnyIssuerDisplay>,
        credentialDisplays: List<AnyCredentialDisplay>,
        clusters: List<Cluster>,
        rawCredentialData: RawCredentialData
    ): Result<Long, CredentialOfferRepositoryError>
}
