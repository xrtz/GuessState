package ru.xrtz.whichstate.core.domain.policy

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class AdFrequencyPolicyTest {
    private val policy = AdFrequencyPolicy(everyNRounds = 3)

    @Test
    fun `not shown after the first round of a session`() {
        assertFalse(policy.shouldShow(roundsSinceInterstitial = 1))
    }

    @Test
    fun `not shown before the cadence threshold is reached`() {
        assertFalse(policy.shouldShow(roundsSinceInterstitial = 2))
    }

    @Test
    fun `shown once the cadence threshold is reached`() {
        assertTrue(policy.shouldShow(roundsSinceInterstitial = 3))
    }

    @Test
    fun `still shown past the threshold, not just exactly at it`() {
        assertTrue(policy.shouldShow(roundsSinceInterstitial = 5))
    }

    @Test
    fun `a full session simulation never shows more often than every N rounds`() {
        var roundsSinceInterstitial = 0
        val shownAtRound = mutableListOf<Int>()

        for (round in 1..12) {
            roundsSinceInterstitial++
            if (policy.shouldShow(roundsSinceInterstitial)) {
                shownAtRound += round
                roundsSinceInterstitial = 0
            }
        }

        assertEquals(listOf(3, 6, 9, 12), shownAtRound)
    }
}
