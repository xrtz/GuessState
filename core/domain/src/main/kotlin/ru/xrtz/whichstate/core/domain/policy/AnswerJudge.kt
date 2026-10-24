package ru.xrtz.whichstate.core.domain.policy

import ru.xrtz.whichstate.core.domain.model.Question
import ru.xrtz.whichstate.core.domain.model.UsState
import ru.xrtz.whichstate.core.domain.model.Verdict

class AnswerJudge {
    fun judge(question: Question, picked: UsState): Verdict = when {
        picked in question.acceptedStates -> Verdict.CORRECT
        question.acceptedStates.any { picked in it.neighbors } -> Verdict.NEAR_MISS
        else -> Verdict.WRONG
    }
}
