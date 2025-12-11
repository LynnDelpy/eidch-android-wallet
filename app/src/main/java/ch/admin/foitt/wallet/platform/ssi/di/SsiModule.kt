package ch.admin.foitt.wallet.platform.ssi.di

import ch.admin.foitt.wallet.platform.ssi.data.repository.CredentialClaimDisplayRepoImpl
import ch.admin.foitt.wallet.platform.ssi.data.repository.CredentialIssuerDisplayRepoImpl
import ch.admin.foitt.wallet.platform.ssi.data.repository.CredentialOfferRepositoryImpl
import ch.admin.foitt.wallet.platform.ssi.data.repository.CredentialRepoImpl
import ch.admin.foitt.wallet.platform.ssi.data.repository.CredentialWithKeyBindingRepositoryImpl
import ch.admin.foitt.wallet.platform.ssi.data.repository.VerifiableCredentialWithDisplaysAndClustersRepositoryImpl
import ch.admin.foitt.wallet.platform.ssi.domain.repository.CredentialClaimDisplayRepo
import ch.admin.foitt.wallet.platform.ssi.domain.repository.CredentialIssuerDisplayRepo
import ch.admin.foitt.wallet.platform.ssi.domain.repository.CredentialOfferRepository
import ch.admin.foitt.wallet.platform.ssi.domain.repository.CredentialRepo
import ch.admin.foitt.wallet.platform.ssi.domain.repository.CredentialWithKeyBindingRepository
import ch.admin.foitt.wallet.platform.ssi.domain.repository.VerifiableCredentialWithDisplaysAndClustersRepository
import ch.admin.foitt.wallet.platform.ssi.domain.usecase.DeleteCredential
import ch.admin.foitt.wallet.platform.ssi.domain.usecase.GetCredentialDetailFlow
import ch.admin.foitt.wallet.platform.ssi.domain.usecase.GetCredentialsWithDetailsFlow
import ch.admin.foitt.wallet.platform.ssi.domain.usecase.MapToCredentialClaimData
import ch.admin.foitt.wallet.platform.ssi.domain.usecase.implementation.DeleteCredentialImpl
import ch.admin.foitt.wallet.platform.ssi.domain.usecase.implementation.GetCredentialDetailFlowImpl
import ch.admin.foitt.wallet.platform.ssi.domain.usecase.implementation.GetCredentialsWithDetailsFlowImpl
import ch.admin.foitt.wallet.platform.ssi.domain.usecase.implementation.MapToCredentialClaimDataImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.scopes.ActivityRetainedScoped

@Module
@InstallIn(ActivityRetainedComponent::class)
interface SsiModule {

    @Binds
    fun bindCredentialClaimDisplayRepo(
        useCase: CredentialClaimDisplayRepoImpl
    ): CredentialClaimDisplayRepo

    @Binds
    fun bindCredentialRepo(
        useCase: CredentialRepoImpl
    ): CredentialRepo

    @Binds
    fun bindDeleteCredential(
        useCase: DeleteCredentialImpl
    ): DeleteCredential

    @Binds
    fun bindMapToCredentialClaimData(
        useCase: MapToCredentialClaimDataImpl
    ): MapToCredentialClaimData

    @Binds
    @ActivityRetainedScoped
    fun bindCredentialOfferRepository(
        repo: CredentialOfferRepositoryImpl
    ): CredentialOfferRepository

    @Binds
    @ActivityRetainedScoped
    fun bindCredentialIssuerDisplayRepository(
        repo: CredentialIssuerDisplayRepoImpl
    ): CredentialIssuerDisplayRepo

    @Binds
    @ActivityRetainedScoped
    fun bindVerifiableCredentialWithDisplaysAndClustersRepository(
        repo: VerifiableCredentialWithDisplaysAndClustersRepositoryImpl
    ): VerifiableCredentialWithDisplaysAndClustersRepository

    @Binds
    @ActivityRetainedScoped
    fun bindCredentialWithKeyBindingRepository(
        repo: CredentialWithKeyBindingRepositoryImpl
    ): CredentialWithKeyBindingRepository

    @Binds
    fun bindGetCredentialDetailFlow(
        useCase: GetCredentialDetailFlowImpl
    ): GetCredentialDetailFlow

    @Binds
    fun bindGetCredentialWithDetailsFlow(
        useCase: GetCredentialsWithDetailsFlowImpl
    ): GetCredentialsWithDetailsFlow
}
