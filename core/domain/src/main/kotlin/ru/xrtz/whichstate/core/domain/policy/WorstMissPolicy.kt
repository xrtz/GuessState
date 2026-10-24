package ru.xrtz.whichstate.core.domain.policy

import ru.xrtz.whichstate.core.domain.model.Answer
import ru.xrtz.whichstate.core.domain.model.Verdict
import javax.inject.Inject

class WorstMissPolicy @Inject constructor() {
    fun worstMiss(answers: List<Answer>): Answer? =
        answers.filter { it.verdict != Verdict.CORRECT }
            .maxByOrNull { it.question.city.population }
}
