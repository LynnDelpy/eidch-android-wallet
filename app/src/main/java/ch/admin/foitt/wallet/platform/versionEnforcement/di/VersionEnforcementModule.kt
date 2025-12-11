package ch.admin.foitt.wallet.platform.versionEnforcement.di

import ch.admin.foitt.wallet.platform.versionEnforcement.data.repository.VersionEnforcementRepositoryImpl
import ch.admin.foitt.wallet.platform.versionEnforcement.domain.repository.VersionEnforcementRepository
import ch.admin.foitt.wallet.platform.versionEnforcement.domain.usecase.FetchAppVersionInfo
import ch.admin.foitt.wallet.platform.versionEnforcement.domain.usecase.GetAppVersion
import ch.admin.foitt.wallet.platform.versionEnforcement.domain.usecase.implementation.FetchAppVersionInfoImpl
import ch.admin.foitt.wallet.platform.versionEnforcement.domain.usecase.implementation.GetAppVersionImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.scopes.ActivityRetainedScoped
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.ClassDiscriminatorMode
import kotlinx.serialization.json.Json
import timber.log.Timber
import javax.inject.Named

@Module
@InstallIn(ActivityRetainedComponent::class)
internal class VersionEnforcementModule {

    @ActivityRetainedScoped
    @Provides
    @Named(NAME_SCOPE)
    fun provideHttpClient(@Named(NAME_SCOPE) engine: HttpClientEngine, @Named(NAME_SCOPE) jsonSerializer: Json) =
        HttpClient(engine) {
            expectSuccess = true
            install(ContentNegotiation) {
                json(jsonSerializer)
            }
            install(Logging) {
                logger = object : Logger {
                    override fun log(message: String) {
                        Timber.d(message)
                    }
                }
                level = LogLevel.INFO
            }
        }

    @Provides
    @Named(NAME_SCOPE)
    fun provideHttpClientEngine(): HttpClientEngine = OkHttp.create()

    @OptIn(ExperimentalSerializationApi::class)
    @Provides
    @Named(NAME_SCOPE)
    fun provideJsonSerializer() =
        Json {
            ignoreUnknownKeys = true
            explicitNulls = false
            coerceInputValues = true
            classDiscriminatorMode = ClassDiscriminatorMode.NONE
        }

    companion object {
        internal const val NAME_SCOPE = "VersionEnforcementModule"
    }
}

@Module
@InstallIn(ActivityRetainedComponent::class)
internal interface VersionEnforcementBindings {
    @Binds
    fun bindFetchAppVersionInfo(
        useCase: FetchAppVersionInfoImpl
    ): FetchAppVersionInfo

    @Binds
    fun bindGetAppVersion(
        useCase: GetAppVersionImpl
    ): GetAppVersion

    @Binds
    @ActivityRetainedScoped
    fun bindVersionEnforcementRepository(
        repository: VersionEnforcementRepositoryImpl
    ): VersionEnforcementRepository
}
