package ru.xrtz.whichstate.feature.game

import ru.xrtz.whichstate.core.domain.game.RoundRules
import ru.xrtz.whichstate.core.map.MapHighlight

data class GameUiState(
    val cityName: String = "",
    val secondsLeft: Int = RoundRules().duration.inWholeSeconds.toInt(),
    val isTimeWarning: Boolean = false,
    val correct: Int = 0,
    val wrong: Int = 0,
    val highlight: MapHighlight? = null,

    val isNearMiss: Boolean = false,
    val isPaused: Boolean = false,
)
