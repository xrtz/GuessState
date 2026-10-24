package ru.xrtz.whichstate.feature.history

import ru.xrtz.whichstate.core.domain.model.Level

sealed interface HistoryUiState {
    data object Loading : HistoryUiState

    data class Loaded(
        val selectedLevel: Level,
        val levels: List<LevelHistoryUiModel>,
    ) : HistoryUiState
}

data class LevelHistoryUiModel(
    val level: Level,
    val displayName: String,
    val best: AttemptUiModel?,
    val recent: List<AttemptUiModel>,
)

data class AttemptUiModel(
    val correct: Int,
    val total: Int,
    val percentile: Int,
    val dateText: String,
)
