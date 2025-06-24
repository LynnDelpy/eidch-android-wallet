package ch.admin.foitt.wallet.platform.credential.domain.usecase

import ch.admin.foitt.openid4vc.domain.model.credentialoffer.metadata.AnyCredentialConfiguration
import ch.admin.foitt.wallet.platform.credential.domain.model.GenerateMetadataDisplaysError
import ch.admin.foitt.wallet.platform.credential.domain.model.MetadataDisplays
import com.github.michaelbull.result.Result

interface GenerateMetadataDisplays {
    suspend operator fun invoke(
        credentialClaims: Map<String, String>,
        metadata: AnyCredentialConfiguration,
    ): Result<MetadataDisplays, GenerateMetadataDisplaysError>
}
