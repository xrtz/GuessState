package ru.xrtz.whichstate.core.map

import ru.xrtz.whichstate.core.designsystem.components.StateTileStatus
import ru.xrtz.whichstate.core.domain.model.UsState
import ru.xrtz.whichstate.core.domain.model.Verdict

data class MapHighlight(val pickedState: UsState, val correctState: UsState, val verdict: Verdict) {
    fun statusOf(state: UsState): StateTileStatus = when {
        state == pickedState -> when (verdict) {
            Verdict.CORRECT -> StateTileStatus.CORRECT
            Verdict.NEAR_MISS -> StateTileStatus.NEAR_MISS
            Verdict.WRONG -> StateTileStatus.WRONG
        }
        verdict != Verdict.CORRECT && state == correctState -> StateTileStatus.CORRECT
        else -> StateTileStatus.DEFAULT
    }
}
