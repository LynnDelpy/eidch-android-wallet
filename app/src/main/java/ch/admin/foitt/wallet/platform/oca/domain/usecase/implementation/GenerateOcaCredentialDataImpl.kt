package ch.admin.foitt.wallet.platform.oca.domain.usecase.implementation

import ch.admin.foitt.wallet.platform.oca.domain.model.BrandingData
import ch.admin.foitt.wallet.platform.oca.domain.model.CaptureBase
import ch.admin.foitt.wallet.platform.oca.domain.model.Locale
import ch.admin.foitt.wallet.platform.oca.domain.model.MetaData
import ch.admin.foitt.wallet.platform.oca.domain.model.OcaCredentialData
import ch.admin.foitt.wallet.platform.oca.domain.model.overlays.BrandingOverlay
import ch.admin.foitt.wallet.platform.oca.domain.model.overlays.BrandingOverlay1x1
import ch.admin.foitt.wallet.platform.oca.domain.model.overlays.MetaOverlay
import ch.admin.foitt.wallet.platform.oca.domain.model.overlays.MetaOverlay1x0
import ch.admin.foitt.wallet.platform.oca.domain.model.overlays.Overlay
import ch.admin.foitt.wallet.platform.oca.domain.usecase.GenerateOcaCredentialData
import ch.admin.foitt.wallet.platform.oca.domain.util.getLatestOverlaysOfType
import javax.inject.Inject

class GenerateOcaCredentialDataImpl @Inject constructor() : GenerateOcaCredentialData {
    override fun invoke(
        rootCaptureBase: CaptureBase,
        overlays: List<Overlay>
    ): List<OcaCredentialData> {
        val localizedBranding = getBrandingPerLocale(rootCaptureBase, overlays)
        val localizedMeta = getMetaPerLocale(rootCaptureBase, overlays)

        val locales = localizedBranding.keys + localizedMeta.keys
        return locales.map { locale ->
            val meta = localizedMeta[locale]
            val branding = localizedBranding[locale]

            OcaCredentialData(
                captureBaseDigest = rootCaptureBase.digest,
                locale = locale,
                theme = branding?.theme,
                name = meta?.name,
                description = branding?.primaryField ?: meta?.description,
                logoData = branding?.logoData,
                backgroundColor = branding?.backgroundColor
            )
        }
    }

    private fun getMetaPerLocale(rootCaptureBase: CaptureBase, overlays: List<Overlay>): Map<Locale, MetaData> {
        val metaOverlays = getLatestOverlaysOfType<MetaOverlay>(overlays, rootCaptureBase.digest)
        return metaOverlays.associate { metaOverlay ->
            val metaData = when (metaOverlay) {
                is MetaOverlay1x0 -> MetaData(metaOverlay.name, metaOverlay.description)
            }

            metaOverlay.language to metaData
        }
    }

    private fun getBrandingPerLocale(rootCaptureBase: CaptureBase, overlays: List<Overlay>): Map<Locale, BrandingData> {
        val brandingOverlays = getLatestOverlaysOfType<BrandingOverlay>(overlays, rootCaptureBase.digest)
        return brandingOverlays.associate { brandingOverlay ->
            val brandingData = when (brandingOverlay) {
                is BrandingOverlay1x1 -> BrandingData(
                    theme = brandingOverlay.theme,
                    primaryField = brandingOverlay.primaryField,
                    logoData = brandingOverlay.logo,
                    backgroundColor = brandingOverlay.primaryBackgroundColor
                )
            }
            brandingOverlay.language to brandingData
        }
    }
}
