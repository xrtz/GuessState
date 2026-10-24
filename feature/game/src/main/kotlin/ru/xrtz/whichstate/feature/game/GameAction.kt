package ru.xrtz.whichstate.feature.game

import ru.xrtz.whichstate.core.domain.model.UsState

sealed interface GameAction {
    data class TapState(val state: UsState) : GameAction
    data object Pause : GameAction
    data object Resume : GameAction
    data object ExitToMenu : GameAction
}
