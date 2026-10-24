package ru.xrtz.whichstate.feature.menu

import ru.xrtz.whichstate.core.domain.model.Level
import ru.xrtz.whichstate.core.domain.model.PartyMode

sealed interface MenuEffect {
    data class OpenLevel(val level: Level) : MenuEffect
    data class OpenParty(val mode: PartyMode) : MenuEffect
    data object OpenHistory : MenuEffect
}
