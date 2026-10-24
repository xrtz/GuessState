package ru.xrtz.whichstate.core.data.repository

import androidx.room.Room
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import ru.xrtz.whichstate.core.data.asset.CitiesAssetDataSource
import ru.xrtz.whichstate.core.data.local.db.WhichStateDatabase
import ru.xrtz.whichstate.core.domain.model.Answer
import ru.xrtz.whichstate.core.domain.model.Level
import ru.xrtz.whichstate.core.domain.model.Question
import ru.xrtz.whichstate.core.domain.model.RoundResult
import ru.xrtz.whichstate.core.domain.model.UsState
import ru.xrtz.whichstate.core.domain.model.Verdict
import kotlin.test.assertEquals

@RunWith(RobolectricTestRunner::class)
class ProgressRepositoryImplTest {
    private val database = Room
        .inMemoryDatabaseBuilder(RuntimeEnvironment.getApplication(), WhichStateDatabase::class.java)
        .build()
    private val cityRepository = CityRepositoryImpl(CitiesAssetDataSource(RuntimeEnvironment.getApplication()))
    private val historyRepository = RoundHistoryRepositoryImpl(
        database = database,
        roundDao = database.roundDao(),
        stateProgressDao = database.stateProgressDao(),
        cityRepository = cityRepository,
    )
    private val progressRepository = ProgressRepositoryImpl(database.stateProgressDao())

    private suspend fun city(id: String) = cityRepository.cities().single { it.id.value == id }

    @Test
    fun `only states with at least one correct answer are mastered`() = runTest {
        val newYork = city("new-york-ny")
        val losAngeles = city("los-angeles-ca")
        val chicago = city("chicago-il")

        historyRepository.saveFinished(
            RoundResult(
                level = Level.ROOKIE,
                startedAtMs = 0,
                durationMs = 30_000,
                answers = listOf(

                    Answer(
                        Question(newYork, setOf(UsState.NY)),
                        picked = UsState.NY,
                        verdict = Verdict.CORRECT,
                        atMs = 100,
                    ),

                    Answer(
                        Question(losAngeles, setOf(UsState.CA)),
                        picked = UsState.NV,
                        verdict = Verdict.WRONG,
                        atMs = 200,
                    ),

                    Answer(
                        Question(chicago, setOf(UsState.IL)),
                        picked = UsState.IN,
                        verdict = Verdict.NEAR_MISS,
                        atMs = 300,
                    ),
                ),
            ),
        )

        val mastered = progressRepository.observeMastered().first()

        assertEquals(setOf(UsState.NY), mastered)
    }

    @Test
    fun `a state becomes mastered once a later round finally gets it right`() = runTest {
        val losAngeles = city("los-angeles-ca")

        historyRepository.saveFinished(
            RoundResult(
                level = Level.ROOKIE,
                startedAtMs = 0,
                durationMs = 30_000,
                answers = listOf(
                    Answer(
                        Question(losAngeles, setOf(UsState.CA)),
                        picked = UsState.NV,
                        verdict = Verdict.WRONG,
                        atMs = 100,
                    ),
                ),
            ),
        )
        assertEquals(emptySet(), progressRepository.observeMastered().first())

        historyRepository.saveFinished(
            RoundResult(
                level = Level.ROOKIE,
                startedAtMs = 60_000,
                durationMs = 30_000,
                answers = listOf(
                    Answer(
                        Question(losAngeles, setOf(UsState.CA)),
                        picked = UsState.CA,
                        verdict = Verdict.CORRECT,
                        atMs = 60_100,
                    ),
                ),
            ),
        )

        assertEquals(setOf(UsState.CA), progressRepository.observeMastered().first())
    }
}
