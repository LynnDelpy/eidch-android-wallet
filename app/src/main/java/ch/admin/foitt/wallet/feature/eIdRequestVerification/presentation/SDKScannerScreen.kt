package ch.admin.foitt.wallet.feature.eIdRequestVerification.presentation

import android.annotation.SuppressLint
import android.content.Context
import android.util.DisplayMetrics
import android.view.SurfaceView
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ch.admin.foitt.wallet.R
import ch.admin.foitt.wallet.feature.eIdRequestVerification.presentation.composables.ScannerInfoBox
import ch.admin.foitt.wallet.feature.eIdRequestVerification.presentation.model.SDKInfoState
import ch.admin.foitt.wallet.platform.composables.LoadingOverlay
import ch.admin.foitt.wallet.platform.preview.WalletAllScreenPreview
import ch.admin.foitt.wallet.platform.scaffold.presentation.TopBarBackArrow
import ch.admin.foitt.wallet.theme.Sizes
import ch.admin.foitt.wallet.theme.WalletTheme
import ch.admin.foitt.wallet.theme.WalletTopBarColors
import com.ramcosta.composedestinations.annotation.Destination

@Composable
@Destination
fun SDKScannerScreen(
    viewModel: SDKScannerViewModel,
) {
    val context = LocalContext.current

    val displayMetrics = remember {
        DisplayMetrics().apply {
            val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            @Suppress("DEPRECATION")
            windowManager.defaultDisplay.getMetrics(this)
        }
    }

    val density = LocalDensity.current
    val statusBar = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()

    val topOffsetPx = with(density) {
        val topBar = 10.dp
        (statusBar + topBar).roundToPx()
    }

    val maxWidth = displayMetrics.widthPixels
    val maxHeight = displayMetrics.heightPixels - topOffsetPx

    val surfaceView by viewModel.surfaceView.collectAsStateWithLifecycle()
    LaunchedEffect(Unit) {
        viewModel.onInitScan(maxWidth, maxHeight)
    }

    DocumentScannerScreenContent(
        infoState = viewModel.infoState.collectAsStateWithLifecycle().value,
        infoText = viewModel.infoText.collectAsStateWithLifecycle().value,
        surfaceView = surfaceView,
        changeSecondCard = viewModel.changeToBackCard.collectAsStateWithLifecycle().value,
        onAfterViewAttached = viewModel::onAfterViewAttached,
        onUp = viewModel::onUp,
        onCloseToast = viewModel::onCloseToast,
    )
}

@SuppressLint("UnusedBoxWithConstraintsScope")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DocumentScannerScreenContent(
    infoState: SDKInfoState,
    infoText: String,
    changeSecondCard: Boolean,
    surfaceView: SurfaceView?,
    onAfterViewAttached: () -> Unit,
    onUp: () -> Unit,
    onCloseToast: () -> Unit,
) = Column(
    modifier = Modifier
        .fillMaxSize()
        .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal))
) {
    TopBarBackArrow(
        titleId = null,
        showButtonBackground = false,
        onUp = onUp,
        actionButton = {},
        colors = WalletTopBarColors.transparent(),
    )
    BoxWithConstraints {
        val containerWidth = constraints.maxWidth
        val containerHeight = constraints.maxHeight

        Camera(
            surfaceView = surfaceView,
            containerWidth = containerWidth,
            containerHeight = containerHeight,
            onAfterViewAttached = onAfterViewAttached,
        )
        if (infoState == SDKInfoState.Loading) {
            LoadingOverlay(showOverlay = true)
        } else {
            ScanBox(
                changeToBackCard = changeSecondCard,
                modifier = Modifier.align(Alignment.Center)
            )
            ScannerInfoBox(
                infoState = infoState,
                onClose = onCloseToast,
                infoText = infoText,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = Sizes.s05)
            )
        }
    }
}

@Composable
private fun Camera(
    surfaceView: SurfaceView?,
    containerWidth: Int,
    containerHeight: Int,
    onAfterViewAttached: () -> Unit,
) {
    val context = LocalContext.current
    val cameraAspectRatio = remember { mutableFloatStateOf(9f / 16f) } // default fallback

    if (surfaceView != null) {
        val ratio = cameraAspectRatio.floatValue
        val containerAspectRatio = containerWidth.toFloat() / containerHeight.toFloat()

        val scaledWidth: Int
        val scaledHeight: Int

        if (ratio > containerAspectRatio) {
            // Camera is wider than container — crop width
            scaledHeight = containerHeight
            scaledWidth = (scaledHeight * ratio).toInt()
        } else {
            // Camera is taller than container — crop height
            scaledWidth = containerWidth
            scaledHeight = (scaledWidth / ratio).toInt()
        }

        AndroidView(
            factory = {
                FrameLayout(context).apply {
                    (surfaceView.parent as? ViewGroup)?.removeView(surfaceView)
                    addView(
                        surfaceView,
                        FrameLayout.LayoutParams(scaledWidth, scaledHeight)
                    )
                }
            },
            update = {
                val layoutParams = surfaceView.layoutParams
                if (layoutParams.width != scaledWidth || layoutParams.height != scaledHeight) {
                    layoutParams.width = scaledWidth
                    layoutParams.height = scaledHeight
                    surfaceView.layoutParams = layoutParams
                }
            },
            modifier = Modifier
                .sizeIn(maxWidth = scaledWidth.dp, maxHeight = scaledHeight.dp)
                .clip(
                    RoundedCornerShape(
                        topStart = Sizes.boxCornerSize,
                        topEnd = Sizes.boxCornerSize,
                    )
                )
        )
        LaunchedEffect(surfaceView) {
            onAfterViewAttached()
        }
    }
}

@Composable
private fun ScanBox(
    changeToBackCard: Boolean,
    modifier: Modifier,
) {
    val rotation = remember { Animatable(0f) }
    var currentCardFront by remember { mutableStateOf(true) }

    LaunchedEffect(changeToBackCard) {
        if (changeToBackCard) {
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

    val overlayRes = if (changeToBackCard) {
        R.drawable.wallet_id_card_back_overlay
    } else {
        R.drawable.wallet_id_card_front_overlay
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .navigationBarsPadding(),
        contentAlignment = Alignment.Center,
    ) {
        Image(
            painter = painterResource(id = overlayRes),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            colorFilter = ColorFilter.tint(WalletTheme.colorScheme.onPrimaryFixed),
            modifier = Modifier
                .rotate(90f)
                .sizeIn(maxWidth = 1200.dp, maxHeight = 1200.dp)
                .graphicsLayer {
                    rotationY = rotation.value
                    cameraDistance = 8 * density
                }
        )
    }
}

private data class DocumentScannerPreviewParams(
    val infoState: SDKInfoState,
)

private class DocumentScannerPreviewParamsProvider : PreviewParameterProvider<DocumentScannerPreviewParams> {
    override val values = sequenceOf(
        DocumentScannerPreviewParams(
            infoState = SDKInfoState.InfoData
        ),
    )
}

@WalletAllScreenPreview
@Composable
private fun DocumentScannerPreview(
    @PreviewParameter(DocumentScannerPreviewParamsProvider::class) previewParams: DocumentScannerPreviewParams,
) {
    WalletTheme {
        DocumentScannerScreenContent(
            surfaceView = null,
            onAfterViewAttached = {},
            infoState = previewParams.infoState,
            onUp = {},
            onCloseToast = {},
            infoText = "something",
            changeSecondCard = false,
        )
    }
}
