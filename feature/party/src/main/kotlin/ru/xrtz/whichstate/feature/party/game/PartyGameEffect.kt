package ru.xrtz.whichstate.feature.party.game

sealed interface PartyGameEffect {
    data object ExitToMenu : PartyGameEffect
}
