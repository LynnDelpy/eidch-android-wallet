package ch.admin.foitt.wallet.platform.credentialStatus.domain.usecase.implementation.mock

import ch.admin.foitt.openid4vc.domain.model.credentialoffer.metadata.CredentialFormat
import ch.admin.foitt.wallet.platform.database.domain.model.Credential
import ch.admin.foitt.wallet.platform.database.domain.model.CredentialStatus
import ch.admin.foitt.wallet.platform.database.domain.model.VerifiableCredentialEntity

object MockCredentials {
    private const val payload = "payload"
    private val format = CredentialFormat.VC_SD_JWT

    private val validCredential = Credential(
        id = 0,
        format = format,
        createdAt = 1700463600000,
    )

    private val validVerifiableCredential = VerifiableCredentialEntity(
        status = CredentialStatus.VALID,
        payload = payload,
        createdAt = 1700463600000,
        updatedAt = null,
        issuer = "issuer",
        validFrom = 0,
        credentialId = 0,
        validUntil = 17768026519L,
    )

    val credentials = listOf(
        validCredential,
        validCredential.copy(id = 1),
        validCredential.copy(id = 2),
    )

    val verifiedCredentials = listOf(
        validVerifiableCredential,
        validVerifiableCredential.copy(credentialId = 1, status = CredentialStatus.REVOKED),
        validVerifiableCredential.copy(credentialId = 2, status = CredentialStatus.UNKNOWN),
    )
}
