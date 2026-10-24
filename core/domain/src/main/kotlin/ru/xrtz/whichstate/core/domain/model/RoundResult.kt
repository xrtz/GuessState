package ru.xrtz.whichstate.core.domain.model

data class RoundId(val value: Long)

data class RoundResult(
    val level: Level,
    val answers: List<Answer>,
    val startedAtMs: Long,
    val durationMs: Long,
) {
    val correctCount: Int get() = answers.count { it.verdict == Verdict.CORRECT }
    val wrongCount: Int get() = answers.count { it.verdict != Verdict.CORRECT }
}
