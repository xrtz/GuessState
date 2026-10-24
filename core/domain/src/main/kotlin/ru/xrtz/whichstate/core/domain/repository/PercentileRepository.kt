package ru.xrtz.whichstate.core.domain.repository

import ru.xrtz.whichstate.core.domain.model.Level

interface PercentileRepository {
    suspend fun percentile(level: Level, correct: Int): Int
}
