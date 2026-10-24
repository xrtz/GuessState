package ru.xrtz.whichstate.feature.results

import ru.xrtz.whichstate.core.designsystem.components.ResultRowVariant
import ru.xrtz.whichstate.core.domain.model.Answer
import ru.xrtz.whichstate.core.domain.model.City
import ru.xrtz.whichstate.core.domain.model.CityId
import ru.xrtz.whichstate.core.domain.model.Level
import ru.xrtz.whichstate.core.domain.model.Question
import ru.xrtz.whichstate.core.domain.model.RoundSummary
import ru.xrtz.whichstate.core.domain.model.UsState
import ru.xrtz.whichstate.core.domain.model.Verdict
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ResultsUiMapperTest {
    private fun answer(name: String, state: UsState, picked: UsState, verdict: Verdict): Answer {
        val city = City(CityId(name), name, state, population = 100_000, minLevel = Level.ROOKIE)
        return Answer(Question(city, setOf(state)), picked = picked, verdict = verdict, atMs = 0)
    }

    private fun summary(answers: List<Answer>, worstMiss: Answer? = null) = RoundSummary(
        level = Level.ROOKIE,
        correct = answers.count { it.verdict == Verdict.CORRECT },
        wrong = answers.count { it.verdict != Verdict.CORRECT },
        percentile = 42,
        rankTier = 2,
        rankIndex = 0,
        worstMiss = worstMiss,
        accuracyByState = emptyMap(),
        masteredCount = 3,
        totalStates = 50,
        answers = answers,
    )

    @Test
    fun `total is correct plus wrong, and total states carries through`() {
        val state = summary(
            answers = listOf(
                answer("Austin", UsState.TX, UsState.TX, Verdict.CORRECT),
                answer("Trenton", UsState.NJ, UsState.PA, Verdict.WRONG),
            ),
        ).toUiState()

        assertEquals(1, state.correct)
        assertEquals(1, state.wrong)
        assertEquals(2, state.total)
        assertEquals(3, state.masteredCount)
        assertEquals(50, state.totalStates)
    }

    @Test
    fun `correct rows show just the state code`() {
        val state = summary(
            answers = listOf(answer("Austin", UsState.TX, UsState.TX, Verdict.CORRECT)),
        ).toUiState()

        val row = state.cities.single()
        assertEquals("Austin", row.cityName)
        assertEquals("TX", row.stateText)
        assertEquals(ResultRowVariant.CORRECT, row.variant)
    }

    @Test
    fun `wrong rows show picked to correct with no tilde`() {
        val state = summary(
            answers = listOf(answer("Trenton", UsState.NJ, UsState.PA, Verdict.WRONG)),
        ).toUiState()

        val row = state.cities.single()
        assertEquals("PA → NJ", row.stateText)
        assertEquals(ResultRowVariant.WRONG, row.variant)
    }

    @Test
    fun `near-miss rows are tagged with a trailing tilde`() {
        val state = summary(
            answers = listOf(answer("Kansas City", UsState.MO, UsState.KS, Verdict.NEAR_MISS)),
        ).toUiState()

        val row = state.cities.single()
        assertEquals("KS → MO ~", row.stateText)
        assertEquals(ResultRowVariant.NEAR_MISS, row.variant)
    }

    @Test
    fun `shame line fields are null for a flawless round`() {
        val state = summary(
            answers = listOf(answer("Austin", UsState.TX, UsState.TX, Verdict.CORRECT)),
            worstMiss = null,
        ).toUiState()

        assertNull(state.shameCityName)
        assertNull(state.shamePickedState)
    }

    @Test
    fun `shame line carries the worst miss's city and picked state`() {
        val miss = answer("Trenton", UsState.NJ, UsState.PA, Verdict.WRONG)
        val state = summary(answers = listOf(miss), worstMiss = miss).toUiState()

        assertEquals("Trenton", state.shameCityName)
        assertEquals("PA", state.shamePickedState)
    }
}
