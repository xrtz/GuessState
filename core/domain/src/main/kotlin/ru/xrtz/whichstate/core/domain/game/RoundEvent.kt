package ru.xrtz.whichstate.core.domain.game

import ru.xrtz.whichstate.core.domain.model.UsState

sealed interface RoundEvent {
    data class Tap(val state: UsState, val nowMs: Long) : RoundEvent
    data class Tick(val nowMs: Long) : RoundEvent
    data class Pause(val nowMs: Long) : RoundEvent
    data class Resume(val nowMs: Long) : RoundEvent
}
