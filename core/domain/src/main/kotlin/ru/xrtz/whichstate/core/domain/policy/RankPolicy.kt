package ru.xrtz.whichstate.core.domain.policy

import javax.inject.Inject

data class Rank(val tier: Int, val index: Int)

class RankPolicy @Inject constructor() {
    fun rank(percentile: Int, seed: Int): Rank {
        var tierIndex = 0
        THRESHOLDS.forEachIndexed { i, threshold -> if (percentile >= threshold) tierIndex = i }
        val index = ((seed % PHRASES_PER_TIER) + PHRASES_PER_TIER) % PHRASES_PER_TIER
        return Rank(tierIndex, index)
    }

    companion object {
        const val PHRASES_PER_TIER = 10
        private val THRESHOLDS = listOf(0, 12, 28, 48, 65, 80, 100)
    }
}
