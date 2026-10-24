package ru.xrtz.whichstate.core.ads

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

interface BannerAds {
    @Composable
    fun Banner(placement: AdPlacement, modifier: Modifier)
}
