package ru.xrtz.whichstate.core.domain.policy

import ru.xrtz.whichstate.core.domain.model.Level
import javax.inject.Inject

class PercentileTable @Inject constructor() {
    fun percentile(level: Level, correct: Int): Int {
        val table = TABLES.getValue(level)
        var percentile = 0
        for ((threshold, percentileAtThreshold) in table) {
            if (correct >= threshold) percentile = percentileAtThreshold
        }
        return percentile
    }

    private companion object {
        val TABLES: Map<Level, List<Pair<Int, Int>>> = mapOf(
            Level.ROOKIE to listOf(0 to 0, 1 to 5, 4 to 12, 7 to 25, 10 to 45, 13 to 62, 16 to 78),
            Level.EXPLORER to listOf(0 to 0, 1 to 4, 5 to 11, 9 to 28, 13 to 48, 17 to 65, 21 to 80),
            Level.MASTER to listOf(0 to 0, 1 to 3, 5 to 9, 10 to 22, 15 to 44, 20 to 63, 25 to 79),
        )
    }
}
