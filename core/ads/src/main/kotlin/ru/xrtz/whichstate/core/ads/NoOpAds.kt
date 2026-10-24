package ru.xrtz.whichstate.core.ads

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

object NoOpAds : InterstitialAds, BannerAds {
    override fun preload() = Unit

    override suspend fun show(activity: Activity): Boolean = false

    @Composable
    override fun Banner(placement: AdPlacement, modifier: Modifier) = Unit
}
