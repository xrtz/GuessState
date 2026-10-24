package ru.xrtz.whichstate.feature.menu

import ru.xrtz.whichstate.core.domain.model.AppLanguage
import ru.xrtz.whichstate.core.domain.model.Level
import ru.xrtz.whichstate.core.domain.model.MasteryProgress

data class MenuUiState(
    val levels: List<LevelCardUiModel> = emptyList(),

    val masteredCount: Int? = null,
    val totalStates: Int = MasteryProgress.TOTAL_STATES,
    val language: AppLanguage = AppLanguage.ENGLISH,
)

data class LevelCardUiModel(
    val level: Level,
    val displayName: String,
    val cityCount: Int,
    val difficulty: Int,
)
