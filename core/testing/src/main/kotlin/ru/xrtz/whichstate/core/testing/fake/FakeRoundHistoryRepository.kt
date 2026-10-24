package ru.xrtz.whichstate.core.testing.fake

import ru.xrtz.whichstate.core.domain.model.Level
import ru.xrtz.whichstate.core.domain.model.RoundHistoryEntry
import ru.xrtz.whichstate.core.domain.model.RoundId
import ru.xrtz.whichstate.core.domain.model.RoundResult
import ru.xrtz.whichstate.core.domain.repository.RoundHistoryRepository

class FakeRoundHistoryRepository : RoundHistoryRepository {
    private val saved = mutableMapOf<RoundId, RoundResult>()
    private var nextId = 1L

    override suspend fun saveFinished(result: RoundResult): RoundId {
        val id = RoundId(nextId++)
        saved[id] = result
        return id
    }

    override suspend fun get(id: RoundId): RoundResult =
        saved.getValue(id)

    override suspend fun bestForLevel(level: Level): RoundHistoryEntry? =
        entriesForLevel(level).maxWithOrNull(compareBy({ it.correct }, { -it.wrong }))

    override suspend fun recentForLevel(level: Level, limit: Int): List<RoundHistoryEntry> =
        entriesForLevel(level).sortedByDescending { it.playedAtMs }.take(limit)

    private fun entriesForLevel(level: Level): List<RoundHistoryEntry> = saved
        .filter { (_, result) -> result.level == level }
        .map { (id, result) ->
            RoundHistoryEntry(id, level, result.correctCount, result.wrongCount, result.startedAtMs)
        }
}
