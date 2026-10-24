package ru.xrtz.whichstate.feature.game

import ru.xrtz.whichstate.core.domain.model.RoundId

sealed interface GameEffect {
    data class OpenResults(val roundId: RoundId) : GameEffect
    data object ExitToMenu : GameEffect
}
