package ru.xrtz.whichstate.feature.party.game

import ru.xrtz.whichstate.core.domain.game.PartyGameState
import ru.xrtz.whichstate.core.domain.game.PartyGameState.Phase
import ru.xrtz.whichstate.core.domain.game.PartyRules
import ru.xrtz.whichstate.core.domain.model.PartyTeam
import ru.xrtz.whichstate.core.domain.policy.PartyScoring
import kotlin.math.ceil

private const val TIME_WARNING_THRESHOLD_SECONDS = 10

internal fun PartyGameState.toUiState(rules: PartyRules, scoring: PartyScoring, nowMs: Long): PartyGameUiState =
    when (val phase = phase) {
        Phase.Handoff -> PartyGameUiState.Handoff(
            round = currentRound,
            totalRounds = totalRounds,
            team = currentTeam.toLabel(),
            scoreboard = teams.map {
                ScoreRowUiModel(it.toLabel(), it.score, isActive = it.number == currentTeam.number)
            },
        )
        is Phase.Playing -> playingUiState(phase, rules, nowMs)
        Phase.RoundEnd -> roundEndUiState(scoring)
        Phase.GameEnd -> gameEndUiState(scoring)
    }

private fun PartyGameState.gameEndUiState(scoring: PartyScoring): PartyGameUiState {
    val standings = scoring.standings(teams)
    return PartyGameUiState.GameEnd(
        winner = if (scoring.isTie(teams)) null else standings.first().toLabel(),
        standings = standings.map { team ->
            StandingRowUiModel(standings.indexOfFirst { it.score == team.score }, team.toLabel(), team.score)
        },
    )
}

private fun PartyGameState.playingUiState(phase: Phase.Playing, rules: PartyRules, nowMs: Long): PartyGameUiState {
    val remainingMs = (rules.turnDuration.inWholeMilliseconds - phase.elapsedMs(nowMs)).coerceAtLeast(0)
    val secondsLeft = ceil(remainingMs / 1000.0).toInt()
    val city = currentTurnCities[phase.cityIndex]
    return PartyGameUiState.Playing(
        mode = mode,
        secondsLeft = secondsLeft,
        isTimeWarning = secondsLeft <= TIME_WARNING_THRESHOLD_SECONDS,
        cardNumber = phase.cityIndex + 1,
        cardCount = currentTurnCities.size,
        cardKey = "$turnIndex-${phase.cityIndex}",
        cityName = city.name,
        stateCode = city.state.name,
    )
}

private fun PartyGameState.roundEndUiState(scoring: PartyScoring): PartyGameUiState {
    val correct = turnResults.count { it.correct }
    val points = scoring.turnPoints(turnResults, penaltyOnSkip)
    return PartyGameUiState.RoundEnd(
        team = currentTeam.toLabel(),
        correct = correct,
        skipped = turnResults.size - correct,
        pointsText = if (points > 0) "+$points" else points.toString(),
        results = turnResults.map { PartyResultRowUiModel(it.city.name, it.city.state.name, it.correct) },
        isLastTurn = isLastTurn,
    )
}

private fun PartyTeam.toLabel() = TeamLabel(number, customName)
