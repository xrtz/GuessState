package ru.xrtz.whichstate.core.domain.policy

import ru.xrtz.whichstate.core.domain.model.Answer
import ru.xrtz.whichstate.core.domain.model.UsState
import ru.xrtz.whichstate.core.domain.model.Verdict
import javax.inject.Inject

class AccuracyCalculator @Inject constructor() {
    fun accuracyByState(answers: List<Answer>): Map<UsState, Float> =
        answers.groupBy { it.question.city.state }
            .mapValues { (_, forState) -> forState.count { it.verdict == Verdict.CORRECT }.toFloat() / forState.size }
}
