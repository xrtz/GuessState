package ru.xrtz.whichstate.core.domain.policy

import ru.xrtz.whichstate.core.domain.model.Answer
import ru.xrtz.whichstate.core.domain.model.City
import ru.xrtz.whichstate.core.domain.model.CityId
import ru.xrtz.whichstate.core.domain.model.Level
import ru.xrtz.whichstate.core.domain.model.Question
import ru.xrtz.whichstate.core.domain.model.UsState
import ru.xrtz.whichstate.core.domain.model.Verdict
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class AccuracyCalculatorTest {
    private val calculator = AccuracyCalculator()

    private fun answer(name: String, state: UsState, verdict: Verdict): Answer {
        val city = City(CityId(name), name, state, population = 100_000, Level.ROOKIE)
        val question = Question(city, acceptedStates = setOf(state))
        return Answer(question, picked = UsState.AK, verdict = verdict, atMs = 0)
    }

    @Test
    fun `computes correct-over-total per state, not globally`() {
        val answers = listOf(
            answer("Austin", UsState.TX, Verdict.CORRECT),
            answer("Houston", UsState.TX, Verdict.WRONG),
            answer("Boise", UsState.ID, Verdict.CORRECT),
        )

        val accuracy = calculator.accuracyByState(answers)

        assertEquals(0.5f, accuracy.getValue(UsState.TX))
        assertEquals(1.0f, accuracy.getValue(UsState.ID))
    }

    @Test
    fun `near-miss counts as not-correct for accuracy purposes`() {
        val answers = listOf(answer("Austin", UsState.TX, Verdict.NEAR_MISS))

        assertEquals(0.0f, calculator.accuracyByState(answers).getValue(UsState.TX))
    }

    @Test
    fun `a state with no answers is absent from the map, not zero`() {
        val answers = listOf(answer("Austin", UsState.TX, Verdict.CORRECT))

        assertNull(calculator.accuracyByState(answers)[UsState.NY])
    }
}
