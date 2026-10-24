package ru.xrtz.whichstate.core.domain.usecase

import kotlinx.coroutines.runBlocking
import ru.xrtz.whichstate.core.domain.model.Level
import ru.xrtz.whichstate.core.domain.model.RoundId
import ru.xrtz.whichstate.core.domain.model.RoundResult
import ru.xrtz.whichstate.core.domain.repository.AdSessionRepository
import ru.xrtz.whichstate.core.domain.repository.RoundHistoryRepository
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame

class FinishClassicRoundUseCaseTest {
    private class FakeAdSessionRepository : AdSessionRepository {
        var rounds = 0
            private set

        override fun roundsSinceInterstitial() = rounds
        override fun onRoundFinished() { rounds++ }
        override fun onInterstitialShown() { rounds = 0 }
    }

    @Test
    fun `delegates saving to the repository and returns its assigned id`() = runBlocking {
        val result = RoundResult(level = Level.ROOKIE, answers = emptyList(), startedAtMs = 0, durationMs = 30_000)
        val savedId = RoundId(42)
        var savedResult: RoundResult? = null
        val repository = object : RoundHistoryRepository {
            override suspend fun saveFinished(result: RoundResult): RoundId {
                savedResult = result
                return savedId
            }

            override suspend fun get(id: RoundId): RoundResult = error("not used in this test")
        }
        val useCase = FinishClassicRoundUseCase(repository, FakeAdSessionRepository())

        val returnedId = useCase(result)

        assertSame(result, savedResult)
        assertEquals(savedId, returnedId)
    }

    @Test
    fun `counts the round toward the interstitial cadence`() = runBlocking {
        val result = RoundResult(level = Level.ROOKIE, answers = emptyList(), startedAtMs = 0, durationMs = 30_000)
        val repository = object : RoundHistoryRepository {
            override suspend fun saveFinished(result: RoundResult) = RoundId(1)
            override suspend fun get(id: RoundId): RoundResult = error("not used in this test")
        }
        val adSessionRepository = FakeAdSessionRepository()
        val useCase = FinishClassicRoundUseCase(repository, adSessionRepository)

        useCase(result)

        assertEquals(1, adSessionRepository.rounds)
    }
}
