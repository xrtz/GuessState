package ru.xrtz.whichstate.core.domain.model

data class RoundSummary(
    val level: Level,
    val correct: Int,
    val wrong: Int,
    val percentile: Int,
    val rankTier: Int,
    val rankIndex: Int,
    val worstMiss: Answer?,
    val accuracyByState: Map<UsState, Float>,
    val masteredCount: Int,
    val totalStates: Int = MasteryProgress.TOTAL_STATES,
    val answers: List<Answer> = emptyList(),
)
