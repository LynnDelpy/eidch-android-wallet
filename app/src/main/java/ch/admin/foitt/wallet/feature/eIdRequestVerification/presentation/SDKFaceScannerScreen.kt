package ch.admin.foitt.wallet.feature.eIdRequestVerification.presentation

import android.annotation.SuppressLint
import android.content.Context
import android.util.DisplayMetrics
import android.view.SurfaceView
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
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
import ch.admin.foitt.wallet.platform.composables.presentation.TopRoundCornersOverlay
import ch.admin.foitt.wallet.platform.navArgs.domain.model.EIdOnlineSessionNavArg
import ch.admin.foitt.wallet.platform.preview.WalletAllScreenPreview
import ch.admin.foitt.wallet.platform.scaffold.presentation.LocalScaffoldPaddings
import ch.admin.foitt.wallet.platform.utils.LocalActivity
import ch.admin.foitt.wallet.platform.utils.lockOrientation
import ch.admin.foitt.wallet.platform.utils.unlockOrientation
import ch.admin.foitt.wallet.theme.Sizes
import ch.admin.foitt.wallet.theme.WalletTheme
import com.ramcosta.composedestinations.annotation.Destination

@Destination(
    navArgsDelegate = EIdOnlineSessionNavArg::class,
)
@Composable
fun SDKFaceScannerScreen(
    viewModel: SDKFaceScannerViewModel,
) {
    val activity = LocalActivity.current
    val density = LocalDensity.current

    val displayMetrics = remember {
        DisplayMetrics().apply {
            val windowManager = activity.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            @Suppress("DEPRECATION")
            windowManager.defaultDisplay.getMetrics(this)
        }
    }

    val statusBar = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()

    val topOffsetPx = with(density) {
        statusBar.roundToPx()
    }

    val maxWidth = displayMetrics.widthPixels
    val maxHeight = displayMetrics.heightPixels

    val shouldLock by viewModel.lockOrientation.collectAsStateWithLifecycle()
    val surfaceView by viewModel.surfaceView.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.onInitScan(activity, maxWidth, maxHeight)
    }

    LaunchedEffect(shouldLock) {
        activity.let {
            if (shouldLock) {
                it.lockOrientation()
            } else {
                it.unlockOrientation()
            }
        }
    }

    FaceScannerScreenContent(
        infoState = viewModel.infoState.collectAsStateWithLifecycle().value,
        infoText = viewModel.infoText.collectAsStateWithLifecycle().value,
        surfaceView = surfaceView,
        topLayerSize = topOffsetPx,
        onAfterViewAttached = viewModel::onAfterViewAttached,
        onCloseToast = viewModel::onCloseToast,
    )
}

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
private fun FaceScannerScreenContent(
    infoState: SDKInfoState,
    infoText: String,
    surfaceView: SurfaceView?,
    topLayerSize: Int,
    onAfterViewAttached: () -> Unit,
    onCloseToast: () -> Unit,
) = Column(
    modifier = Modifier
        .padding(top = LocalScaffoldPaddings.current.calculateTopPadding())
        .fillMaxSize()
        .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal))
) {
    BoxWithConstraints {
        val containerWidth = constraints.maxWidth
        val containerHeight = constraints.maxHeight

        Camera(
            surfaceView = surfaceView,
            containerWidth = containerWidth,
            containerHeight = containerHeight,
            onAfterViewAttached = onAfterViewAttached,
        )

        TopLayer(topLayerSize = topLayerSize)

        if (infoState == SDKInfoState.Loading) {
            LoadingOverlay(showOverlay = true)
        } else {
            ScanBox(
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

    if (surfaceView != null) {
        AndroidView(
            factory = {
                FrameLayout(context).apply {
                    (surfaceView.parent as? ViewGroup)?.removeView(surfaceView)
                    addView(
                        surfaceView,
                        FrameLayout.LayoutParams(containerWidth, containerHeight)
                    )
                }
            }
        )
        LaunchedEffect(surfaceView) {
            onAfterViewAttached()
        }
    }
}

@Composable
private fun TopLayer(
    topLayerSize: Int,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .height(topLayerSize.dp)
            .background(
                color = WalletTheme.colorScheme.surface,
            )
    )

    Box(modifier = Modifier.padding(top = topLayerSize.dp)) {
        TopRoundCornersOverlay()
    }
}

@Composable
private fun ScanBox(
    modifier: Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .navigationBarsPadding(),
        contentAlignment = Alignment.Center,
    ) {
        Image(
            painter = painterResource(id = R.drawable.wallet_facescanner_overlay),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            colorFilter = ColorFilter.tint(WalletTheme.colorScheme.onPrimaryFixed),
            modifier = Modifier
                .sizeIn(maxWidth = 1200.dp, maxHeight = 1200.dp)
        )
    }
}

private data class FaceScannerPreviewParams(
    val infoState: SDKInfoState,
)

private class FaceScannerPreviewParamsProvider : PreviewParameterProvider<FaceScannerPreviewParams> {
    override val values = sequenceOf(
        FaceScannerPreviewParams(
            infoState = SDKInfoState.InfoData
        ),
    )
}

@WalletAllScreenPreview
@Composable
private fun DocumentScannerPreview(
    @PreviewParameter(FaceScannerPreviewParamsProvider::class) previewParams: FaceScannerPreviewParams,
) {
    WalletTheme {
        FaceScannerScreenContent(
            surfaceView = null,
            onAfterViewAttached = {},
            infoState = previewParams.infoState,
            topLayerSize = 100,
            onCloseToast = {},
            infoText = "something",
        )
    }
}
