package ru.xrtz.whichstate.core.domain.policy

class AdFrequencyPolicy(private val everyNRounds: Int = 3) {
    fun shouldShow(roundsSinceInterstitial: Int): Boolean = roundsSinceInterstitial >= everyNRounds
}
