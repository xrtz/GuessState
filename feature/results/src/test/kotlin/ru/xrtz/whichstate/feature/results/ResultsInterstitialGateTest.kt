package ru.xrtz.whichstate.feature.results

import android.app.Activity
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import ru.xrtz.whichstate.core.ads.InterstitialAds
import ru.xrtz.whichstate.core.domain.policy.AdFrequencyPolicy
import ru.xrtz.whichstate.core.domain.usecase.RecordInterstitialShownUseCase
import ru.xrtz.whichstate.core.domain.usecase.ShouldShowInterstitialUseCase
import ru.xrtz.whichstate.core.testing.fake.FakeAdSessionRepository
import kotlin.test.assertEquals

private const val EVERY_N_ROUNDS = 3

internal class FakeInterstitialAds(private val showResult: Boolean = true) : InterstitialAds {
    var preloadCount = 0
        private set
    var showCount = 0
        private set

    override fun preload() {
        preloadCount++
    }

    override suspend fun show(activity: Activity): Boolean {
        showCount++
        return showResult
    }
}

@RunWith(RobolectricTestRunner::class)
class ResultsInterstitialGateTest {
    private val activity: Activity = Robolectric.buildActivity(Activity::class.java).get()

    private fun gate(adSession: FakeAdSessionRepository, ads: FakeInterstitialAds) = ResultsInterstitialGate(
        ShouldShowInterstitialUseCase(adSession, AdFrequencyPolicy(EVERY_N_ROUNDS)),
        RecordInterstitialShownUseCase(adSession),
        ads,
    )

    @Test
    fun `not due - prepareForExit never preloads, showIfDue never shows`() = runTest {
        val adSession = FakeAdSessionRepository(initialRounds = EVERY_N_ROUNDS - 1)
        val ads = FakeInterstitialAds()
        val gate = gate(adSession, ads)

        gate.prepareForExit()
        gate.showIfDue(activity)

        assertEquals(0, ads.preloadCount)
        assertEquals(0, ads.showCount)
        assertEquals(EVERY_N_ROUNDS - 1, adSession.rounds)
    }

    @Test
    fun `due - prepareForExit preloads, showIfDue shows and resets the counter`() = runTest {
        val adSession = FakeAdSessionRepository(initialRounds = EVERY_N_ROUNDS)
        val ads = FakeInterstitialAds(showResult = true)
        val gate = gate(adSession, ads)

        gate.prepareForExit()
        assertEquals(1, ads.preloadCount)

        gate.showIfDue(activity)

        assertEquals(1, ads.showCount)
        assertEquals(0, adSession.rounds)
        assertEquals(1, adSession.interstitialShownCount)
    }

    @Test
    fun `due but show fails - counter is not reset`() = runTest {
        val adSession = FakeAdSessionRepository(initialRounds = EVERY_N_ROUNDS)
        val ads = FakeInterstitialAds(showResult = false)
        val gate = gate(adSession, ads)

        gate.prepareForExit()
        gate.showIfDue(activity)

        assertEquals(1, ads.showCount)
        assertEquals(EVERY_N_ROUNDS, adSession.rounds)
        assertEquals(0, adSession.interstitialShownCount)
    }

    @Test
    fun `showIfDue before prepareForExit is called never shows`() = runTest {
        val adSession = FakeAdSessionRepository(initialRounds = EVERY_N_ROUNDS)
        val ads = FakeInterstitialAds()
        val gate = gate(adSession, ads)

        gate.showIfDue(activity)

        assertEquals(0, ads.showCount)
    }
}
