package ru.xrtz.whichstate.core.domain.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import ru.xrtz.whichstate.core.domain.model.Answer
import ru.xrtz.whichstate.core.domain.model.City
import ru.xrtz.whichstate.core.domain.model.CityId
import ru.xrtz.whichstate.core.domain.model.Level
import ru.xrtz.whichstate.core.domain.model.Question
import ru.xrtz.whichstate.core.domain.model.RoundId
import ru.xrtz.whichstate.core.domain.model.RoundResult
import ru.xrtz.whichstate.core.domain.model.UsState
import ru.xrtz.whichstate.core.domain.model.Verdict
import ru.xrtz.whichstate.core.domain.policy.AccuracyCalculator
import ru.xrtz.whichstate.core.domain.policy.PercentileTable
import ru.xrtz.whichstate.core.domain.policy.RankPolicy
import ru.xrtz.whichstate.core.domain.policy.WorstMissPolicy
import ru.xrtz.whichstate.core.domain.repository.ProgressRepository
import ru.xrtz.whichstate.core.domain.repository.RoundHistoryRepository
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class GetRoundSummaryUseCaseTest {
    private fun answer(name: String, state: UsState, population: Int, verdict: Verdict): Answer {
        val city = City(CityId(name), name, state, population, Level.ROOKIE)
        return Answer(Question(city, setOf(state)), picked = state, verdict = verdict, atMs = 0)
    }

    private fun useCase(result: RoundResult, mastered: Set<UsState>): GetRoundSummaryUseCase {
        val historyRepository = object : RoundHistoryRepository {
            override suspend fun saveFinished(result: RoundResult) = error("not used in this test")
            override suspend fun get(id: RoundId): RoundResult = result
        }
        val progressRepository = object : ProgressRepository {
            override fun observeMastered(): Flow<Set<UsState>> = flowOf(mastered)
        }
        return GetRoundSummaryUseCase(
            historyRepository,
            progressRepository,
            PercentileTable(),
            RankPolicy(),
            WorstMissPolicy(),
            AccuracyCalculator(),
        )
    }

    @Test
    fun `composes correct count, percentile, rank, shame line, accuracy and mastered count`() = runBlocking {
        val result = RoundResult(
            level = Level.ROOKIE,
            answers = listOf(
                answer("Austin", UsState.TX, population = 965_893, Verdict.CORRECT),
                answer("New York", UsState.NY, population = 8_804_190, Verdict.WRONG),
            ),
            startedAtMs = 0,
            durationMs = 30_000,
        )
        val useCase = useCase(result, mastered = setOf(UsState.TX, UsState.CA))

        val summary = useCase(RoundId(1))

        assertEquals(1, summary.correct)
        assertEquals(1, summary.wrong)
        assertEquals(5, summary.percentile)
        assertEquals(0, summary.rankTier)
        assertEquals(1, summary.rankIndex)
        assertEquals("New York", summary.worstMiss?.question?.city?.name)
        assertEquals(0.0f, summary.accuracyByState.getValue(UsState.NY))
        assertEquals(1.0f, summary.accuracyByState.getValue(UsState.TX))
        assertEquals(2, summary.masteredCount)
        assertEquals(50, summary.totalStates)
        assertEquals(result.answers, summary.answers)
    }

    @Test
    fun `a flawless round has no shame line`() = runBlocking {
        val result = RoundResult(
            level = Level.ROOKIE,
            answers = listOf(answer("Austin", UsState.TX, population = 965_893, Verdict.CORRECT)),
            startedAtMs = 0,
            durationMs = 30_000,
        )
        val useCase = useCase(result, mastered = emptySet())

        val summary = useCase(RoundId(1))

        assertNotNull(summary)
        assertEquals(null, summary.worstMiss)
    }
}
