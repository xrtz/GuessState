package ru.xrtz.whichstate.core.domain.model

data class RoundHistoryEntry(
    val roundId: RoundId,
    val level: Level,
    val correct: Int,
    val wrong: Int,
    val playedAtMs: Long,
)
