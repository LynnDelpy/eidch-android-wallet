package ch.admin.foitt.wallet.platform.oca.domain.usecase

import ch.admin.foitt.wallet.platform.oca.domain.model.GenerateOcaDisplaysError
import ch.admin.foitt.wallet.platform.oca.domain.model.OcaBundle
import ch.admin.foitt.wallet.platform.oca.domain.model.OcaDisplays
import com.github.michaelbull.result.Result

interface GenerateOcaDisplays {
    suspend operator fun invoke(
        credentialClaims: Map<String, String>,
        ocaBundle: OcaBundle,
    ): Result<OcaDisplays, GenerateOcaDisplaysError>
}
