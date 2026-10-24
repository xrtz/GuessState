package ru.xrtz.whichstate.feature.party.game

sealed interface PartyGameAction {
    data object StartTurn : PartyGameAction
    data class Answer(val correct: Boolean) : PartyGameAction
    data object NextTurn : PartyGameAction
    data object PlayAgain : PartyGameAction
    data object Pause : PartyGameAction
    data object Resume : PartyGameAction
    data object ExitToMenu : PartyGameAction
}
