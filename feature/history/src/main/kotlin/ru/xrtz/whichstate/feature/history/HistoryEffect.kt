package ru.xrtz.whichstate.feature.history

sealed interface HistoryEffect {
    data object Back : HistoryEffect
}
