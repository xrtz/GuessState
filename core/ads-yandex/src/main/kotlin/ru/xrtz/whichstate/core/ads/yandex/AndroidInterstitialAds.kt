package ru.xrtz.whichstate.core.ads.yandex

import android.app.Activity
import android.content.Context
import com.yandex.mobile.ads.common.AdError
import com.yandex.mobile.ads.common.AdRequest
import com.yandex.mobile.ads.common.AdRequestError
import com.yandex.mobile.ads.common.ImpressionData
import com.yandex.mobile.ads.interstitial.InterstitialAd
import com.yandex.mobile.ads.interstitial.InterstitialAdEventListener
import com.yandex.mobile.ads.interstitial.InterstitialAdLoadListener
import com.yandex.mobile.ads.interstitial.InterstitialAdLoader
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import ru.xrtz.whichstate.core.ads.InterstitialAds
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume

@Singleton
internal class AndroidInterstitialAds @Inject constructor(
    @ApplicationContext private val context: Context,
    private val initializer: YandexAdsInitializer,
) : InterstitialAds {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private val loadMutex = Mutex()
    private var loadedAd: InterstitialAd? = null

    override fun preload() {
        scope.launch { ensureLoaded() }
    }

    override suspend fun show(activity: Activity): Boolean {
        val ad = ensureLoaded() ?: return false
        loadedAd = null
        return awaitDismissal(ad, activity)
    }

    private suspend fun ensureLoaded(): InterstitialAd? = loadMutex.withLock {
        loadedAd?.let { return@withLock it }
        initializer.awaitReady()
        val request = AdRequest.Builder(AdUnitIds.interstitial).build()
        val ad = suspendCancellableCoroutine { cont ->
            InterstitialAdLoader(context).loadAd(
                request,
                object : InterstitialAdLoadListener {
                    override fun onAdLoaded(interstitialAd: InterstitialAd) {
                        if (cont.isActive) cont.resume(interstitialAd)
                    }

                    override fun onAdFailedToLoad(adRequestError: AdRequestError) {
                        if (cont.isActive) cont.resume(null)
                    }
                },
            )
        }
        loadedAd = ad
        ad
    }

    private suspend fun awaitDismissal(ad: InterstitialAd, activity: Activity): Boolean =
        suspendCancellableCoroutine { cont ->
            var resumed = false
            fun finish(result: Boolean) {
                if (resumed) return
                resumed = true
                if (cont.isActive) cont.resume(result)
            }
            ad.setAdEventListener(
                object : InterstitialAdEventListener {
                    override fun onAdShown() = Unit
                    override fun onAdFailedToShow(adError: AdError) = finish(false)
                    override fun onAdDismissed() = finish(true)
                    override fun onAdClicked() = Unit
                    override fun onAdImpression(impressionData: ImpressionData?) = Unit
                },
            )
            ad.show(activity)
        }
}
