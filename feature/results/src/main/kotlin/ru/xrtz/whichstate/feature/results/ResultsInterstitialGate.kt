package ru.xrtz.whichstate.feature.results

import android.app.Activity
import ru.xrtz.whichstate.core.ads.InterstitialAds
import ru.xrtz.whichstate.core.domain.usecase.RecordInterstitialShownUseCase
import ru.xrtz.whichstate.core.domain.usecase.ShouldShowInterstitialUseCase
import javax.inject.Inject

class ResultsInterstitialGate @Inject constructor(
    private val shouldShowInterstitial: ShouldShowInterstitialUseCase,
    private val recordInterstitialShown: RecordInterstitialShownUseCase,
    private val interstitialAds: InterstitialAds,
) {
    private var dueOnExit = false

    fun prepareForExit() {
        dueOnExit = shouldShowInterstitial()
        if (dueOnExit) interstitialAds.preload()
    }

    suspend fun showIfDue(activity: Activity) {
        if (dueOnExit && interstitialAds.show(activity)) {
            recordInterstitialShown()
        }
    }
}
