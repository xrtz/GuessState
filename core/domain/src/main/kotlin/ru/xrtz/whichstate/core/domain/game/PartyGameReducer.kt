package ru.xrtz.whichstate.core.domain.game

import ru.xrtz.whichstate.core.domain.game.PartyGameState.Phase
import ru.xrtz.whichstate.core.domain.model.PartyCardResult
import ru.xrtz.whichstate.core.domain.policy.PartyScoring

class PartyGameReducer(
    private val rules: PartyRules,
    private val scoring: PartyScoring,
) {
    fun reduce(state: PartyGameState, event: PartyEvent): PartyGameState = when (event) {
        is PartyEvent.StartTurn -> onStartTurn(state, event)
        is PartyEvent.Answer -> onAnswer(state, event)
        is PartyEvent.Tick -> onTick(state, event)
        is PartyEvent.Pause -> onPause(state, event)
        is PartyEvent.Resume -> onResume(state, event)
        PartyEvent.NextTurn -> onNextTurn(state)
    }

    private fun onStartTurn(state: PartyGameState, event: PartyEvent.StartTurn): PartyGameState {
        if (state.phase != Phase.Handoff) return state
        return state.copy(phase = Phase.Playing(cityIndex = 0, startedAtMs = event.nowMs), turnResults = emptyList())
    }

    private fun onAnswer(state: PartyGameState, event: PartyEvent.Answer): PartyGameState {
        val playing = state.phase as? Phase.Playing ?: return state
        if (playing.pausedAtMs != null) return state

        if (isTimeUp(playing, event.nowMs)) return state.copy(phase = Phase.RoundEnd)

        val result = PartyCardResult(state.currentTurnCities[playing.cityIndex], event.correct)
        val points = scoring.points(event.correct, state.penaltyOnSkip)
        val current = state.currentTeam
        val teams = state.teams.map { if (it.number == current.number) it.copy(score = it.score + points) else it }
        val nextIndex = playing.cityIndex + 1
        val hasMoreCards = nextIndex < state.currentTurnCities.size
        val phase = if (hasMoreCards) playing.copy(cityIndex = nextIndex) else Phase.RoundEnd

        return state.copy(teams = teams, turnResults = state.turnResults + result, phase = phase)
    }

    private fun onTick(state: PartyGameState, event: PartyEvent.Tick): PartyGameState {
        val playing = state.phase as? Phase.Playing ?: return state
        if (playing.pausedAtMs != null || !isTimeUp(playing, event.nowMs)) return state
        return state.copy(phase = Phase.RoundEnd)
    }

    private fun onPause(state: PartyGameState, event: PartyEvent.Pause): PartyGameState {
        val playing = state.phase as? Phase.Playing ?: return state
        if (playing.pausedAtMs != null) return state
        return state.copy(phase = playing.copy(pausedAtMs = event.nowMs))
    }

    private fun onResume(state: PartyGameState, event: PartyEvent.Resume): PartyGameState {
        val playing = state.phase as? Phase.Playing ?: return state
        val pausedAtMs = playing.pausedAtMs ?: return state
        return state.copy(
            phase = playing.copy(pausedAtMs = null, pausedTotalMs = playing.pausedTotalMs + (event.nowMs - pausedAtMs)),
        )
    }

    private fun onNextTurn(state: PartyGameState): PartyGameState {
        if (state.phase != Phase.RoundEnd) return state
        if (state.isLastTurn) return state.copy(phase = Phase.GameEnd)
        return state.copy(phase = Phase.Handoff, turnIndex = state.turnIndex + 1)
    }

    private fun isTimeUp(playing: Phase.Playing, nowMs: Long): Boolean =
        playing.elapsedMs(nowMs) >= rules.turnDuration.inWholeMilliseconds
}
