package ru.xrtz.whichstate.core.domain.game

import ru.xrtz.whichstate.core.domain.model.City
import ru.xrtz.whichstate.core.domain.model.CityId
import ru.xrtz.whichstate.core.domain.model.Level
import ru.xrtz.whichstate.core.domain.model.Question
import ru.xrtz.whichstate.core.domain.model.UsState
import ru.xrtz.whichstate.core.domain.model.Verdict
import ru.xrtz.whichstate.core.domain.policy.AnswerJudge
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertSame

class ClassicRoundReducerTest {
    private val reducer = ClassicRoundReducer(AnswerJudge(), RoundRules())

    private fun city(name: String, state: UsState) =
        City(CityId(name), name, state, population = 100_000, minLevel = Level.ROOKIE)
    private fun question(name: String, state: UsState) = Question(city(name, state), acceptedStates = setOf(state))

    private val q1 = question("Austin", UsState.TX)
    private val q2 = question("Boise", UsState.ID)

    @Test
    fun `a correct tap transitions Asking to Feedback and records the answer`() {
        val state = ClassicRoundState.start(q1, listOf(q2), startedAtMs = 0)

        val next = reducer.reduce(state, RoundEvent.Tap(UsState.TX, nowMs = 100))

        assertEquals(1, next.answers.size)
        assertEquals(Verdict.CORRECT, next.answers.single().verdict)
        val feedback = assertIs<ClassicRoundState.Phase.Feedback>(next.phase)
        assertEquals(q1, feedback.question)
        assertEquals(100, feedback.startedAtMs)
    }

    @Test
    fun `a wrong tap on a non-neighboring state records WRONG`() {
        val state = ClassicRoundState.start(q1, listOf(q2), startedAtMs = 0)

        val next = reducer.reduce(state, RoundEvent.Tap(UsState.NY, nowMs = 100))

        assertEquals(Verdict.WRONG, next.answers.single().verdict)
    }

    @Test
    fun `a wrong tap on a neighboring state records NEAR_MISS`() {
        val state = ClassicRoundState.start(q1, listOf(q2), startedAtMs = 0)

        val next = reducer.reduce(state, RoundEvent.Tap(UsState.OK, nowMs = 100))

        assertEquals(Verdict.NEAR_MISS, next.answers.single().verdict)
    }

    @Test
    fun `tap during feedback phase is a no-op`() {
        val asking = ClassicRoundState.start(q1, listOf(q2), startedAtMs = 0)
        val feedback = reducer.reduce(asking, RoundEvent.Tap(UsState.TX, nowMs = 100))

        val next = reducer.reduce(feedback, RoundEvent.Tap(UsState.NY, nowMs = 150))

        assertEquals(feedback, next)
    }

    @Test
    fun `double tap on the same question is ignored`() {
        val asking = ClassicRoundState.start(q1, listOf(q2), startedAtMs = 0)
        val afterFirstTap = reducer.reduce(asking, RoundEvent.Tap(UsState.TX, nowMs = 100))

        val afterSecondTap = reducer.reduce(afterFirstTap, RoundEvent.Tap(UsState.TX, nowMs = 100))

        assertEquals(1, afterSecondTap.answers.size)
        assertEquals(afterFirstTap, afterSecondTap)
    }

    @Test
    fun `tap while finished is a no-op`() {
        val finished = ClassicRoundState(
            phase = ClassicRoundState.Phase.Finished,
            queue = emptyList(),
            answers = emptyList(),
            startedAtMs = 0,
        )

        val next = reducer.reduce(finished, RoundEvent.Tap(UsState.TX, nowMs = 100))

        assertSame(finished, next)
    }

    @Test
    fun `tick during feedback before the feedback duration elapsed does not advance`() {
        val asking = ClassicRoundState.start(q1, listOf(q2), startedAtMs = 0)
        val feedback = reducer.reduce(asking, RoundEvent.Tap(UsState.TX, nowMs = 1_000))

        val next = reducer.reduce(feedback, RoundEvent.Tick(nowMs = 1_300))

        assertEquals(feedback, next)
    }

    @Test
    fun `tick during feedback after the feedback duration elapsed advances to the next question`() {
        val asking = ClassicRoundState.start(q1, listOf(q2), startedAtMs = 0)
        val feedback = reducer.reduce(asking, RoundEvent.Tap(UsState.TX, nowMs = 1_000))

        val next = reducer.reduce(feedback, RoundEvent.Tick(nowMs = 1_500))

        val nextAsking = assertIs<ClassicRoundState.Phase.Asking>(next.phase)
        assertEquals(q2, nextAsking.question)
        assertEquals(emptyList(), next.queue)
    }

    @Test
    fun `round ends when the city pool is exhausted before time runs out`() {
        val asking = ClassicRoundState.start(q1, remaining = emptyList(), startedAtMs = 0)
        val feedback = reducer.reduce(asking, RoundEvent.Tap(UsState.TX, nowMs = 1_000))

        val next = reducer.reduce(feedback, RoundEvent.Tick(nowMs = 1_500))

        assertEquals(ClassicRoundState.Phase.Finished, next.phase)
    }

    @Test
    fun `round ends when time hits zero even mid-feedback`() {
        val asking = ClassicRoundState.start(q1, listOf(q2), startedAtMs = 0)
        val feedback = reducer.reduce(asking, RoundEvent.Tap(UsState.TX, nowMs = 1_000))

        val next = reducer.reduce(feedback, RoundEvent.Tick(nowMs = 30_000))

        assertEquals(ClassicRoundState.Phase.Finished, next.phase)
    }

    @Test
    fun `pause freezes elapsed time and further ticks are ignored`() {
        val asking = ClassicRoundState.start(q1, listOf(q2), startedAtMs = 0)
        val paused = reducer.reduce(asking, RoundEvent.Pause(nowMs = 1_000))

        val next = reducer.reduce(paused, RoundEvent.Tick(nowMs = 40_000))

        assertEquals(paused, next)
    }

    @Test
    fun `resume folds the paused interval into paused time so it does not count against the round`() {
        val asking = ClassicRoundState.start(q1, listOf(q2), startedAtMs = 0)
        val paused = reducer.reduce(asking, RoundEvent.Pause(nowMs = 1_000))
        val resumed = reducer.reduce(paused, RoundEvent.Resume(nowMs = 10_000))
        assertEquals(9_000L, resumed.pausedTotalMs)

        val stillRunning = reducer.reduce(resumed, RoundEvent.Tick(nowMs = 38_999))
        assertIs<ClassicRoundState.Phase.Asking>(stillRunning.phase)

        val finished = reducer.reduce(stillRunning, RoundEvent.Tick(nowMs = 39_000))
        assertEquals(ClassicRoundState.Phase.Finished, finished.phase)
    }

    @Test
    fun `resume when not paused is a no-op`() {
        val asking = ClassicRoundState.start(q1, listOf(q2), startedAtMs = 0)

        val next = reducer.reduce(asking, RoundEvent.Resume(nowMs = 1_000))

        assertEquals(asking, next)
    }
}
