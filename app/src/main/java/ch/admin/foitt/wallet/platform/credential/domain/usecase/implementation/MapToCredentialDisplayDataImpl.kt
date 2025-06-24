package ch.admin.foitt.wallet.platform.credential.domain.usecase.implementation

import ch.admin.foitt.wallet.platform.credential.domain.model.CredentialDisplayData
import ch.admin.foitt.wallet.platform.credential.domain.model.CredentialError
import ch.admin.foitt.wallet.platform.credential.domain.model.MapToCredentialDisplayDataError
import ch.admin.foitt.wallet.platform.credential.domain.model.getDisplayStatus
import ch.admin.foitt.wallet.platform.credential.domain.usecase.IsBetaIssuer
import ch.admin.foitt.wallet.platform.credential.domain.usecase.MapToCredentialDisplayData
import ch.admin.foitt.wallet.platform.database.domain.model.Credential
import ch.admin.foitt.wallet.platform.database.domain.model.CredentialClaimWithDisplays
import ch.admin.foitt.wallet.platform.database.domain.model.CredentialDisplay
import ch.admin.foitt.wallet.platform.locale.domain.usecase.GetLocalizedAndThemedDisplay
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.coroutines.coroutineBinding
import javax.inject.Inject

class MapToCredentialDisplayDataImpl @Inject constructor(
    private val getLocalizedAndThemedDisplay: GetLocalizedAndThemedDisplay,
    private val isBetaIssuer: IsBetaIssuer,
) : MapToCredentialDisplayData {
    override suspend fun invoke(
        credential: Credential,
        credentialDisplays: List<CredentialDisplay>,
        claims: List<CredentialClaimWithDisplays>,
    ): Result<CredentialDisplayData, MapToCredentialDisplayDataError> = coroutineBinding {
        val credentialDisplay = getDisplay(credentialDisplays).bind()

        val resolvedDisplay = credentialDisplay.resolveTemplate(claims)

        CredentialDisplayData(
            credentialId = credential.id,
            status = credential.getDisplayStatus(credential.status),
            credentialDisplay = resolvedDisplay,
            isCredentialFromBetaIssuer = isBetaIssuer(credential.issuer)
        )
    }

    private fun getDisplay(displays: List<CredentialDisplay>): Result<CredentialDisplay, MapToCredentialDisplayDataError> =
        getLocalizedAndThemedDisplay(displays)?.let { Ok(it) }
            ?: Err(CredentialError.Unexpected(IllegalStateException("No localized display found")))

    private fun CredentialDisplay.resolveTemplate(claims: List<CredentialClaimWithDisplays>): CredentialDisplay {
        val description = this.description ?: ""

        // matches must start with: {{$.
        // contain 1 to n: word characters, dots, [, and ]
        // end with: }}
        val jsonPathFinder = Regex("""\{\{(\$\.[\w.\[\]]+?)\}\}""")
        val matches = jsonPathFinder.findAll(description).map { match ->
            val matchText = match.groupValues[1]
            val range = match.range
            Pair(matchText, range)
        }.toList()

        var resolvedField = description
        matches.reversed().forEach { (text, range) ->
            val replacement = claims.find { text == "$.${it.claim.key}" }?.claim?.value ?: ""
            resolvedField = resolvedField.replaceRange(range, replacement)
        }

        return this.copy(description = resolvedField)
    }
}
