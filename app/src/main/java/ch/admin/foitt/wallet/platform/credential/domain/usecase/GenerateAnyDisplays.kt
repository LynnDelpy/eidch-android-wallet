package ch.admin.foitt.wallet.platform.credential.domain.usecase

import ch.admin.foitt.openid4vc.domain.model.anycredential.AnyCredential
import ch.admin.foitt.openid4vc.domain.model.credentialoffer.metadata.AnyCredentialConfiguration
import ch.admin.foitt.openid4vc.domain.model.credentialoffer.metadata.IssuerCredentialInfo
import ch.admin.foitt.wallet.platform.credential.domain.model.AnyDisplays
import ch.admin.foitt.wallet.platform.credential.domain.model.GenerateCredentialDisplaysError
import ch.admin.foitt.wallet.platform.oca.domain.model.OcaBundle
import com.github.michaelbull.result.Result

interface GenerateAnyDisplays {
    suspend operator fun invoke(
        anyCredential: AnyCredential,
        issuerInfo: IssuerCredentialInfo,
        trustIssuerNames: Map<String, String>? = null,
        metadata: AnyCredentialConfiguration,
        ocaBundle: OcaBundle?,
    ): Result<AnyDisplays, GenerateCredentialDisplaysError>
}
