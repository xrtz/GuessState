package ru.xrtz.whichstate.feature.menu

import ru.xrtz.whichstate.core.domain.model.AppLanguage
import ru.xrtz.whichstate.core.domain.model.LevelInfo
import ru.xrtz.whichstate.core.domain.model.MasteryProgress

internal fun LevelInfo.toUiModel(): LevelCardUiModel = LevelCardUiModel(
    level = level,
    displayName = level.name.lowercase().replaceFirstChar { it.titlecase() },
    cityCount = cityCount,
    difficulty = level.ordinal + 1,
)

internal fun toMenuUiState(levels: List<LevelInfo>, mastery: MasteryProgress, language: AppLanguage): MenuUiState =
    MenuUiState(
        levels = levels.map { it.toUiModel() },
        masteredCount = mastery.mastered.size.takeIf { it > 0 },
        totalStates = mastery.total,
        language = language,
    )
