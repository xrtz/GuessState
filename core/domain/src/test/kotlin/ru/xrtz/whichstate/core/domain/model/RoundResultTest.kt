package ru.xrtz.whichstate.core.domain.model

import kotlin.test.Test
import kotlin.test.assertEquals

class RoundResultTest {
    private fun answer(state: UsState, verdict: Verdict): Answer {
        val city = City(CityId(state.name), state.name, state, population = 100_000, Level.ROOKIE)
        return Answer(Question(city, setOf(state)), picked = state, verdict = verdict, atMs = 0)
    }

    @Test
    fun `correctCount counts only CORRECT answers`() {
        val result = RoundResult(
            level = Level.ROOKIE,
            answers = listOf(
                answer(UsState.TX, Verdict.CORRECT),
                answer(UsState.NY, Verdict.CORRECT),
                answer(UsState.CA, Verdict.WRONG),
                answer(UsState.ID, Verdict.NEAR_MISS),
            ),
            startedAtMs = 0,
            durationMs = 30_000,
        )

        assertEquals(2, result.correctCount)
    }

    @Test
    fun `wrongCount counts NEAR_MISS together with WRONG, matching the site's scoring`() {
        val result = RoundResult(
            level = Level.ROOKIE,
            answers = listOf(
                answer(UsState.TX, Verdict.CORRECT),
                answer(UsState.CA, Verdict.WRONG),
                answer(UsState.ID, Verdict.NEAR_MISS),
            ),
            startedAtMs = 0,
            durationMs = 30_000,
        )

        assertEquals(2, result.wrongCount)
    }
}
