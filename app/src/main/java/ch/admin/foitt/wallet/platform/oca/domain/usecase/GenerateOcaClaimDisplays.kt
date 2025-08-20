package ch.admin.foitt.wallet.platform.oca.domain.usecase

import ch.admin.foitt.wallet.platform.credential.domain.model.AnyClaimDisplay
import ch.admin.foitt.wallet.platform.database.domain.model.CredentialClaim
import ch.admin.foitt.wallet.platform.oca.domain.model.OcaClaimData

interface GenerateOcaClaimDisplays {
    operator fun invoke(
        key: String,
        value: String?,
        ocaClaimData: OcaClaimData,
    ): Pair<CredentialClaim, List<AnyClaimDisplay>>
}
