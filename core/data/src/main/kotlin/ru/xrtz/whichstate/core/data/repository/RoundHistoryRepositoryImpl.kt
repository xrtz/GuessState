package ru.xrtz.whichstate.core.data.repository

import androidx.room.withTransaction
import ru.xrtz.whichstate.core.data.local.db.WhichStateDatabase
import ru.xrtz.whichstate.core.data.local.db.dao.RoundDao
import ru.xrtz.whichstate.core.data.local.db.dao.StateProgressDao
import ru.xrtz.whichstate.core.data.local.db.entity.RoundAnswerEntity
import ru.xrtz.whichstate.core.data.local.db.entity.RoundEntity
import ru.xrtz.whichstate.core.data.local.db.entity.StateProgressEntity
import ru.xrtz.whichstate.core.domain.model.Answer
import ru.xrtz.whichstate.core.domain.model.City
import ru.xrtz.whichstate.core.domain.model.Level
import ru.xrtz.whichstate.core.domain.model.Question
import ru.xrtz.whichstate.core.domain.model.RoundHistoryEntry
import ru.xrtz.whichstate.core.domain.model.RoundId
import ru.xrtz.whichstate.core.domain.model.RoundResult
import ru.xrtz.whichstate.core.domain.model.UsState
import ru.xrtz.whichstate.core.domain.model.Verdict
import ru.xrtz.whichstate.core.domain.repository.CityRepository
import ru.xrtz.whichstate.core.domain.repository.RoundHistoryRepository
import javax.inject.Inject

private const val MODE_CLASSIC = "CLASSIC"

internal class RoundHistoryRepositoryImpl @Inject constructor(
    private val database: WhichStateDatabase,
    private val roundDao: RoundDao,
    private val stateProgressDao: StateProgressDao,
    private val cityRepository: CityRepository,
) : RoundHistoryRepository {
    override suspend fun saveFinished(result: RoundResult): RoundId = database.withTransaction {
        val roundEntity = RoundEntity(
            mode = MODE_CLASSIC,
            level = result.level.name,
            startedAtMs = result.startedAtMs,
            durationMs = result.durationMs,
            correct = result.correctCount,
            wrong = result.wrongCount,
        )
        val roundId = roundDao.insertRound(roundEntity)

        val answerEntities = result.answers.mapIndexed { index, answer ->
            RoundAnswerEntity(
                roundId = roundId,
                position = index,
                cityId = answer.question.city.id.value,
                pickedState = answer.picked.name,
                verdict = answer.verdict.name,
                atMs = answer.atMs,
            )
        }
        roundDao.insertAnswers(answerEntities)

        applyProgressDeltas(result.answers)

        RoundId(roundId)
    }

    private suspend fun applyProgressDeltas(answers: List<Answer>) {
        for ((state, answersForState) in answers.groupBy { it.question.city.state }) {
            val correctAnswers = answersForState.filter { it.verdict == Verdict.CORRECT }
            val existing = stateProgressDao.get(state.name)
            val firstMasteredAtMs = existing?.firstMasteredAtMs
                ?: correctAnswers.minOfOrNull { it.atMs }
            stateProgressDao.upsert(
                StateProgressEntity(
                    state = state.name,
                    correctCount = (existing?.correctCount ?: 0) + correctAnswers.size,
                    wrongCount = (existing?.wrongCount ?: 0) + (answersForState.size - correctAnswers.size),
                    firstMasteredAtMs = firstMasteredAtMs,
                ),
            )
        }
    }

    override suspend fun get(id: RoundId): RoundResult {
        val roundEntity = requireNotNull(roundDao.getRound(id.value)) { "Round ${id.value} not found" }
        val answerEntities = roundDao.getAnswers(id.value)
        val level = Level.valueOf(roundEntity.level)

        val allCities = cityRepository.cities()
        val citiesById = allCities.associateBy { it.id.value }
        val acceptedStatesByName = acceptedStatesByName(allCities, level)

        val answers = answerEntities.map { entity ->
            val city = citiesById.getValue(entity.cityId)
            Answer(
                question = Question(city, acceptedStatesByName.getValue(city.name)),
                picked = UsState.valueOf(entity.pickedState),
                verdict = Verdict.valueOf(entity.verdict),
                atMs = entity.atMs,
            )
        }

        return RoundResult(
            level = level,
            answers = answers,
            startedAtMs = roundEntity.startedAtMs,
            durationMs = roundEntity.durationMs,
        )
    }

    private fun acceptedStatesByName(allCities: List<City>, level: Level): Map<String, Set<UsState>> =
        allCities.filter { it.minLevel <= level }
            .groupBy { it.name }
            .mapValues { (_, cities) -> cities.map { it.state }.toSet() }

    override suspend fun bestForLevel(level: Level): RoundHistoryEntry? =
        roundDao.getBestForLevel(MODE_CLASSIC, level.name)?.toHistoryEntry()

    override suspend fun recentForLevel(level: Level, limit: Int): List<RoundHistoryEntry> =
        roundDao.getRecentForLevel(MODE_CLASSIC, level.name, limit).map { it.toHistoryEntry() }

    private fun RoundEntity.toHistoryEntry() = RoundHistoryEntry(
        roundId = RoundId(id),
        level = Level.valueOf(level),
        correct = correct,
        wrong = wrong,
        playedAtMs = startedAtMs,
    )
}
