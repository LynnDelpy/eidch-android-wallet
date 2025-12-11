package ch.admin.foitt.wallet.platform.nonCompliance.di

import ch.admin.foitt.wallet.platform.nonCompliance.data.repository.NonComplianceRepositoryImpl
import ch.admin.foitt.wallet.platform.nonCompliance.domain.repository.NonComplianceRepository
import ch.admin.foitt.wallet.platform.nonCompliance.domain.usecase.FetchNonComplianceData
import ch.admin.foitt.wallet.platform.nonCompliance.domain.usecase.implementation.FetchNonComplianceDataImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.scopes.ActivityRetainedScoped

@Module
@InstallIn(ActivityRetainedComponent::class)
internal interface NonComplianceModule {
    @Binds
    @ActivityRetainedScoped
    fun bindNonComplianceRepository(
        repo: NonComplianceRepositoryImpl
    ): NonComplianceRepository

    @Binds
    fun bindFetchNonComplianceData(
        useCase: FetchNonComplianceDataImpl
    ): FetchNonComplianceData
}
