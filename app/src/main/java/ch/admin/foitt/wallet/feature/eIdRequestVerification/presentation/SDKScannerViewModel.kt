package ch.admin.foitt.wallet.feature.eIdRequestVerification.presentation

import android.view.SurfaceView
import androidx.lifecycle.viewModelScope
import ch.admin.foitt.avwrapper.AVBeam
import ch.admin.foitt.avwrapper.AVBeamStatus
import ch.admin.foitt.avwrapper.AvBeamNotification
import ch.admin.foitt.avwrapper.config.AVBeamScanDocumentConfig
import ch.admin.foitt.wallet.feature.eIdRequestVerification.presentation.model.SDKInfoState
import ch.admin.foitt.wallet.platform.eIdApplicationProcess.domain.usecase.SetDocumentScanResult
import ch.admin.foitt.wallet.platform.navArgs.domain.model.EIdMrzSubmissionNavArg
import ch.admin.foitt.wallet.platform.navigation.NavigationManager
import ch.admin.foitt.wallet.platform.scaffold.domain.model.TopBarState
import ch.admin.foitt.wallet.platform.scaffold.domain.usecase.SetTopBarState
import ch.admin.foitt.wallet.platform.scaffold.presentation.ScreenViewModel
import ch.admin.foitt.walletcomposedestinations.destinations.MrzSubmissionScreenDestination
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class SDKScannerViewModel @Inject constructor(
    private val navManager: NavigationManager,
    private val avBeam: AVBeam,
    private val setDocumentScanResult: SetDocumentScanResult,
    setTopBarState: SetTopBarState,
) : ScreenViewModel(setTopBarState) {

    override val topBarState = TopBarState.None

    private val _infoState = MutableStateFlow(initCameraInfoHint())
    val infoState = _infoState.asStateFlow()

    private val _infoText = MutableStateFlow("")
    val infoText = _infoText.asStateFlow()

    private val _changeTBackCard = MutableStateFlow(false)
    val changeToBackCard = _changeTBackCard.asStateFlow()

    private val _screenSize = MutableStateFlow<Pair<Int, Int>?>(null)
    val screenSize: StateFlow<Pair<Int, Int>?> = _screenSize.asStateFlow()

    private val _surfaceView = MutableStateFlow<SurfaceView?>(null)
    val surfaceView: StateFlow<SurfaceView?> = _surfaceView.asStateFlow()

    private fun setScreenSize(width: Int, height: Int) {
        _screenSize.value = width to height
    }

    suspend fun onInitScan(width: Int, height: Int) {
        avBeam.initializedFlow
            .filter { it }
            .first()

        // Sometimes the app is faster than the lib and the GLView wasn't yet initialized
        delay(LIB_DELAY)

        val view = avBeam.getGLView(width, height)
        setScreenSize(view.width, view.height)
        _surfaceView.value = view
    }

    fun onAfterViewAttached() {
        _infoState.update { SDKInfoState.Ready }
        // Stop the camera feed so the SDK can rotate the feed
        avBeam.stopCamera()
        avBeam.startCamera()

        startScanDocument(avBeam)

        viewModelScope.launch {
            avBeam.statusFlow.collect { notif ->
                notif.let {
                    _infoState.update { SDKInfoState.InfoData }
                    _infoText.value = it.toString()

                    if (notif == AVBeamStatus.IdNeedSecondPageForMatching) {
                        _changeTBackCard.value = true
                    }
                }
            }
        }

        viewModelScope.launch {
            avBeam.errorFlow.collect { notif ->
                notif.let {
                    Timber.tag("SDK_PXL").d("Received error: $it")
                    // Received error: UnsupportedCameraResolution
                }
            }
        }

        viewModelScope.launch {
            avBeam.scanDocumentFlow.collect { notif ->
                when (notif) {
                    is AvBeamNotification.Loading -> {
                        _infoState.update { SDKInfoState.Loading }
                    }
                    is AvBeamNotification.DocumentScanCompleted -> {
                        avBeam.stopCamera()
                        avBeam.shutDown()
                        setDocumentScanResult(documentScanResult = notif.packageData)
                        navManager.navigateTo(
                            MrzSubmissionScreenDestination(
                                navArgs = EIdMrzSubmissionNavArg(
                                    mrzLines = notif.packageData.mrzValues.toTypedArray(),
                                )
                            )
                        )
                        _infoState.update { SDKInfoState.Empty }
                    }

                    is AvBeamNotification.Empty, is AvBeamNotification.Initial -> {
                        _infoState.update { SDKInfoState.Empty }
                    }

                    is AvBeamNotification.Error -> {
                        val packageResult = notif.packageData
                        Timber.tag("SDK_PXL").d("Empty package - ${packageResult?.errorType} type")
                        _infoText.value = "Empty package - ${packageResult?.errorType} type"
                    }
                }
            }
        }
    }

    private fun startScanDocument(avBeam: AVBeam) {
        val configScanning = AVBeamScanDocumentConfig(
            timeout = 30,
            rectWidth = screenSize.value!!.first,
            rectHeight = screenSize.value!!.second
        )

        viewModelScope.launch {
            avBeam.startScanDocument(configScanning)
        }
    }

    fun onUp() {
        avBeam.stopScanDocument()
        navManager.navigateUp()
    }

    fun onCloseToast() {
        _infoState.update { SDKInfoState.Empty }
    }

    private fun initCameraInfoHint(): SDKInfoState {
        return SDKInfoState.Loading
    }

    override fun onCleared() {
        avBeam.stopScanDocument()
        super.onCleared()
    }

    companion object {
        private const val LIB_DELAY = 10L
    }
}
