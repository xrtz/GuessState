package ru.xrtz.whichstate.core.domain.game

import ru.xrtz.whichstate.core.domain.model.City
import ru.xrtz.whichstate.core.domain.model.PartyCardResult
import ru.xrtz.whichstate.core.domain.model.PartyMode
import ru.xrtz.whichstate.core.domain.model.PartyTeam

data class PartyGameState(
    val mode: PartyMode,
    val penaltyOnSkip: Boolean,
    val teams: List<PartyTeam>,
    val turns: List<List<City>>,
    val turnIndex: Int,
    val phase: Phase,
    val turnResults: List<PartyCardResult>,
) {
    val currentTeam: PartyTeam get() = teams[turnIndex % teams.size]
    val currentRound: Int get() = turnIndex / teams.size + 1
    val totalRounds: Int get() = turns.size / teams.size
    val currentTurnCities: List<City> get() = turns[turnIndex]
    val isLastTurn: Boolean get() = turnIndex + 1 >= turns.size

    sealed interface Phase {
        data object Handoff : Phase

        data class Playing(
            val cityIndex: Int,
            val startedAtMs: Long,
            val pausedTotalMs: Long = 0L,
            val pausedAtMs: Long? = null,
        ) : Phase {
            fun elapsedMs(nowMs: Long): Long = (pausedAtMs ?: nowMs) - startedAtMs - pausedTotalMs
        }

        data object RoundEnd : Phase
        data object GameEnd : Phase
    }
}
