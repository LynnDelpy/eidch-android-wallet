package ch.admin.foitt.wallet.feature.eIdRequestVerification.presentation

import android.annotation.SuppressLint
import android.view.View
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ch.admin.foitt.wallet.R
import ch.admin.foitt.wallet.feature.eIdRequestVerification.presentation.composables.ScannerCamera
import ch.admin.foitt.wallet.feature.eIdRequestVerification.presentation.composables.ScannerInfoBox
import ch.admin.foitt.wallet.feature.eIdRequestVerification.presentation.model.SDKInfoState
import ch.admin.foitt.wallet.platform.composables.LoadingOverlay
import ch.admin.foitt.wallet.platform.navArgs.domain.model.EIdOnlineSessionNavArg
import ch.admin.foitt.wallet.platform.preview.WalletAllScreenPreview
import ch.admin.foitt.wallet.platform.scaffold.presentation.TopBarTitleOnly
import ch.admin.foitt.wallet.platform.utils.LocalActivity
import ch.admin.foitt.wallet.platform.utils.OnPauseEventHandler
import ch.admin.foitt.wallet.platform.utils.OnResumeEventHandler
import ch.admin.foitt.wallet.theme.Sizes
import ch.admin.foitt.wallet.theme.WalletTheme
import com.ramcosta.composedestinations.annotation.Destination

@Destination(
    navArgsDelegate = EIdOnlineSessionNavArg::class,
)
@Composable
fun EIdDocumentRecordingScreen(
    viewModel: EIdDocumentRecordingViewModel,
) {
    val currentActivity = LocalActivity.current
    OnResumeEventHandler(viewModel::onResume)
    OnPauseEventHandler(viewModel::onPause)
    BackHandler(enabled = true, viewModel::onBack)

    LaunchedEffect(viewModel) {
        viewModel.initScannerSdk(currentActivity)
    }

    EIdDocumentRecordingScreenContent(
        infoState = viewModel.infoState.collectAsStateWithLifecycle().value,
        infoText = viewModel.infoText.collectAsStateWithLifecycle().value,
        showSecondSide = viewModel.changeToBackCard.collectAsStateWithLifecycle().value,
        isLoading = viewModel.isLoading.collectAsStateWithLifecycle().value,
        getScannerView = viewModel::getScannerView,
        onAfterViewLayout = viewModel::onAfterViewLayout,
        onCloseToast = viewModel::onCloseToast,
    )
}

@SuppressLint("UnusedBoxWithConstraintsScope")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EIdDocumentRecordingScreenContent(
    infoState: SDKInfoState,
    infoText: String,
    showSecondSide: Boolean,
    isLoading: Boolean,
    getScannerView: suspend (width: Int, height: Int) -> View,
    onAfterViewLayout: (width: Int, height: Int) -> Unit,
    onCloseToast: () -> Unit,
) = Column(
    modifier = Modifier.Companion
        .background(color = WalletTheme.colorScheme.surface)
        .fillMaxSize()
        .windowInsetsPadding(WindowInsets.Companion.safeDrawing.only(WindowInsetsSides.Companion.Horizontal))
) {
    TopBarTitleOnly(
        titleId = if (showSecondSide) {
            R.string.tk_eidRequest_recordDocument_verso
        } else {
            R.string.tk_eidRequest_recordDocument_recto
        },
    )
    BoxWithConstraints {
        ScannerCamera(
            containerWidth = constraints.maxWidth,
            containerHeight = constraints.maxHeight,
            getScannerView = getScannerView,
            onAfterViewLayout = onAfterViewLayout,
        )
        if (!isLoading) {
            ScanBox(
                showSecondSide = showSecondSide,
                modifier = Modifier.Companion.align(Alignment.Companion.Center)
            )
            ScannerInfoBox(
                infoState = infoState,
                onClose = onCloseToast,
                infoText = infoText,
                modifier = Modifier.Companion
                    .align(Alignment.Companion.BottomCenter)
                    .padding(bottom = Sizes.s05)
            )
        }
    }
    LoadingOverlay(isLoading)
}

@Composable
private fun ScanBox(
    showSecondSide: Boolean,
    modifier: Modifier,
) {
    val rotation = remember { Animatable(0f) }
    var currentCardFront by remember { mutableStateOf(true) }

    LaunchedEffect(showSecondSide) {
        if (showSecondSide && currentCardFront) {
            rotation.animateTo(
                targetValue = 90f,
                animationSpec = tween(durationMillis = 250, easing = LinearEasing)
            )
            currentCardFront = false
            rotation.animateTo(
                targetValue = 180f,
                animationSpec = tween(durationMillis = 250, easing = LinearEasing)
            )
            rotation.snapTo(targetValue = 0f)
        } else {
            currentCardFront = true
        }
    }

    val overlayRes = if (showSecondSide) {
        R.drawable.wallet_id_card_back_overlay
    } else {
        R.drawable.wallet_id_card_front_overlay
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .navigationBarsPadding(),
        contentAlignment = Alignment.Companion.Center,
    ) {
        Image(
            painter = painterResource(id = overlayRes),
            contentDescription = null,
            contentScale = ContentScale.Companion.Fit,
            colorFilter = ColorFilter.Companion.tint(WalletTheme.colorScheme.onPrimaryFixed),
            modifier = Modifier.Companion
                .sizeIn(maxWidth = 1200.dp, maxHeight = 1200.dp)
                .graphicsLayer {
                    rotationY = rotation.value
                    cameraDistance = 8 * density
                }
        )
    }
}

@WalletAllScreenPreview
@Composable
private fun EIdDocumentRecordingPreview() {
    WalletTheme {
        val currentContext = LocalContext.current
        EIdDocumentRecordingScreenContent(
            infoState = SDKInfoState.InfoData,
            infoText = "Please look straight at the camera",
            showSecondSide = false,
            isLoading = false,
            getScannerView = { _, _ -> View(currentContext) },
            onAfterViewLayout = { _, _ -> },
            onCloseToast = {},
        )
    }
}
