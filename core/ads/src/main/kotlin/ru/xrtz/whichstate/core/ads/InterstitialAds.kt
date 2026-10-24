package ru.xrtz.whichstate.core.ads

import android.app.Activity

interface InterstitialAds {
    fun preload()

    suspend fun show(activity: Activity): Boolean
}
