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

class WorstMissPolicyTest {
    private val policy = WorstMissPolicy()

    private fun answer(name: String, state: UsState, population: Int, verdict: Verdict): Answer {
        val city = City(CityId(name), name, state, population, Level.ROOKIE)
        val question = Question(city, acceptedStates = setOf(state))
        return Answer(question, picked = UsState.AK, verdict = verdict, atMs = 0)
    }

    @Test
    fun `picks the highest-population answer among wrong and near-miss ones`() {
        val answers = listOf(
            answer("New York", UsState.NY, population = 8_804_190, verdict = Verdict.WRONG),
            answer("Austin", UsState.TX, population = 965_893, verdict = Verdict.CORRECT),
            answer("Boise", UsState.ID, population = 236_043, verdict = Verdict.NEAR_MISS),
        )

        assertEquals("New York", policy.worstMiss(answers)?.question?.city?.name)
    }

    @Test
    fun `a near-miss can be the worst miss even if a smaller wrong answer exists`() {
        val answers = listOf(
            answer("Small Wrong Town", UsState.WY, population = 1_000, verdict = Verdict.WRONG),
            answer("Big Near Miss City", UsState.NY, population = 8_804_190, verdict = Verdict.NEAR_MISS),
        )

        assertEquals("Big Near Miss City", policy.worstMiss(answers)?.question?.city?.name)
    }

    @Test
    fun `no wrong or near-miss answers means no shame result`() {
        val answers = listOf(
            answer("Austin", UsState.TX, population = 965_893, verdict = Verdict.CORRECT),
        )

        assertNull(policy.worstMiss(answers))
    }

    @Test
    fun `empty answer list means no shame result`() {
        assertNull(policy.worstMiss(emptyList()))
    }
}
