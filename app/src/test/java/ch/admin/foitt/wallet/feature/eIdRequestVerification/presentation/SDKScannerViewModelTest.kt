package ch.admin.foitt.wallet.feature.eIdRequestVerification.presentation

import ch.admin.foitt.avwrapper.AVBeam
import ch.admin.foitt.avwrapper.AVBeamError
import ch.admin.foitt.avwrapper.AVBeamStatus
import ch.admin.foitt.avwrapper.AvBeamNotification
import ch.admin.foitt.avwrapper.AvBeamScanDocumentNotification
import ch.admin.foitt.avwrapper.DocumentScanPackageResult
import ch.admin.foitt.wallet.platform.eIdApplicationProcess.domain.usecase.SetDocumentScanResult
import ch.admin.foitt.wallet.platform.navigation.NavigationManager
import ch.admin.foitt.wallet.platform.scaffold.domain.usecase.SetTopBarState
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@OptIn(ExperimentalCoroutinesApi::class)
@ExtendWith(MockKExtension::class)
class SDKScannerViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    @MockK(relaxed = true)
    lateinit var avBeam: AVBeam

    @MockK(relaxed = true)
    lateinit var navManager: NavigationManager

    @MockK(relaxed = true)
    lateinit var setDocumentScanResult: SetDocumentScanResult

    @MockK(relaxed = true)
    lateinit var setTopBarState: SetTopBarState

    private lateinit var scanDocumentFlow: MutableStateFlow<AvBeamScanDocumentNotification>
    private lateinit var statusFlow: MutableStateFlow<AVBeamStatus>
    private lateinit var errorFlow: MutableStateFlow<AVBeamError>

    private lateinit var viewModel: SDKScannerViewModel

    @BeforeEach
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        scanDocumentFlow = MutableStateFlow(AvBeamNotification.Empty)
        statusFlow = MutableStateFlow(AVBeamStatus.Init)
        errorFlow = MutableStateFlow(AVBeamError.None)

        coEvery { avBeam.initializedFlow } returns MutableStateFlow(true)
        every { avBeam.getGLView(any(), any()) } returns mockk {
            every { width } returns 100
            every { height } returns 200
        }
        every { avBeam.scanDocumentFlow } returns scanDocumentFlow
        every { avBeam.statusFlow } returns statusFlow
        every { avBeam.errorFlow } returns errorFlow

        viewModel = SDKScannerViewModel(
            navManager = navManager,
            avBeam = avBeam,
            setDocumentScanResult = setDocumentScanResult,
            setTopBarState = setTopBarState
        )
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `calls avBeam_shutDown and stopCamera when DocumentScanCompleted is emitted`() = runTest {
        // Given
        val fakePackageData = mockk<DocumentScanPackageResult>(relaxed = true)
        val notification = AvBeamNotification.DocumentScanCompleted(fakePackageData)

        // When
        viewModel.onInitScan(100, 200)
        viewModel.onAfterViewAttached()
        scanDocumentFlow.emit(notification)
        advanceUntilIdle()

        // Then
        coVerify { avBeam.stopCamera() }
        coVerify { avBeam.shutDown() }
        coVerify { setDocumentScanResult.invoke(fakePackageData) }
    }
}
