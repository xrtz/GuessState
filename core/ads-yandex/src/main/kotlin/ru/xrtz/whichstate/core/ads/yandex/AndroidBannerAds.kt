package ru.xrtz.whichstate.core.ads.yandex

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.yandex.mobile.ads.banner.BannerAdEventListener
import com.yandex.mobile.ads.banner.BannerAdSize
import com.yandex.mobile.ads.banner.BannerAdView
import com.yandex.mobile.ads.common.AdRequest
import com.yandex.mobile.ads.common.AdRequestError
import com.yandex.mobile.ads.common.ImpressionData
import ru.xrtz.whichstate.core.ads.AdPlacement
import ru.xrtz.whichstate.core.ads.BannerAds
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.roundToInt

private const val MAX_INLINE_HEIGHT_DP = 100

@Singleton
internal class AndroidBannerAds @Inject constructor(
    private val initializer: YandexAdsInitializer,
) : BannerAds {
    @Composable
    override fun Banner(placement: AdPlacement, modifier: Modifier) {
        val context = LocalContext.current

        val bannerView = remember(placement) { BannerAdView(context) }
        var isLoaded by remember(placement) { mutableStateOf(false) }

        LaunchedEffect(bannerView) {
            initializer.awaitReady()
            val density = context.resources.displayMetrics.density
            val widthDp = (context.resources.displayMetrics.widthPixels / density).roundToInt()
            bannerView.setAdSize(BannerAdSize.inline(context, widthDp, MAX_INLINE_HEIGHT_DP))
            bannerView.setBannerAdEventListener(
                object : BannerAdEventListener {
                    override fun onAdLoaded() {
                        isLoaded = true
                    }

                    override fun onAdFailedToLoad(error: AdRequestError) {
                        isLoaded = false
                    }

                    override fun onAdClicked() = Unit
                    override fun onImpression(impressionData: ImpressionData?) = Unit
                },
            )
            bannerView.loadAd(AdRequest.Builder(AdUnitIds.banner(placement)).build())
        }

        Box(modifier = if (isLoaded) modifier.fillMaxWidth().wrapContentHeight() else Modifier.size(0.dp)) {
            AndroidView(modifier = Modifier.fillMaxWidth(), factory = { bannerView }, onRelease = { it.destroy() })
        }
    }
}
