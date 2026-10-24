package ru.xrtz.whichstate.core.domain.time

fun interface GameClock {
    fun nowMs(): Long
}
