package ch.admin.foitt.openid4vc.domain.usecase

import androidx.annotation.CheckResult
import ch.admin.foitt.openid4vc.domain.model.credentialoffer.FetchIssuerCredentialInfoError
import ch.admin.foitt.openid4vc.domain.model.credentialoffer.metadata.RawAndParsedIssuerCredentialInfo
import com.github.michaelbull.result.Result

interface FetchRawAndParsedIssuerCredentialInfo {
    @CheckResult
    suspend operator fun invoke(
        issuerEndpoint: String
    ): Result<RawAndParsedIssuerCredentialInfo, FetchIssuerCredentialInfoError>
}
