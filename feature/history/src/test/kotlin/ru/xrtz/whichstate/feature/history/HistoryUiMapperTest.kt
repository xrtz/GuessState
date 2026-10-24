package ru.xrtz.whichstate.feature.history

import ru.xrtz.whichstate.core.domain.model.HistoryAttempt
import ru.xrtz.whichstate.core.domain.model.Level
import ru.xrtz.whichstate.core.domain.model.LevelHistory
import ru.xrtz.whichstate.core.domain.model.RoundId
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class HistoryUiMapperTest {
    private fun attempt(correct: Int, wrong: Int, percentile: Int, playedAtMs: Long) =
        HistoryAttempt(RoundId(1), Level.ROOKIE, correct, wrong, percentile, playedAtMs)

    private fun history(vararg entries: Pair<Level, LevelHistory>): List<LevelHistory> {
        val byLevel = entries.toMap()
        return Level.entries.map { byLevel[it] ?: LevelHistory(it, best = null, recent = emptyList()) }
    }

    @Test
    fun `selectedLevel carries through unchanged`() {
        val state = history().toUiState(Level.EXPLORER)

        assertEquals(Level.EXPLORER, state.selectedLevel)
    }

    @Test
    fun `level display names are capitalized from the enum`() {
        val state = history().toUiState(Level.ROOKIE)

        assertEquals("Rookie", state.levels.single { it.level == Level.ROOKIE }.displayName)
        assertEquals("Explorer", state.levels.single { it.level == Level.EXPLORER }.displayName)
        assertEquals("Master", state.levels.single { it.level == Level.MASTER }.displayName)
    }

    @Test
    fun `best total is correct plus wrong`() {
        val best = attempt(correct = 47, wrong = 3, percentile = 92, playedAtMs = 0)
        val levelHistory = LevelHistory(Level.ROOKIE, best, recent = emptyList())
        val state = history(Level.ROOKIE to levelHistory).toUiState(Level.ROOKIE)

        assertEquals(50, state.levels.single { it.level == Level.ROOKIE }.best?.total)
    }

    @Test
    fun `a level with no best maps to null`() {
        val state = history().toUiState(Level.MASTER)

        assertNull(state.levels.single { it.level == Level.MASTER }.best)
    }

    @Test
    fun `recent attempts map in the same order`() {
        val recent = listOf(
            attempt(correct = 10, wrong = 0, percentile = 50, playedAtMs = 2_000),
            attempt(correct = 5, wrong = 5, percentile = 20, playedAtMs = 1_000),
        )
        val state = history(Level.ROOKIE to LevelHistory(Level.ROOKIE, best = null, recent)).toUiState(Level.ROOKIE)

        val mapped = state.levels.single { it.level == Level.ROOKIE }.recent
        assertEquals(listOf(10, 5), mapped.map { it.correct })
    }
}
