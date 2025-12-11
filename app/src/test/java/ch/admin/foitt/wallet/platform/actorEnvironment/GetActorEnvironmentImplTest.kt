package ch.admin.foitt.wallet.platform.actorEnvironment

import ch.admin.foitt.wallet.platform.actorEnvironment.domain.model.ActorEnvironment
import ch.admin.foitt.wallet.platform.actorEnvironment.domain.usecase.GetActorEnvironment
import ch.admin.foitt.wallet.platform.actorEnvironment.domain.usecase.implementation.GetActorEnvironmentImpl
import ch.admin.foitt.wallet.platform.credential.domain.usecase.implementation.mock.MockCredential
import io.mockk.MockKAnnotations
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class GetActorEnvironmentImplTest {

    private lateinit var useCase: GetActorEnvironment

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        useCase = GetActorEnvironmentImpl()
    }

    @Test
    fun `Getting a PROD payload should return Prod`() = runTest {
        val result = useCase(MockCredential.vcSdJwtCredentialProd.issuer)

        assertEquals(ActorEnvironment.PRODUCTION, result)
    }

    @Test
    fun `Getting a BETA payload should return Beta`() = runTest {
        val result = useCase(MockCredential.vcSdJwtCredentialBeta.issuer)

        assertEquals(ActorEnvironment.BETA, result)
    }

    @Test
    fun `Getting an external payload should return External`() = runTest {
        val result = useCase("external Issuer")

        assertEquals(ActorEnvironment.EXTERNAL, result)
    }

    @Test
    fun `Passing a null issuer returns External`() = runTest {
        val result = useCase(null)

        assertEquals(ActorEnvironment.EXTERNAL, result)
    }
}
