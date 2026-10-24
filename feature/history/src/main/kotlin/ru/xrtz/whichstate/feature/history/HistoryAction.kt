package ru.xrtz.whichstate.feature.history

import ru.xrtz.whichstate.core.domain.model.Level

sealed interface HistoryAction {
    data class SelectLevel(val level: Level) : HistoryAction
    data object Back : HistoryAction
}
