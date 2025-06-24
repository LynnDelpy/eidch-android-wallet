package ch.admin.foitt.wallet.platform.credential.domain.model

import ch.admin.foitt.wallet.platform.database.domain.model.Cluster

interface MetaDisplays {
    val credentialDisplays: List<AnyCredentialDisplay>
    val clusters: List<Cluster>
}
