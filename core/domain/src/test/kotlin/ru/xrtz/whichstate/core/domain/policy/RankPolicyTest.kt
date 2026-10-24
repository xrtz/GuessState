package ru.xrtz.whichstate.core.domain.policy

import kotlin.test.Test
import kotlin.test.assertEquals

class RankPolicyTest {
    private val policy = RankPolicy()

    @Test
    fun `percentile zero picks the bottom tier`() {
        assertEquals(0, policy.rank(percentile = 0, seed = 0).tier)
    }

    @Test
    fun `percentile 100 picks the top tier`() {
        assertEquals(6, policy.rank(percentile = 100, seed = 0).tier)
    }

    @Test
    fun `a percentile between two thresholds uses the lower tier`() {
        assertEquals(1, policy.rank(percentile = 27, seed = 0).tier)
    }

    @Test
    fun `seed selects one of the ten phrases within a tier`() {
        assertEquals(0, policy.rank(percentile = 0, seed = 0).index)
        assertEquals(1, policy.rank(percentile = 0, seed = 1).index)
        assertEquals(9, policy.rank(percentile = 0, seed = 9).index)
    }

    @Test
    fun `seed wraps around after ten since every tier has exactly ten phrases`() {
        assertEquals(policy.rank(percentile = 0, seed = 0), policy.rank(percentile = 0, seed = 10))
        assertEquals(policy.rank(percentile = 0, seed = 3), policy.rank(percentile = 0, seed = 13))
    }
}
