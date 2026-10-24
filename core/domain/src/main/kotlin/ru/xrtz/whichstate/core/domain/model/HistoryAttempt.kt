package ru.xrtz.whichstate.core.domain.model

data class HistoryAttempt(
    val roundId: RoundId,
    val level: Level,
    val correct: Int,
    val wrong: Int,
    val percentile: Int,
    val playedAtMs: Long,
)
