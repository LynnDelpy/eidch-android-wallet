package ch.admin.foitt.wallet.platform.oca.domain.model

import ch.admin.foitt.wallet.platform.credential.domain.model.AnyCredentialDisplay
import ch.admin.foitt.wallet.platform.credential.domain.model.MetaDisplays
import ch.admin.foitt.wallet.platform.database.domain.model.Cluster

data class OcaDisplays(
    override val credentialDisplays: List<AnyCredentialDisplay>,
    override val clusters: List<Cluster>,
) : MetaDisplays
