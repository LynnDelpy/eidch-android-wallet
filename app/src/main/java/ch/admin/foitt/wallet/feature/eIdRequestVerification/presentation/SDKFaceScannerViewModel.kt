package ch.admin.foitt.wallet.feature.eIdRequestVerification.presentation

import android.view.SurfaceView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import ch.admin.foitt.avwrapper.AVBeam
import ch.admin.foitt.avwrapper.AVBeamInitConfig
import ch.admin.foitt.avwrapper.AVBeamStatus
import ch.admin.foitt.avwrapper.AvBeamNotification
import ch.admin.foitt.avwrapper.config.AVBeamCaptureFaceConfig
import ch.admin.foitt.avwrapper.config.AVBeamConfigLogLevel
import ch.admin.foitt.wallet.feature.eIdRequestVerification.domain.usecase.SaveEIdRequestFiles
import ch.admin.foitt.wallet.feature.eIdRequestVerification.presentation.model.SDKInfoState
import ch.admin.foitt.wallet.platform.database.domain.model.EIdRequestFileCategory
import ch.admin.foitt.wallet.platform.environmentSetup.domain.repository.EnvironmentSetupRepository
import ch.admin.foitt.wallet.platform.navArgs.domain.model.EIdOnlineSessionNavArg
import ch.admin.foitt.wallet.platform.navigation.NavigationManager
import ch.admin.foitt.wallet.platform.scaffold.domain.model.TopBarState
import ch.admin.foitt.wallet.platform.scaffold.domain.usecase.SetTopBarState
import ch.admin.foitt.wallet.platform.scaffold.presentation.ScreenViewModel
import ch.admin.foitt.walletcomposedestinations.destinations.EIdProcessDataScreenDestination
import ch.admin.foitt.walletcomposedestinations.destinations.SDKFaceScannerScreenDestination
import com.github.michaelbull.result.onSuccess
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class SDKFaceScannerViewModel @Inject constructor(
    private val navManager: NavigationManager,
    private val avBeam: AVBeam,
    private val saveEIdRequestFiles: SaveEIdRequestFiles,
    private val environmentSetupRepository: EnvironmentSetupRepository,
    savedStateHandle: SavedStateHandle,
    setTopBarState: SetTopBarState,
) : ScreenViewModel(setTopBarState) {

    private val navArgs: EIdOnlineSessionNavArg = SDKFaceScannerScreenDestination.argsFrom(savedStateHandle)

    override val topBarState = TopBarState.None

    private val _infoState = MutableStateFlow<SDKInfoState>(SDKInfoState.Loading)
    val infoState = _infoState.asStateFlow()

    private val _infoText = MutableStateFlow("")
    val infoText = _infoText.asStateFlow()

    private val _screenSize = MutableStateFlow<Pair<Int, Int>?>(null)

    private val _surfaceView = MutableStateFlow<SurfaceView?>(null)
    val surfaceView: StateFlow<SurfaceView?> = _surfaceView.asStateFlow()

    private val _lockOrientation = MutableStateFlow(false)
    val lockOrientation: StateFlow<Boolean> = _lockOrientation

    private fun setScreenSize(width: Int, height: Int) {
        _screenSize.value = width to height
    }

    suspend fun onInitScan(activity: AppCompatActivity, width: Int, height: Int) {
        val logLevel = if (environmentSetupRepository.avBeamLoggingEnabled) {
            AVBeamConfigLogLevel.DEBUG
        } else {
            AVBeamConfigLogLevel.NONE
        }
        avBeam.init(AVBeamInitConfig(logLevel), activity)
        avBeam.initializedFlow
            .first { it }

        // Sometimes the app is faster than the lib and the GLView wasn't yet initialized
        delay(LIB_DELAY)

        val view = avBeam.getGLView(width, height)
        setScreenSize(view.width, view.height)
        _surfaceView.value = view
    }

    fun onAfterViewAttached() {
        _infoState.update { SDKInfoState.Ready }

        // Stop the camera feed the SDK needs it
        avBeam.stopCamera()
        avBeam.startFrontCamera()

        startScanFace(avBeam)

        viewModelScope.launch {
            avBeam.statusFlow.collect { notif ->
                notif.let {
                    _infoState.update { SDKInfoState.InfoData }
                    _infoText.value = it.toString()

                    when (notif) {
                        AVBeamStatus.FaceCapturingStopped -> _lockOrientation.value = false
                        else -> {}
                    }
                }
            }
        }

        viewModelScope.launch {
            avBeam.errorFlow.collect { notif ->
                notif.let {
                    Timber.tag("SDK_PXL").d("Received error: $it")
                }
            }
        }

        viewModelScope.launch {
            avBeam.captureFaceFlow.collect { notif ->
                when (notif) {
                    is AvBeamNotification.Completed -> {
                        avBeam.stopCamera()
                        avBeam.stopCaptureFace()
                        avBeam.shutDown()

                        saveEIdRequestFiles(
                            sIdCaseId = navArgs.caseId,
                            filesDataList = notif.packageData.files,
                            filesCategory = EIdRequestFileCategory.FACE_RECORDING,
                        ).onSuccess {
                            Timber.d("Files saved successfully")
                        }

                        navManager.navigateToAndClearCurrent(EIdProcessDataScreenDestination(caseId = navArgs.caseId))
                        _infoState.update { SDKInfoState.Empty }
                    }

                    is AvBeamNotification.Empty, is AvBeamNotification.Initial -> {
                        _infoState.update { SDKInfoState.Empty }
                    }
                }
            }
        }
    }

    private fun startScanFace(avBeam: AVBeam) {
        _lockOrientation.value = true
        val width = _screenSize.value!!.first
        val height = _screenSize.value!!.second

        val configScanning = AVBeamCaptureFaceConfig(
            timeout = 30,
            rectWidth = width,
            rectHeight = height,
        )

        viewModelScope.launch {
            delay(LIB_CAMERA_DELAY)
            avBeam.startCaptureFace(configScanning)
        }
    }

    fun onCloseToast() {
        _infoState.update { SDKInfoState.Empty }
    }

    override fun onCleared() {
        avBeam.stopCaptureFace()
        super.onCleared()
    }

    companion object {
        private const val LIB_DELAY = 20L
        private const val LIB_CAMERA_DELAY = 700L
    }
}
