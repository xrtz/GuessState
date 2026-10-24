package ru.xrtz.whichstate.core.testing.fake

import ru.xrtz.whichstate.core.domain.repository.AdSessionRepository

class FakeAdSessionRepository(initialRounds: Int = 0) : AdSessionRepository {
    var rounds: Int = initialRounds
        private set
    var interstitialShownCount: Int = 0
        private set

    override fun roundsSinceInterstitial(): Int = rounds

    override fun onRoundFinished() {
        rounds++
    }

    override fun onInterstitialShown() {
        interstitialShownCount++
        rounds = 0
    }
}
