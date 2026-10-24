package ru.xrtz.whichstate.core.domain.repository

interface AdSessionRepository {
    fun roundsSinceInterstitial(): Int
    fun onRoundFinished()
    fun onInterstitialShown()
}
