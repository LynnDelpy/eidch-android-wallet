package ch.admin.foitt.wallet.platform.credentialCluster.domain.usercase.implementation

import ch.admin.foitt.wallet.platform.credentialCluster.domain.usercase.MapToCredentialClaimCluster
import ch.admin.foitt.wallet.platform.credentialCluster.domain.usercase.implementation.mock.ClusterMocks.clusterInput
import ch.admin.foitt.wallet.platform.credentialCluster.domain.usercase.implementation.mock.ClusterMocks.complexClusterInput
import ch.admin.foitt.wallet.platform.credentialCluster.domain.usercase.implementation.mock.ClusterMocks.complexEmptyClusterInput
import ch.admin.foitt.wallet.platform.credentialCluster.domain.usercase.implementation.mock.ClusterMocks.credentialClaimClusterEntities1
import ch.admin.foitt.wallet.platform.credentialCluster.domain.usercase.implementation.mock.ClusterMocks.credentialClaimClusterEntities2
import ch.admin.foitt.wallet.platform.credentialCluster.domain.usercase.implementation.mock.ClusterMocks.credentialClaimClusterEntities3
import ch.admin.foitt.wallet.platform.credentialCluster.domain.usercase.implementation.mock.ClusterMocks.credentialClaimClusterEntities4
import ch.admin.foitt.wallet.platform.credentialCluster.domain.usercase.implementation.mock.ClusterMocks.credentialClaimClusterEntities5
import ch.admin.foitt.wallet.platform.credentialCluster.domain.usercase.implementation.mock.ClusterMocks.credentialClaimClusterEntitiesMinus1
import ch.admin.foitt.wallet.platform.credentialCluster.domain.usercase.implementation.mock.ClusterMocks.credentialClaimItem1
import ch.admin.foitt.wallet.platform.credentialCluster.domain.usercase.implementation.mock.ClusterMocks.credentialClaimItem2
import ch.admin.foitt.wallet.platform.credentialCluster.domain.usercase.implementation.mock.ClusterMocks.credentialClaimItem3
import ch.admin.foitt.wallet.platform.credentialCluster.domain.usercase.implementation.mock.ClusterMocks.credentialClaimItem4
import ch.admin.foitt.wallet.platform.credentialCluster.domain.usercase.implementation.mock.ClusterMocks.credentialClaimItem5
import ch.admin.foitt.wallet.platform.credentialCluster.domain.usercase.implementation.mock.ClusterMocks.credentialClaimItemMinus1
import ch.admin.foitt.wallet.platform.credentialCluster.domain.usercase.implementation.mock.ClusterMocks.credentialClaimWithDisplay1
import ch.admin.foitt.wallet.platform.credentialCluster.domain.usercase.implementation.mock.ClusterMocks.credentialClaimWithDisplay2
import ch.admin.foitt.wallet.platform.credentialCluster.domain.usercase.implementation.mock.ClusterMocks.credentialClaimWithDisplay3
import ch.admin.foitt.wallet.platform.credentialCluster.domain.usercase.implementation.mock.ClusterMocks.credentialClaimWithDisplay4
import ch.admin.foitt.wallet.platform.credentialCluster.domain.usercase.implementation.mock.ClusterMocks.credentialClaimWithDisplay5
import ch.admin.foitt.wallet.platform.credentialCluster.domain.usercase.implementation.mock.ClusterMocks.credentialClaimWithDisplayMinus1
import ch.admin.foitt.wallet.platform.credentialCluster.domain.usercase.implementation.mock.ClusterMocks.expectedCluster
import ch.admin.foitt.wallet.platform.credentialCluster.domain.usercase.implementation.mock.ClusterMocks.expectedComplexCluster
import ch.admin.foitt.wallet.platform.credentialCluster.domain.usercase.implementation.mock.ClusterMocks.expectedComplexEmptyCluster
import ch.admin.foitt.wallet.platform.credentialCluster.domain.usercase.implementation.mock.ClusterMocks.expectedSimpleCluster
import ch.admin.foitt.wallet.platform.credentialCluster.domain.usercase.implementation.mock.ClusterMocks.expectedSimpleEmptyCluster
import ch.admin.foitt.wallet.platform.credentialCluster.domain.usercase.implementation.mock.ClusterMocks.simpleClusterInput
import ch.admin.foitt.wallet.platform.credentialCluster.domain.usercase.implementation.mock.ClusterMocks.simpleEmptyClusterInput
import ch.admin.foitt.wallet.platform.locale.domain.usecase.GetLocalizedDisplay
import ch.admin.foitt.wallet.platform.ssi.domain.usecase.MapToCredentialClaimData
import com.github.michaelbull.result.Ok
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import io.mockk.unmockkAll
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class MapToCredentialClaimClusterImplTest {
    @MockK
    private lateinit var mockGetLocalizedDisplay: GetLocalizedDisplay

    @MockK
    private lateinit var mockMapToCredentialClaimData: MapToCredentialClaimData

    private lateinit var useCase: MapToCredentialClaimCluster

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)

        initDefaultMocks()

        useCase = MapToCredentialClaimClusterImpl(mockGetLocalizedDisplay, mockMapToCredentialClaimData)
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `mapping a list of simple clusters and claims should return a correct result`() = runTest {
        val result = useCase(clustersWithDisplaysAndClaims = simpleClusterInput)

        assertEquals(expectedSimpleCluster, result)
    }

    @Test
    fun `mapping list of clusters and claims should return a correct result`() = runTest {
        val result = useCase(clustersWithDisplaysAndClaims = clusterInput)

        assertEquals(expectedCluster, result)
    }

    @Test
    fun `mapping list of complex clusters and claims should return a correct result`() = runTest {
        val result = useCase(clustersWithDisplaysAndClaims = complexClusterInput)

        assertEquals(expectedComplexCluster, result)
    }

    @Test
    fun `mapping list of clusters correctly filters empty clusters`() = runTest {
        val result = useCase(clustersWithDisplaysAndClaims = simpleEmptyClusterInput)

        assertEquals(expectedSimpleEmptyCluster, result)
    }

    @Test
    fun `mapping list of clusters correctly filters nested empty clusters`() = runTest {
        val result = useCase(clustersWithDisplaysAndClaims = complexEmptyClusterInput)

        assertEquals(expectedComplexEmptyCluster, result)
    }

    private fun initDefaultMocks() {
        coEvery {
            mockGetLocalizedDisplay(displays = credentialClaimClusterEntitiesMinus1)
        } returns credentialClaimClusterEntitiesMinus1.first()
        coEvery {
            mockGetLocalizedDisplay(displays = credentialClaimClusterEntities1)
        } returns credentialClaimClusterEntities1.first()
        coEvery {
            mockGetLocalizedDisplay(displays = credentialClaimClusterEntities2)
        } returns credentialClaimClusterEntities2.first()
        coEvery {
            mockGetLocalizedDisplay(displays = credentialClaimClusterEntities3)
        } returns credentialClaimClusterEntities3.first()
        coEvery {
            mockGetLocalizedDisplay(displays = credentialClaimClusterEntities4)
        } returns credentialClaimClusterEntities4.first()
        coEvery {
            mockGetLocalizedDisplay(displays = credentialClaimClusterEntities5)
        } returns credentialClaimClusterEntities5.first()

        coEvery {
            mockMapToCredentialClaimData(claimWithDisplays = credentialClaimWithDisplayMinus1)
        } returns Ok(credentialClaimItemMinus1)
        coEvery {
            mockMapToCredentialClaimData(claimWithDisplays = credentialClaimWithDisplay1)
        } returns Ok(credentialClaimItem1)
        coEvery {
            mockMapToCredentialClaimData(claimWithDisplays = credentialClaimWithDisplay2)
        } returns Ok(credentialClaimItem2)
        coEvery {
            mockMapToCredentialClaimData(claimWithDisplays = credentialClaimWithDisplay3)
        } returns Ok(credentialClaimItem3)
        coEvery {
            mockMapToCredentialClaimData(claimWithDisplays = credentialClaimWithDisplay4)
        } returns Ok(credentialClaimItem4)
        coEvery {
            mockMapToCredentialClaimData(claimWithDisplays = credentialClaimWithDisplay5)
        } returns Ok(credentialClaimItem5)
    }
}
