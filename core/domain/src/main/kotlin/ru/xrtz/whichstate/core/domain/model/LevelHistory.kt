package ru.xrtz.whichstate.core.domain.model

data class LevelHistory(
    val level: Level,
    val best: HistoryAttempt?,
    val recent: List<HistoryAttempt>,
)
