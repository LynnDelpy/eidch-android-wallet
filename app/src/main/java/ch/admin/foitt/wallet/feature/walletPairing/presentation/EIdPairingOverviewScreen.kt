package ch.admin.foitt.wallet.feature.walletPairing.presentation

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import ch.admin.foitt.wallet.R
import ch.admin.foitt.wallet.platform.composables.Buttons
import ch.admin.foitt.wallet.platform.composables.presentation.HeightReportingLayout
import ch.admin.foitt.wallet.platform.composables.presentation.addTopScaffoldPadding
import ch.admin.foitt.wallet.platform.composables.presentation.horizontalSafeDrawing
import ch.admin.foitt.wallet.platform.composables.presentation.layout.LazyColumn
import ch.admin.foitt.wallet.platform.composables.presentation.layout.WalletLayouts
import ch.admin.foitt.wallet.platform.composables.presentation.verticalSafeDrawing
import ch.admin.foitt.wallet.platform.navArgs.domain.model.EIdOnlineSessionNavArg
import ch.admin.foitt.wallet.platform.preview.WalletAllScreenPreview
import ch.admin.foitt.wallet.theme.Sizes
import ch.admin.foitt.wallet.theme.WalletTexts
import ch.admin.foitt.wallet.theme.WalletTheme
import com.ramcosta.composedestinations.annotation.Destination

@Destination(
    navArgsDelegate = EIdOnlineSessionNavArg::class
)
@Composable
fun EIdPairingOverviewScreen(
    viewModel: EIdPairingOverviewViewModel,
) {
    EIdParingOverviewScreenContent(
        onThisDeviceClick = viewModel::onThisDeviceClick,
        onAdditionalDevicesClick = viewModel::onAdditionalDevicesClick,
        onContinueClick = viewModel::onContinue,
        deviceName = viewModel.deviceName
    )
}

@Composable
private fun EIdParingOverviewScreenContent(
    onThisDeviceClick: () -> Unit,
    onAdditionalDevicesClick: () -> Unit,
    onContinueClick: () -> Unit = {},
    deviceName: String,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        var buttonHeight by remember { mutableStateOf(0.dp) }

        OverviewList(
            modifier = Modifier
                .fillMaxSize(),
            contentPadding = PaddingValues(bottom = Sizes.s06),
            onThisDeviceClick = onThisDeviceClick,
            onAdditionalDevicesClick = onAdditionalDevicesClick,
            deviceName = deviceName,
            buttonHeight = buttonHeight
        )
        HeightReportingLayout(
            modifier = Modifier.align(Alignment.BottomCenter),
            onContentHeightMeasured = { height -> buttonHeight = height },
        ) {
            Buttons.FilledPrimary(
                text = stringResource(id = R.string.tk_eidRequest_walletPairing_button_primary),
                onClick = onContinueClick,
                enabled = false,
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalSafeDrawing()
                    .horizontalSafeDrawing()
                    .padding(horizontal = Sizes.s04, vertical = Sizes.s04)
            )
        }
    }
}

@Composable
private fun ListHeader() {
    Column(
        modifier = Modifier
            .addTopScaffoldPadding()
            .padding(
                start = Sizes.s04,
                end = Sizes.s04,
            )
    ) {
        WalletTexts.TitleScreen(
            text = stringResource(id = R.string.tk_eidRequest_walletPairing_primary)
        )
        Spacer(modifier = Modifier.height(Sizes.s06))
        WalletTexts.BodyLarge(
            text = stringResource(id = R.string.tk_eidRequest_walletPairing_secondary)
        )
        Spacer(modifier = Modifier.height(Sizes.s06))
    }
}

@Composable
private fun OverviewList(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    onThisDeviceClick: () -> Unit,
    onAdditionalDevicesClick: () -> Unit,
    deviceName: String,
    buttonHeight: Dp,
) {
    WalletLayouts.LazyColumn(
        modifier = modifier
            .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal)),
        contentPadding = contentPadding,
    ) {
        item {
            ListHeader()
        }
        item {
            Sections(text = R.string.tk_eidRequest_walletPairing_currentDevice_sectionTitle)
        }
        item {
            EIdDeviceItem(
                onClick = { onThisDeviceClick() },
                title = R.string.tk_eidRequest_walletPairing_currentDevice_button_primary,
                subtitle = deviceName
            )
        }
        item {
            Sections(text = R.string.tk_eidRequest_walletPairing_additionalDevice_sectionTitle)
        }
        item {
            EIdDeviceItem(
                onClick = { onAdditionalDevicesClick() },
                title = R.string.tk_eidRequest_walletPairing_additionalDevice_button_primary
            )
        }
        item {
            Spacer(modifier = Modifier.height(buttonHeight))
        }
    }
}

@Composable
private fun Sections(
    modifier: Modifier = Modifier,
    @StringRes text: Int
) {
    Row(
        modifier = modifier
            .padding(start = Sizes.s04, top = Sizes.s03, end = Sizes.s06, bottom = Sizes.s03),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        WalletTexts.TitleSmall(
            modifier = Modifier.semantics { heading() },
            text = stringResource(id = text),
            color = WalletTheme.colorScheme.onSurface,
        )
    }
}

@WalletAllScreenPreview
@Composable
private fun EIdParingOverviewScreenPreview() {
    WalletTheme {
        EIdParingOverviewScreenContent(
            onThisDeviceClick = {},
            onAdditionalDevicesClick = {},
            deviceName = "Google Pixel 10"
        )
    }
}
