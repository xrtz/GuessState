package ru.xrtz.whichstate.core.domain.game

import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

data class RoundRules(
    val duration: Duration = 30.seconds,
    val feedbackCorrect: Duration = 500.milliseconds,
    val feedbackWrong: Duration = 500.milliseconds,
)
