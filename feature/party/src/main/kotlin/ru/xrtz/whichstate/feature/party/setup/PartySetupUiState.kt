package ru.xrtz.whichstate.feature.party.setup

import ru.xrtz.whichstate.core.domain.game.PartyRules
import ru.xrtz.whichstate.core.domain.model.PartyMode

data class PartySetupUiState(
    val mode: PartyMode,
    val teamCountOptions: List<Int>,
    val teamCount: Int,
    val teamNames: List<String>,
    val penaltyOnSkip: Boolean,
    val roundCount: Int,
    val citiesPerRound: Int,
    val secondsPerRound: Int,
) {
    companion object {
        fun initial(mode: PartyMode, rules: PartyRules = PartyRules()) = PartySetupUiState(
            mode = mode,
            teamCountOptions = rules.teamCounts.toList(),
            teamCount = rules.teamCounts.first,
            teamNames = List(rules.teamCounts.last) { "" },
            penaltyOnSkip = false,
            roundCount = rules.totalRounds,
            citiesPerRound = rules.citiesPerTurn,
            secondsPerRound = rules.turnDuration.inWholeSeconds.toInt(),
        )
    }
}
