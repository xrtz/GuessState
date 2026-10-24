package ru.xrtz.whichstate.feature.history

import app.cash.turbine.test
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import ru.xrtz.whichstate.core.domain.model.Answer
import ru.xrtz.whichstate.core.domain.model.City
import ru.xrtz.whichstate.core.domain.model.CityId
import ru.xrtz.whichstate.core.domain.model.Level
import ru.xrtz.whichstate.core.domain.model.Question
import ru.xrtz.whichstate.core.domain.model.RoundResult
import ru.xrtz.whichstate.core.domain.model.UsState
import ru.xrtz.whichstate.core.domain.model.Verdict
import ru.xrtz.whichstate.core.domain.policy.PercentileTable
import ru.xrtz.whichstate.core.domain.usecase.GetHistoryUseCase
import ru.xrtz.whichstate.core.testing.fake.FakeRoundHistoryRepository
import kotlin.test.assertEquals
import kotlin.test.assertIs

@OptIn(ExperimentalCoroutinesApi::class)
class HistoryViewModelTest {
    private val dispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun answer(index: Int, verdict: Verdict): Answer {
        val city = City(CityId("city-$index"), "City $index", UsState.TX, population = 1, minLevel = Level.ROOKIE)
        return Answer(Question(city, setOf(UsState.TX)), picked = UsState.TX, verdict = verdict, atMs = 0)
    }

    private suspend fun FakeRoundHistoryRepository.givenRound(
        level: Level,
        correct: Int,
        wrong: Int,
        startedAtMs: Long,
    ) {
        val answers = (0 until correct).map { answer(it, Verdict.CORRECT) } +
            (0 until wrong).map { answer(correct + it, Verdict.WRONG) }
        saveFinished(RoundResult(level, answers, startedAtMs, durationMs = 30_000))
    }

    private fun viewModel(repository: FakeRoundHistoryRepository = FakeRoundHistoryRepository()) =
        HistoryViewModel(GetHistoryUseCase(repository, PercentileTable()))

    @Test
    fun `uiState starts on Rookie once loaded`() = runTest {
        val repository = FakeRoundHistoryRepository()
        repository.givenRound(Level.ROOKIE, correct = 40, wrong = 10, startedAtMs = 1_000)
        val viewModel = viewModel(repository)

        backgroundScope.launch { viewModel.uiState.collect {} }
        advanceUntilIdle()

        val state = assertIs<HistoryUiState.Loaded>(viewModel.uiState.value)
        assertEquals(Level.ROOKIE, state.selectedLevel)
        assertEquals(40, state.levels.single { it.level == Level.ROOKIE }.best?.correct)
    }

    @Test
    fun `selecting a level switches selectedLevel`() = runTest {
        val repository = FakeRoundHistoryRepository()
        repository.givenRound(Level.EXPLORER, correct = 20, wrong = 5, startedAtMs = 1_000)
        val viewModel = viewModel(repository)
        backgroundScope.launch { viewModel.uiState.collect {} }
        advanceUntilIdle()

        viewModel.onAction(HistoryAction.SelectLevel(Level.EXPLORER))

        val state = assertIs<HistoryUiState.Loaded>(viewModel.uiState.value)
        assertEquals(Level.EXPLORER, state.selectedLevel)
        assertEquals(20, state.levels.single { it.level == Level.EXPLORER }.best?.correct)
    }

    @Test
    fun `back emits a Back effect`() = runTest {
        val viewModel = viewModel()

        viewModel.effects.test {
            viewModel.onAction(HistoryAction.Back)
            assertEquals(HistoryEffect.Back, awaitItem())
        }
    }

    @Test
    fun `a level with no rounds has a null best and empty recent`() = runTest {
        val viewModel = viewModel()
        backgroundScope.launch { viewModel.uiState.collect {} }
        advanceUntilIdle()

        val state = assertIs<HistoryUiState.Loaded>(viewModel.uiState.value)
        val master = state.levels.single { it.level == Level.MASTER }
        assertEquals(null, master.best)
        assertEquals(emptyList(), master.recent)
    }
}
