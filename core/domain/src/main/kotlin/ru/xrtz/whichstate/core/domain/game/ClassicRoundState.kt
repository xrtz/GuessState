package ru.xrtz.whichstate.core.domain.game

import ru.xrtz.whichstate.core.domain.model.Answer
import ru.xrtz.whichstate.core.domain.model.Question

data class ClassicRoundState(
    val phase: Phase,
    val queue: List<Question>,
    val answers: List<Answer>,
    val startedAtMs: Long,
    val pausedTotalMs: Long = 0L,
    val pausedAtMs: Long? = null,
) {
    sealed interface Phase {
        data class Asking(val question: Question) : Phase
        data class Feedback(val question: Question, val answer: Answer, val startedAtMs: Long) : Phase
        data object Finished : Phase
    }

    companion object {
        fun start(firstQuestion: Question, remaining: List<Question>, startedAtMs: Long): ClassicRoundState =
            ClassicRoundState(
                phase = Phase.Asking(firstQuestion),
                queue = remaining,
                answers = emptyList(),
                startedAtMs = startedAtMs,
            )
    }
}
