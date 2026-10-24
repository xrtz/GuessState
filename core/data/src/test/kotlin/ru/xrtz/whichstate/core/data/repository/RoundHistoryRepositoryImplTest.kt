package ru.xrtz.whichstate.core.data.repository

import androidx.room.Room
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import ru.xrtz.whichstate.core.data.asset.CitiesAssetDataSource
import ru.xrtz.whichstate.core.data.local.db.WhichStateDatabase
import ru.xrtz.whichstate.core.domain.model.Answer
import ru.xrtz.whichstate.core.domain.model.City
import ru.xrtz.whichstate.core.domain.model.CityId
import ru.xrtz.whichstate.core.domain.model.Level
import ru.xrtz.whichstate.core.domain.model.Question
import ru.xrtz.whichstate.core.domain.model.RoundId
import ru.xrtz.whichstate.core.domain.model.RoundResult
import ru.xrtz.whichstate.core.domain.model.UsState
import ru.xrtz.whichstate.core.domain.model.Verdict
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

@RunWith(RobolectricTestRunner::class)
class RoundHistoryRepositoryImplTest {
    private val database = Room
        .inMemoryDatabaseBuilder(RuntimeEnvironment.getApplication(), WhichStateDatabase::class.java)
        .build()
    private val cityRepository = CityRepositoryImpl(CitiesAssetDataSource(RuntimeEnvironment.getApplication()))
    private val repository = RoundHistoryRepositoryImpl(
        database = database,
        roundDao = database.roundDao(),
        stateProgressDao = database.stateProgressDao(),
        cityRepository = cityRepository,
    )

    private suspend fun city(id: String) = cityRepository.cities().single { it.id.value == id }

    private suspend fun saveRound(level: Level, correct: Int, wrong: Int, startedAtMs: Long): RoundId {
        val answers = (0 until correct).map { syntheticAnswer(Verdict.CORRECT, it) } +
            (0 until wrong).map { syntheticAnswer(Verdict.WRONG, correct + it) }
        return repository.saveFinished(
            RoundResult(level = level, answers = answers, startedAtMs = startedAtMs, durationMs = 30_000),
        )
    }

    private fun syntheticAnswer(verdict: Verdict, index: Int): Answer {
        val city = City(CityId("city-$index"), "City $index", UsState.TX, population = 1, minLevel = Level.ROOKIE)
        return Answer(Question(city, setOf(UsState.TX)), picked = UsState.TX, verdict = verdict, atMs = 0)
    }

    @Test
    fun `a saved round reads back with the same level, order, and verdicts`() = runTest {
        val springfieldMo = city("springfield-mo")
        val newYork = city("new-york-ny")

        val result = RoundResult(
            level = Level.MASTER,
            startedAtMs = 1_000,
            durationMs = 30_000,
            answers = listOf(
                Answer(
                    Question(newYork, setOf(UsState.NY)),
                    picked = UsState.NY,
                    verdict = Verdict.CORRECT,
                    atMs = 1_500,
                ),

                Answer(
                    Question(springfieldMo, setOf(UsState.MO, UsState.MA)),
                    picked = UsState.MA,
                    verdict = Verdict.CORRECT,
                    atMs = 2_000,
                ),
                Answer(
                    Question(newYork, setOf(UsState.NY)),
                    picked = UsState.NJ,
                    verdict = Verdict.NEAR_MISS,
                    atMs = 2_500,
                ),
            ),
        )

        val roundId = repository.saveFinished(result)
        val readBack = repository.get(roundId)

        assertEquals(Level.MASTER, readBack.level)
        assertEquals(30_000, readBack.durationMs)
        assertEquals(1_000, readBack.startedAtMs)
        assertEquals(
            result.answers.map { it.picked to it.verdict },
            readBack.answers.map { it.picked to it.verdict },
        )
        assertEquals(result.answers.map { it.atMs }, readBack.answers.map { it.atMs })
        assertEquals(2, readBack.correctCount)
        assertEquals(1, readBack.wrongCount)

        val springfieldAnswer = readBack.answers[1]
        assertEquals(CityId("springfield-mo"), springfieldAnswer.question.city.id)
        assertEquals(setOf(UsState.MO, UsState.MA), springfieldAnswer.question.acceptedStates)
    }

    @Test
    fun `reading an unknown round id fails loudly instead of returning garbage`() = runTest {
        assertFailsWith<IllegalArgumentException> { repository.get(RoundId(999_999)) }
    }

    @Test
    fun `best for a level picks the highest correct count`() = runTest {
        saveRound(Level.ROOKIE, correct = 10, wrong = 5, startedAtMs = 1_000)
        saveRound(Level.ROOKIE, correct = 25, wrong = 2, startedAtMs = 2_000)
        saveRound(Level.ROOKIE, correct = 18, wrong = 0, startedAtMs = 3_000)

        val best = repository.bestForLevel(Level.ROOKIE)

        assertEquals(25, best?.correct)
    }

    @Test
    fun `best for a level with no rounds yet is null`() = runTest {
        assertEquals(null, repository.bestForLevel(Level.MASTER))
    }

    @Test
    fun `recent for a level returns newest first, capped at the limit`() = runTest {
        saveRound(Level.EXPLORER, correct = 1, wrong = 0, startedAtMs = 1_000)
        saveRound(Level.EXPLORER, correct = 2, wrong = 0, startedAtMs = 3_000)
        saveRound(Level.EXPLORER, correct = 3, wrong = 0, startedAtMs = 2_000)

        val recent = repository.recentForLevel(Level.EXPLORER, limit = 2)

        assertEquals(listOf(3_000L, 2_000L), recent.map { it.playedAtMs })
    }

    @Test
    fun `recent for a level only includes rounds from that level`() = runTest {
        saveRound(Level.ROOKIE, correct = 5, wrong = 0, startedAtMs = 1_000)
        saveRound(Level.MASTER, correct = 5, wrong = 0, startedAtMs = 2_000)

        val recent = repository.recentForLevel(Level.ROOKIE, limit = 100)

        assertEquals(1, recent.size)
        assertEquals(Level.ROOKIE, recent.single().level)
    }
}
