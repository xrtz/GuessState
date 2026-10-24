package ru.xrtz.whichstate.feature.party.setup

import ru.xrtz.whichstate.core.domain.model.PartyMode

sealed interface PartySetupEffect {
    data class StartGame(
        val mode: PartyMode,
        val teamNames: List<String>,
        val penaltyOnSkip: Boolean,
    ) : PartySetupEffect
    data object Back : PartySetupEffect
}
