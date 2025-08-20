package ch.admin.foitt.wallet.platform.ssi.data.repository

import ch.admin.foitt.openid4vc.domain.model.credentialoffer.metadata.CredentialFormat
import ch.admin.foitt.openid4vc.domain.model.keyBinding.KeyBinding
import ch.admin.foitt.wallet.platform.credential.domain.model.AnyClaimDisplay
import ch.admin.foitt.wallet.platform.credential.domain.model.AnyCredentialDisplay
import ch.admin.foitt.wallet.platform.credential.domain.model.AnyIssuerDisplay
import ch.admin.foitt.wallet.platform.database.data.dao.CredentialClaimClusterDisplayEntityDao
import ch.admin.foitt.wallet.platform.database.data.dao.CredentialClaimClusterEntityDao
import ch.admin.foitt.wallet.platform.database.data.dao.CredentialClaimDao
import ch.admin.foitt.wallet.platform.database.data.dao.CredentialClaimDisplayDao
import ch.admin.foitt.wallet.platform.database.data.dao.CredentialDao
import ch.admin.foitt.wallet.platform.database.data.dao.CredentialDisplayDao
import ch.admin.foitt.wallet.platform.database.data.dao.CredentialIssuerDisplayDao
import ch.admin.foitt.wallet.platform.database.data.dao.CredentialKeyBindingEntityDao
import ch.admin.foitt.wallet.platform.database.data.dao.DaoProvider
import ch.admin.foitt.wallet.platform.database.data.dao.RawCredentialDataDao
import ch.admin.foitt.wallet.platform.database.domain.model.Cluster
import ch.admin.foitt.wallet.platform.database.domain.model.ClusterDisplay
import ch.admin.foitt.wallet.platform.database.domain.model.Credential
import ch.admin.foitt.wallet.platform.database.domain.model.CredentialClaim
import ch.admin.foitt.wallet.platform.database.domain.model.CredentialClaimDisplay
import ch.admin.foitt.wallet.platform.database.domain.model.CredentialDisplay
import ch.admin.foitt.wallet.platform.database.domain.model.CredentialIssuerDisplay
import ch.admin.foitt.wallet.platform.database.domain.model.CredentialKeyBindingEntity
import ch.admin.foitt.wallet.platform.database.domain.model.DisplayConst
import ch.admin.foitt.wallet.platform.database.domain.model.DisplayLanguage
import ch.admin.foitt.wallet.platform.database.domain.model.RawCredentialData
import ch.admin.foitt.wallet.platform.database.domain.model.toCredentialClaimClusterDisplayEntity
import ch.admin.foitt.wallet.platform.database.domain.model.toCredentialClaimClusterEntity
import ch.admin.foitt.wallet.platform.database.domain.usecase.RunInTransaction
import ch.admin.foitt.wallet.platform.di.IoDispatcher
import ch.admin.foitt.wallet.platform.ssi.domain.model.CredentialOfferRepositoryError
import ch.admin.foitt.wallet.platform.ssi.domain.model.toCredentialOfferRepositoryError
import ch.admin.foitt.wallet.platform.ssi.domain.repository.CredentialOfferRepository
import ch.admin.foitt.wallet.platform.utils.suspendUntilNonNull
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.coroutines.runSuspendCatching
import com.github.michaelbull.result.mapError
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CredentialOfferRepositoryImpl @Inject constructor(
    daoProvider: DaoProvider,
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    private val runInTransaction: RunInTransaction,
) : CredentialOfferRepository {

    override suspend fun saveCredentialOffer(
        keyBinding: KeyBinding?,
        payload: String,
        format: CredentialFormat,
        validFrom: Long?,
        validUntil: Long?,
        issuer: String?,
        issuerDisplays: List<AnyIssuerDisplay>,
        credentialDisplays: List<AnyCredentialDisplay>,
        clusters: List<Cluster>,
        rawCredentialData: RawCredentialData
    ): Result<Long, CredentialOfferRepositoryError> = withContext(ioDispatcher) {
        val credential = createCredential(
            payload = payload,
            format = format,
            issuer = issuer,
            validFrom = validFrom,
            validUntil = validUntil,
        )
        val credentialKeyBinding = createCredentialKeyBinding(keyBinding)
        val credentialIssuerDisplays = createCredentialIssuerDisplays(issuerDisplays)
        val credDisplays = createCredentialDisplays(credentialDisplays)

        saveCredentialOffer(
            credential = credential,
            keyBinding = credentialKeyBinding,
            issuerDisplays = credentialIssuerDisplays,
            credentialDisplays = credDisplays,
            clusters = clusters,
            rawCredentialData = rawCredentialData
        )
    }

    private fun createCredential(
        payload: String,
        format: CredentialFormat,
        validFrom: Long?,
        validUntil: Long?,
        issuer: String?
    ) = Credential(
        payload = payload,
        format = format,
        issuer = issuer,
        validFrom = validFrom,
        validUntil = validUntil
    )

    private fun createCredentialKeyBinding(keyBinding: KeyBinding?) = keyBinding?.let {
        CredentialKeyBindingEntity(
            id = it.identifier,
            credentialId = -1,
            algorithm = it.algorithm.stdName,
            bindingType = it.bindingType,
            publicKey = it.publicKey,
            privateKey = it.privateKey,
        )
    }

    private fun createCredentialIssuerDisplays(
        issuerDisplays: List<AnyIssuerDisplay>,
    ) = issuerDisplays.map { display ->
        CredentialIssuerDisplay(
            credentialId = -1,
            name = display.name ?: DisplayConst.ISSUER_FALLBACK_NAME,
            image = display.logo,
            imageAltText = display.logoAltText,
            locale = display.locale ?: DisplayLanguage.UNKNOWN,
        )
    }

    private fun createCredentialDisplays(
        credentialDisplays: List<AnyCredentialDisplay>,
    ) = credentialDisplays.map { display ->
        CredentialDisplay(
            // at this point we do not have the credential id yet, since it will only be known after the credential was inserted in the db
            credentialId = -1,
            locale = display.locale ?: DisplayLanguage.UNKNOWN,
            name = display.name,
            description = display.description,
            logoUri = display.logo,
            logoAltText = display.logoAltText,
            backgroundColor = display.backgroundColor,
        )
    }

    private fun createClusterDisplayEntities(
        clusterDisplays: List<ClusterDisplay>,
        clusterId: Long
    ) = clusterDisplays.map { clusterDisplay ->
        clusterDisplay.toCredentialClaimClusterDisplayEntity(clusterId)
    }

    private fun createCredentialClaims(
        claims: Map<CredentialClaim, List<AnyClaimDisplay>>
    ): Map<CredentialClaim, List<CredentialClaimDisplay>> = claims.map { (claim, claimDisplays) ->
        claim to claimDisplays.map { display ->
            CredentialClaimDisplay(
                // at this point we do not have the claim id yet, since it will only be known after the claim was inserted in the db
                claimId = -1,
                name = display.name,
                locale = display.locale ?: DisplayLanguage.UNKNOWN,
                value = display.value,
            )
        }
    }.toMap()

    private suspend fun saveCredentialOffer(
        credential: Credential,
        keyBinding: CredentialKeyBindingEntity?,
        issuerDisplays: List<CredentialIssuerDisplay>,
        credentialDisplays: List<CredentialDisplay>,
        clusters: List<Cluster>,
        rawCredentialData: RawCredentialData
    ): Result<Long, CredentialOfferRepositoryError> = runSuspendCatching {
        val credentialId = runInTransaction {
            val credentialId = credentialDao().insert(credential)
            keyBinding?.let {
                credentialKeyBindingDao().insert(it.copy(credentialId = credentialId))
            }
            credentialIssuerDisplayDao().insertAll(issuerDisplays.map { it.copy(credentialId = credentialId) })
            credentialDisplayDao().insertAll(credentialDisplays.map { it.copy(credentialId = credentialId) })

            clusters.forEach { cluster ->
                saveCluster(cluster, credentialId, null)
            }

            rawCredentialDataDao().insert(rawCredentialData = rawCredentialData.copy(credentialId = credentialId))
            credentialId
        }

        if (credentialId == null) {
            error("credential id == null")
        }

        credentialId
    }.mapError { throwable ->
        throwable.toCredentialOfferRepositoryError("saveCredentialOffer error")
    }

    private suspend fun saveCluster(cluster: Cluster, credentialId: Long, parentClusterId: Long?) {
        val clusterId = credentialClaimClusterEntityDao().insert(cluster.toCredentialClaimClusterEntity(credentialId, parentClusterId))

        val clusterDisplays = createClusterDisplayEntities(cluster.clusterDisplays, clusterId)
        credentialClaimClusterDisplayEntityDao().insertAll(clusterDisplays)

        val claims = createCredentialClaims(cluster.claims)
        claims.forEach { (claim, claimDisplays) ->
            val claimToSave = claim.copy(clusterId = clusterId)
            val claimId = credentialClaimDao().insert(claimToSave)
            val displays = claimDisplays.map { it.copy(claimId = claimId) }
            credentialClaimDisplayDao().insertAll(displays)
        }

        cluster.childClusters.forEach { childCluster ->
            saveCluster(cluster = childCluster, credentialId = credentialId, parentClusterId = clusterId)
        }
    }

    private val credentialDaoFlow = daoProvider.credentialDaoFlow
    private suspend fun credentialDao(): CredentialDao = suspendUntilNonNull { credentialDaoFlow.value }

    private val credentialKeyBindingEntityDaoFlow = daoProvider.credentialKeyBindingEntityDaoFlow
    private suspend fun credentialKeyBindingDao(): CredentialKeyBindingEntityDao = suspendUntilNonNull {
        credentialKeyBindingEntityDaoFlow.value
    }

    private val credentialDisplayDaoFlow = daoProvider.credentialDisplayDaoFlow
    private suspend fun credentialDisplayDao(): CredentialDisplayDao = suspendUntilNonNull {
        credentialDisplayDaoFlow.value
    }

    private val rawCredentialDataDao = daoProvider.rawCredentialDataDao
    private suspend fun rawCredentialDataDao(): RawCredentialDataDao = suspendUntilNonNull { rawCredentialDataDao.value }

    private val credentialIssuerDisplayDaoFlow = daoProvider.credentialIssuerDisplayDaoFlow
    private suspend fun credentialIssuerDisplayDao(): CredentialIssuerDisplayDao = suspendUntilNonNull {
        credentialIssuerDisplayDaoFlow.value
    }

    private val credentialClaimDaoFlow = daoProvider.credentialClaimDaoFlow
    private suspend fun credentialClaimDao(): CredentialClaimDao = suspendUntilNonNull { credentialClaimDaoFlow.value }

    private val credentialClaimDisplayDaoFlow = daoProvider.credentialClaimDisplayDaoFlow
    private suspend fun credentialClaimDisplayDao(): CredentialClaimDisplayDao = suspendUntilNonNull {
        credentialClaimDisplayDaoFlow.value
    }

    private val credentialClaimClusterEntityDaoFlow = daoProvider.credentialClaimClusterEntityDao
    private suspend fun credentialClaimClusterEntityDao(): CredentialClaimClusterEntityDao = suspendUntilNonNull {
        credentialClaimClusterEntityDaoFlow.value
    }

    private val credentialClaimClusterDisplayEntityDaoFlow = daoProvider.credentialClaimClusterDisplayEntityDao
    private suspend fun credentialClaimClusterDisplayEntityDao(): CredentialClaimClusterDisplayEntityDao = suspendUntilNonNull {
        credentialClaimClusterDisplayEntityDaoFlow.value
    }
}
