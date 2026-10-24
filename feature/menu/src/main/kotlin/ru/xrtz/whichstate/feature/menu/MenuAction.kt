package ru.xrtz.whichstate.feature.menu

import ru.xrtz.whichstate.core.domain.model.Level
import ru.xrtz.whichstate.core.domain.model.PartyMode

sealed interface MenuAction {
    data class SelectLevel(val level: Level) : MenuAction
    data class SelectPartyMode(val mode: PartyMode) : MenuAction
    data object ToggleLanguage : MenuAction
    data object OpenHistory : MenuAction
}
