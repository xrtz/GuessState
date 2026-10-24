package ru.xrtz.whichstate.core.domain.usecase

import kotlinx.coroutines.runBlocking
import ru.xrtz.whichstate.core.domain.model.Level
import ru.xrtz.whichstate.core.domain.model.RoundHistoryEntry
import ru.xrtz.whichstate.core.domain.model.RoundId
import ru.xrtz.whichstate.core.domain.model.RoundResult
import ru.xrtz.whichstate.core.domain.policy.PercentileTable
import ru.xrtz.whichstate.core.domain.repository.RoundHistoryRepository
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class GetHistoryUseCaseTest {
    private fun repository(
        best: Map<Level, RoundHistoryEntry?> = emptyMap(),
        recent: Map<Level, List<RoundHistoryEntry>> = emptyMap(),
    ) = object : RoundHistoryRepository {
        override suspend fun saveFinished(result: RoundResult): RoundId = error("not used in this test")
        override suspend fun get(id: RoundId): RoundResult = error("not used in this test")
        override suspend fun bestForLevel(level: Level): RoundHistoryEntry? = best[level]
        override suspend fun recentForLevel(level: Level, limit: Int): List<RoundHistoryEntry> =
            recent[level].orEmpty()
    }

    private fun entry(level: Level, correct: Int, playedAtMs: Long) =
        RoundHistoryEntry(RoundId(1), level, correct, wrong = 0, playedAtMs = playedAtMs)

    private fun useCase(
        best: Map<Level, RoundHistoryEntry?> = emptyMap(),
        recent: Map<Level, List<RoundHistoryEntry>> = emptyMap(),
    ) = GetHistoryUseCase(repository(best, recent), PercentileTable())

    @Test
    fun `returns one entry per level, in Level order`() = runBlocking {
        val history = useCase()()

        assertEquals(listOf(Level.ROOKIE, Level.EXPLORER, Level.MASTER), history.map { it.level })
    }

    @Test
    fun `carries through each level's best and recent from the repository, enriched with percentile`() = runBlocking {
        val bestRookie = entry(Level.ROOKIE, correct = 42, playedAtMs = 1_000)
        val recentRookie = listOf(entry(Level.ROOKIE, correct = 10, playedAtMs = 2_000))
        val history = useCase(best = mapOf(Level.ROOKIE to bestRookie), recent = mapOf(Level.ROOKIE to recentRookie))()

        val rookie = history.single { it.level == Level.ROOKIE }
        val expectedBestPercentile = PercentileTable().percentile(Level.ROOKIE, 42)
        val expectedRecentPercentile = PercentileTable().percentile(Level.ROOKIE, 10)

        assertEquals(42, rookie.best?.correct)
        assertEquals(expectedBestPercentile, rookie.best?.percentile)
        assertEquals(1_000, rookie.best?.playedAtMs)
        assertEquals(1, rookie.recent.size)
        assertEquals(10, rookie.recent.single().correct)
        assertEquals(expectedRecentPercentile, rookie.recent.single().percentile)
    }

    @Test
    fun `a level with no rounds yet has a null best and empty recent`() = runBlocking {
        val history = useCase()()

        assertNull(history.single { it.level == Level.EXPLORER }.best)
        assertEquals(emptyList(), history.single { it.level == Level.EXPLORER }.recent)
    }
}
