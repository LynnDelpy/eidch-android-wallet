package ch.admin.foitt.wallet.platform.credential.domain.model

import ch.admin.foitt.wallet.platform.database.domain.model.Cluster

data class MetadataDisplays(
    override val credentialDisplays: List<AnyCredentialDisplay>,
    override val clusters: List<Cluster>,
) : MetaDisplays
