package ru.xrtz.whichstate.core.domain.policy

import ru.xrtz.whichstate.core.domain.model.City
import ru.xrtz.whichstate.core.domain.model.CityId
import ru.xrtz.whichstate.core.domain.model.Level
import ru.xrtz.whichstate.core.domain.model.Question
import ru.xrtz.whichstate.core.domain.model.UsState
import ru.xrtz.whichstate.core.domain.model.Verdict
import kotlin.test.Test
import kotlin.test.assertEquals

class AnswerJudgeTest {
    private val judge = AnswerJudge()

    private fun city(name: String, state: UsState) =
        City(CityId(name), name, state, population = 100_000, minLevel = Level.ROOKIE)

    @Test
    fun `picking the accepted state is CORRECT`() {
        val question = Question(city("Austin", UsState.TX), acceptedStates = setOf(UsState.TX))

        assertEquals(Verdict.CORRECT, judge.judge(question, UsState.TX))
    }

    @Test
    fun `picking a neighbor of the accepted state is NEAR_MISS`() {
        val question = Question(city("Austin", UsState.TX), acceptedStates = setOf(UsState.TX))

        assertEquals(Verdict.NEAR_MISS, judge.judge(question, UsState.OK))
    }

    @Test
    fun `picking a non-neighboring wrong state is WRONG`() {
        val question = Question(city("Austin", UsState.TX), acceptedStates = setOf(UsState.TX))

        assertEquals(Verdict.WRONG, judge.judge(question, UsState.NY))
    }

    @Test
    fun `same-name city question accepts any of the pool's matching states`() {
        val question = Question(city("Springfield", UsState.MO), acceptedStates = setOf(UsState.MO, UsState.MA))

        assertEquals(Verdict.CORRECT, judge.judge(question, UsState.MO))
        assertEquals(Verdict.CORRECT, judge.judge(question, UsState.MA))
    }

    @Test
    fun `near-miss checks neighbors of every accepted state, not just the city's own state`() {
        val question = Question(city("Springfield", UsState.MO), acceptedStates = setOf(UsState.MO, UsState.MA))

        assertEquals(Verdict.NEAR_MISS, judge.judge(question, UsState.CT))
    }
}
