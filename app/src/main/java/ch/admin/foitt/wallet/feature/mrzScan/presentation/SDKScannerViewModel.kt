package ch.admin.foitt.wallet.feature.mrzScan.presentation

import android.view.SurfaceView
import androidx.lifecycle.viewModelScope
import ch.admin.foitt.avwrapper.AVBeam
import ch.admin.foitt.avwrapper.AVBeamPackageResult
import ch.admin.foitt.avwrapper.AVBeamStatus
import ch.admin.foitt.avwrapper.AvBeamNotification
import ch.admin.foitt.avwrapper.config.AVBeamScanDocumentConfig
import ch.admin.foitt.wallet.feature.mrzScan.domain.model.GetImportantMrzKeysError
import ch.admin.foitt.wallet.feature.mrzScan.domain.model.MrzScanError
import ch.admin.foitt.wallet.feature.mrzScan.domain.model.toGetImportantMrzKeysError
import ch.admin.foitt.wallet.platform.eIdApplicationProcess.domain.model.ApplyRequest
import ch.admin.foitt.wallet.platform.eIdApplicationProcess.domain.model.MrzData
import ch.admin.foitt.wallet.platform.navArgs.domain.model.EIdMrzResultNavArg
import ch.admin.foitt.wallet.platform.navigation.NavigationManager
import ch.admin.foitt.wallet.platform.scaffold.domain.model.TopBarState
import ch.admin.foitt.wallet.platform.scaffold.domain.usecase.SetTopBarState
import ch.admin.foitt.wallet.platform.scaffold.presentation.ScreenViewModel
import ch.admin.foitt.walletcomposedestinations.destinations.ErrorScreenDestination
import ch.admin.foitt.walletcomposedestinations.destinations.MrzSubmissionScreenDestination
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.coroutines.runSuspendCatching
import com.github.michaelbull.result.mapError
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
    setTopBarState: SetTopBarState,
) : ScreenViewModel(setTopBarState, systemBarsFixedLightColor = true) {

    override val topBarState = TopBarState.None

    private val _infoState = MutableStateFlow(initCameraInfoHint())
    val infoState = _infoState.asStateFlow()

    private val _infoText = MutableStateFlow("")
    val infoText = _infoText.asStateFlow()

    private val _changeTBackCard = MutableStateFlow(false)
    val changeToBackCard = _changeTBackCard.asStateFlow()

    private val _screenSize = MutableStateFlow<Pair<Int, Int>?>(null)
    val screenSize: StateFlow<Pair<Int, Int>?> = _screenSize

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

        val statusFlow = avBeam.statusFlow
        val errorFlow = avBeam.errorFlow
        val documentFlow = avBeam.scanDocumentFlow
        viewModelScope.launch {
            statusFlow.collect { notif ->
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
            errorFlow.collect { notif ->
                notif.let {
                    Timber.tag("SDK_PXL").d("Received error: $it")
                    // Received error: UnsupportedCameraResolution
                }
            }
        }

        viewModelScope.launch {
            documentFlow.collect { notif ->
                when (notif) {
                    is AvBeamNotification.Completed -> {
                        val packageResult: AVBeamPackageResult? = notif.packageData
                        if (packageResult != null) {
                            _infoState.update { SDKInfoState.Loading }

                            val mrzValues = getMrzValues(packageResult)

                            if (mrzValues.isOk) {
                                val request = ApplyRequest(
                                    mrz = mrzValues.value,
                                    legalRepresentant = false,
                                )

                                navManager.navigateTo(
                                    MrzSubmissionScreenDestination(
                                        navArgs = EIdMrzResultNavArg(
                                            mrzData = MrzData(
                                                displayName = "",
                                                payload = request
                                            )
                                        )
                                    )
                                )
                            } else {
                                navigateToErrorScreen()
                            }
                        } else {
                            _infoState.update { SDKInfoState.UnexpectedError }
                        }
                    }

                    is AvBeamNotification.Empty -> {
                        _infoState.update { SDKInfoState.Empty }
                    }

                    is AvBeamNotification.Error -> {
                        Timber.tag("SDK_PXL").d("Empty package - ${notif.packageData?.errorType} type")
                        _infoText.value = "Empty package - ${notif.packageData?.errorType} type"
                    }

                    else -> {
                        // Handle other notification types
                        Timber.tag("SDK_PXL").d("Something went wrong with the SDK")
                        _infoText.value = "Something went wrong with the SDK"
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

    private fun replaceStarWithLessThan(value: String): String {
        return value.replace("*", "<")
    }

    private fun getMrzValues(packageResult: AVBeamPackageResult): Result<List<String>, GetImportantMrzKeysError> {
        return runSuspendCatching {
            if (packageResult.importantDataKeys.isEmpty()) {
                _infoState.update { SDKInfoState.UnexpectedError }
                MrzScanError.Unexpected(cause = IllegalStateException("importantDataKeys is empty"))
            }

            listOf(
                replaceStarWithLessThan(packageResult.data?.getValue(packageResult.importantDataKeys[0]) ?: ""),
                replaceStarWithLessThan(packageResult.data?.getValue(packageResult.importantDataKeys[1]) ?: ""),
                replaceStarWithLessThan(packageResult.data?.getValue(packageResult.importantDataKeys[2]) ?: "")
            )
        }.mapError { throwable -> throwable.toGetImportantMrzKeysError("importantDataKeys is empty") }
    }

    private fun initCameraInfoHint(): SDKInfoState {
        return SDKInfoState.Loading
    }

    override fun onCleared() {
        avBeam.stopScanDocument()
        super.onCleared()
    }

    fun navigateToErrorScreen() {
        navManager.navigateToAndClearCurrent(ErrorScreenDestination())
    }

    companion object {
        private const val LIB_DELAY = 10L
    }
}
