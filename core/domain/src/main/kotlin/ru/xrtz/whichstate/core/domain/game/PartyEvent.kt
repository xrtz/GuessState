package ru.xrtz.whichstate.core.domain.game

sealed interface PartyEvent {
    data class StartTurn(val nowMs: Long) : PartyEvent
    data class Answer(val correct: Boolean, val nowMs: Long) : PartyEvent
    data class Tick(val nowMs: Long) : PartyEvent
    data class Pause(val nowMs: Long) : PartyEvent
    data class Resume(val nowMs: Long) : PartyEvent
    data object NextTurn : PartyEvent
}
