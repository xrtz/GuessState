package ru.xrtz.whichstate.feature.history

import ru.xrtz.whichstate.core.domain.model.HistoryAttempt
import ru.xrtz.whichstate.core.domain.model.Level
import ru.xrtz.whichstate.core.domain.model.LevelHistory
import java.text.DateFormat
import java.util.Date

internal fun List<LevelHistory>.toUiState(selectedLevel: Level): HistoryUiState.Loaded = HistoryUiState.Loaded(
    selectedLevel = selectedLevel,
    levels = map { it.toUiModel() },
)

private fun LevelHistory.toUiModel(): LevelHistoryUiModel = LevelHistoryUiModel(
    level = level,
    displayName = level.name.lowercase().replaceFirstChar { it.titlecase() },
    best = best?.toUiModel(),
    recent = recent.map { it.toUiModel() },
)

private fun HistoryAttempt.toUiModel(): AttemptUiModel = AttemptUiModel(
    correct = correct,
    total = correct + wrong,
    percentile = percentile,
    dateText = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT).format(Date(playedAtMs)),
)
