package ru.xrtz.whichstate.feature.game

import ru.xrtz.whichstate.core.domain.game.ClassicRoundState
import ru.xrtz.whichstate.core.domain.game.RoundRules
import ru.xrtz.whichstate.core.domain.model.Verdict
import ru.xrtz.whichstate.core.map.MapHighlight
import kotlin.math.ceil

private const val TIME_WARNING_THRESHOLD_SECONDS = 10

internal fun ClassicRoundState.toUiState(rules: RoundRules, nowMs: Long): GameUiState {
    val elapsedMs = nowMs - startedAtMs - pausedTotalMs
    val remainingMs = (rules.duration.inWholeMilliseconds - elapsedMs).coerceAtLeast(0)
    val secondsLeft = ceil(remainingMs / 1000.0).toInt()

    val question = when (val phase = phase) {
        is ClassicRoundState.Phase.Asking -> phase.question
        is ClassicRoundState.Phase.Feedback -> phase.question
        ClassicRoundState.Phase.Finished -> answers.lastOrNull()?.question
    }
    val feedback = phase as? ClassicRoundState.Phase.Feedback

    return GameUiState(
        cityName = question?.city?.name.orEmpty(),
        secondsLeft = secondsLeft,
        isTimeWarning = secondsLeft <= TIME_WARNING_THRESHOLD_SECONDS,
        correct = answers.count { it.verdict == Verdict.CORRECT },
        wrong = answers.count { it.verdict != Verdict.CORRECT },
        highlight = feedback?.let {
            MapHighlight(
                pickedState = it.answer.picked,
                correctState = it.question.city.state,
                verdict = it.answer.verdict,
            )
        },
        isNearMiss = feedback?.answer?.verdict == Verdict.NEAR_MISS,
        isPaused = pausedAtMs != null,
    )
}
