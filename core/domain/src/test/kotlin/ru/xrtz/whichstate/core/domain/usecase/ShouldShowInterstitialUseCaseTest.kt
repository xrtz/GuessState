package ru.xrtz.whichstate.core.domain.usecase

import ru.xrtz.whichstate.core.domain.policy.AdFrequencyPolicy
import ru.xrtz.whichstate.core.domain.repository.AdSessionRepository
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ShouldShowInterstitialUseCaseTest {
    private class FakeAdSessionRepository(private val rounds: Int) : AdSessionRepository {
        override fun roundsSinceInterstitial(): Int = rounds
        override fun onRoundFinished() = error("not used in this test")
        override fun onInterstitialShown() = error("not used in this test")
    }

    @Test
    fun `defers to the policy using the repository's current counter`() {
        val policy = AdFrequencyPolicy(everyNRounds = 3)
        val notYet = ShouldShowInterstitialUseCase(FakeAdSessionRepository(rounds = 2), policy)
        val dueNow = ShouldShowInterstitialUseCase(FakeAdSessionRepository(rounds = 3), policy)

        assertFalse(notYet())
        assertTrue(dueNow())
    }
}
