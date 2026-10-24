package ru.xrtz.whichstate.core.data.repository

import ru.xrtz.whichstate.core.domain.model.Level
import ru.xrtz.whichstate.core.domain.policy.PercentileTable
import ru.xrtz.whichstate.core.domain.repository.PercentileRepository
import javax.inject.Inject

internal class PercentileRepositoryImpl @Inject constructor() : PercentileRepository {
    private val percentileTable = PercentileTable()

    override suspend fun percentile(level: Level, correct: Int): Int = percentileTable.percentile(level, correct)
}
