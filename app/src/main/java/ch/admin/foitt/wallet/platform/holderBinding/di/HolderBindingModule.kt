package ch.admin.foitt.wallet.platform.holderBinding.di

import ch.admin.foitt.wallet.platform.holderBinding.domain.usecase.CreateJWSKeyPairInHardware
import ch.admin.foitt.wallet.platform.holderBinding.domain.usecase.CreateJWSKeyPairInSoftware
import ch.admin.foitt.wallet.platform.holderBinding.domain.usecase.CreateKeyGenSpec
import ch.admin.foitt.wallet.platform.holderBinding.domain.usecase.GenerateKeyPair
import ch.admin.foitt.wallet.platform.holderBinding.domain.usecase.implementation.CreateJWSKeyPairInHardwareImpl
import ch.admin.foitt.wallet.platform.holderBinding.domain.usecase.implementation.CreateJWSKeyPairInSoftwareImpl
import ch.admin.foitt.wallet.platform.holderBinding.domain.usecase.implementation.CreateKeyGenSpecImpl
import ch.admin.foitt.wallet.platform.holderBinding.domain.usecase.implementation.GenerateKeyPairImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent

@Module
@InstallIn(ActivityRetainedComponent::class)
class HolderBindingModule

@Module
@InstallIn(ActivityRetainedComponent::class)
internal interface HolderBindingBindingsModule {
    @Binds
    fun bindGenerateKeyPair(
        useCase: GenerateKeyPairImpl
    ): GenerateKeyPair

    @Binds
    fun bindCreateJWSKeyPairInHardware(
        useCase: CreateJWSKeyPairInHardwareImpl
    ): CreateJWSKeyPairInHardware

    @Binds
    fun bindCreateKeyGenSpec(
        factory: CreateKeyGenSpecImpl
    ): CreateKeyGenSpec

    @Binds
    fun bindCreateJWSKeyPairInSoftware(
        useCase: CreateJWSKeyPairInSoftwareImpl
    ): CreateJWSKeyPairInSoftware
}
