package ch.admin.foitt.wallet.platform.eIdApplicationProcess

import ch.admin.foitt.wallet.platform.eIdApplicationProcess.di.EidApplicationProcessEntryPoint
import ch.admin.foitt.wallet.platform.eIdApplicationProcess.domain.model.EIdDocumentType
import ch.admin.foitt.wallet.platform.eIdApplicationProcess.domain.repository.EidApplicationProcessRepository
import ch.admin.foitt.wallet.platform.eIdApplicationProcess.domain.usecase.implementation.SetDocumentTypeImpl
import ch.admin.foitt.wallet.platform.navigation.DestinationScopedComponentManager
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerifyOrder
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.unmockkAll
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource

class SetDocumentTypeTest {

    @MockK
    private lateinit var mockDestinationScopedComponentManager: DestinationScopedComponentManager

    @MockK
    private lateinit var mockEidApplicationProcessRepository: EidApplicationProcessRepository

    @MockK
    private lateinit var mockEidApplicationProcessEntryPoint: EidApplicationProcessEntryPoint

    private lateinit var useCase: SetDocumentTypeImpl

    @BeforeEach
    fun setup() {
        MockKAnnotations.init(this)

        useCase = SetDocumentTypeImpl(
            destinationScopedComponentManager = mockDestinationScopedComponentManager,
        )

        coEvery {
            mockDestinationScopedComponentManager.getEntryPoint(
                EidApplicationProcessEntryPoint::class.java,
                componentScope = any()
            )
        } returns mockEidApplicationProcessEntryPoint

        coEvery {
            mockEidApplicationProcessEntryPoint.eidApplicationProcessRepository()
        } returns mockEidApplicationProcessRepository

        coEvery {
            mockEidApplicationProcessRepository.setDocumentType(eIdDocumentType = any())
        } just Runs
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @ParameterizedTest
    @EnumSource(value = EIdDocumentType::class)
    fun `SetDocumentType sets the proper value in the scoped repository`(documentType: EIdDocumentType): Unit =
        runTest {
            useCase(eIdDocumentType = documentType)

            coVerifyOrder {
                mockDestinationScopedComponentManager.getEntryPoint(
                    EidApplicationProcessEntryPoint::class.java,
                    any()
                )
                mockEidApplicationProcessEntryPoint.eidApplicationProcessRepository()
                mockEidApplicationProcessRepository.setDocumentType(eIdDocumentType = documentType)
            }
        }
}
