package ru.xrtz.whichstate.core.ads.yandex

import ru.xrtz.whichstate.core.ads.AdPlacement

internal object AdUnitIds {
    val interstitial: String get() = BuildConfig.INTERSTITIAL_AD_UNIT_ID

    fun banner(placement: AdPlacement): String = when (placement) {
        AdPlacement.MENU -> BuildConfig.BANNER_MENU_AD_UNIT_ID
        AdPlacement.RESULTS -> BuildConfig.BANNER_RESULTS_AD_UNIT_ID
    }
}
