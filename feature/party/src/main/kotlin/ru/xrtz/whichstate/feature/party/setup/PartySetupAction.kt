package ru.xrtz.whichstate.feature.party.setup

sealed interface PartySetupAction {
    data class SelectTeamCount(val count: Int) : PartySetupAction
    data class ChangeTeamName(val index: Int, val name: String) : PartySetupAction
    data class SetPenaltyOnSkip(val enabled: Boolean) : PartySetupAction
    data object Start : PartySetupAction
    data object Back : PartySetupAction
}
