package ru.xrtz.whichstate.core.testing.fake

import ru.xrtz.whichstate.core.domain.time.GameClock

class FakeGameClock(startAtMs: Long = 0L) : GameClock {
    var currentMs: Long = startAtMs

    override fun nowMs(): Long = currentMs

    fun advanceBy(deltaMs: Long) {
        currentMs += deltaMs
    }
}
