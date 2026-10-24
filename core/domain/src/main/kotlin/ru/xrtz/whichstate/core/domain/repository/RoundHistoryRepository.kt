package ru.xrtz.whichstate.core.domain.repository

import ru.xrtz.whichstate.core.domain.model.Level
import ru.xrtz.whichstate.core.domain.model.RoundHistoryEntry
import ru.xrtz.whichstate.core.domain.model.RoundId
import ru.xrtz.whichstate.core.domain.model.RoundResult

interface RoundHistoryRepository {
    suspend fun saveFinished(result: RoundResult): RoundId
    suspend fun get(id: RoundId): RoundResult
    suspend fun bestForLevel(level: Level): RoundHistoryEntry?
    suspend fun recentForLevel(level: Level, limit: Int): List<RoundHistoryEntry>
}
