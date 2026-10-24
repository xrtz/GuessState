package ru.xrtz.whichstate.feature.party.game

import ru.xrtz.whichstate.core.domain.model.PartyMode

sealed interface PartyGameUiState {
    data object Loading : PartyGameUiState

    data class Handoff(
        val round: Int,
        val totalRounds: Int,
        val team: TeamLabel,
        val scoreboard: List<ScoreRowUiModel>,
    ) : PartyGameUiState

    data class Playing(
        val mode: PartyMode,
        val secondsLeft: Int,
        val isTimeWarning: Boolean,
        val cardNumber: Int,
        val cardCount: Int,
        val cardKey: String,
        val cityName: String,
        val stateCode: String,
    ) : PartyGameUiState

    data class RoundEnd(
        val team: TeamLabel,
        val correct: Int,
        val skipped: Int,
        val pointsText: String,
        val results: List<PartyResultRowUiModel>,
        val isLastTurn: Boolean,
    ) : PartyGameUiState

    data class GameEnd(
        val winner: TeamLabel?,
        val standings: List<StandingRowUiModel>,
    ) : PartyGameUiState
}

data class TeamLabel(val number: Int, val customName: String?)

data class ScoreRowUiModel(val team: TeamLabel, val score: Int, val isActive: Boolean)

data class PartyResultRowUiModel(val cityName: String, val stateCode: String, val correct: Boolean)

data class StandingRowUiModel(val place: Int, val team: TeamLabel, val score: Int)
