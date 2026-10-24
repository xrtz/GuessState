package ru.xrtz.whichstate.core.domain.game

import ru.xrtz.whichstate.core.domain.model.Answer
import ru.xrtz.whichstate.core.domain.model.Verdict
import ru.xrtz.whichstate.core.domain.policy.AnswerJudge

class ClassicRoundReducer(
    private val judge: AnswerJudge,
    private val rules: RoundRules,
) {
    fun reduce(state: ClassicRoundState, event: RoundEvent): ClassicRoundState {
        if (state.phase is ClassicRoundState.Phase.Finished) return state

        return when (event) {
            is RoundEvent.Tap -> onTap(state, event)
            is RoundEvent.Tick -> onTick(state, event)
            is RoundEvent.Pause -> onPause(state, event)
            is RoundEvent.Resume -> onResume(state, event)
        }
    }

    private fun onTap(state: ClassicRoundState, event: RoundEvent.Tap): ClassicRoundState {
        if (state.pausedAtMs != null) return state

        val asking = state.phase as? ClassicRoundState.Phase.Asking ?: return state

        val verdict = judge.judge(asking.question, event.state)
        val answer = Answer(asking.question, event.state, verdict, event.nowMs)
        return state.copy(
            phase = ClassicRoundState.Phase.Feedback(asking.question, answer, startedAtMs = event.nowMs),
            answers = state.answers + answer,
        )
    }

    private fun onTick(state: ClassicRoundState, event: RoundEvent.Tick): ClassicRoundState {
        if (state.pausedAtMs != null) return state

        val elapsedMs = event.nowMs - state.startedAtMs - state.pausedTotalMs
        if (elapsedMs >= rules.duration.inWholeMilliseconds) {
            return state.copy(phase = ClassicRoundState.Phase.Finished)
        }

        val feedback = state.phase as? ClassicRoundState.Phase.Feedback ?: return state
        val feedbackDuration = when (feedback.answer.verdict) {
            Verdict.CORRECT -> rules.feedbackCorrect
            Verdict.NEAR_MISS, Verdict.WRONG -> rules.feedbackWrong
        }
        if (event.nowMs - feedback.startedAtMs < feedbackDuration.inWholeMilliseconds) return state

        val nextQuestion = state.queue.firstOrNull()
            ?: return state.copy(phase = ClassicRoundState.Phase.Finished)
        return state.copy(phase = ClassicRoundState.Phase.Asking(nextQuestion), queue = state.queue.drop(1))
    }

    private fun onPause(state: ClassicRoundState, event: RoundEvent.Pause): ClassicRoundState {
        if (state.pausedAtMs != null) return state
        return state.copy(pausedAtMs = event.nowMs)
    }

    private fun onResume(state: ClassicRoundState, event: RoundEvent.Resume): ClassicRoundState {
        val pausedAtMs = state.pausedAtMs ?: return state
        return state.copy(
            pausedAtMs = null,
            pausedTotalMs = state.pausedTotalMs + (event.nowMs - pausedAtMs),
        )
    }
}
