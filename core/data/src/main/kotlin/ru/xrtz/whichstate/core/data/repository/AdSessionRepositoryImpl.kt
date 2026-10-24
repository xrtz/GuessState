package ru.xrtz.whichstate.core.data.repository

import ru.xrtz.whichstate.core.domain.repository.AdSessionRepository
import java.util.concurrent.atomic.AtomicInteger
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class AdSessionRepositoryImpl @Inject constructor() : AdSessionRepository {
    private val roundsSinceLastInterstitial = AtomicInteger(0)

    override fun roundsSinceInterstitial(): Int = roundsSinceLastInterstitial.get()

    override fun onRoundFinished() {
        roundsSinceLastInterstitial.incrementAndGet()
    }

    override fun onInterstitialShown() {
        roundsSinceLastInterstitial.set(0)
    }
}
