package ru.xrtz.whichstate.core.domain.game

import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

data class PartyRules(
    val turnDuration: Duration = 30.seconds,
    val citiesPerTurn: Int = 7,
    val totalRounds: Int = 3,
    val teamCounts: IntRange = 2..4,
    val maxTeamNameLength: Int = 20,
)
