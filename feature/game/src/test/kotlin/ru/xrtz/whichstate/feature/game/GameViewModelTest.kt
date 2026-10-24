package ru.xrtz.whichstate.feature.game

import androidx.lifecycle.viewModelScope
import app.cash.turbine.test
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.Timeout
import ru.xrtz.whichstate.core.domain.model.City
import ru.xrtz.whichstate.core.domain.model.CityId
import ru.xrtz.whichstate.core.domain.model.Level
import ru.xrtz.whichstate.core.domain.model.RoundId
import ru.xrtz.whichstate.core.domain.model.UsState
import ru.xrtz.whichstate.core.domain.model.Verdict
import ru.xrtz.whichstate.core.domain.usecase.FinishClassicRoundUseCase
import ru.xrtz.whichstate.core.domain.usecase.StartClassicRoundUseCase
import ru.xrtz.whichstate.core.testing.fake.FakeAdSessionRepository
import ru.xrtz.whichstate.core.testing.fake.FakeCityRepository
import ru.xrtz.whichstate.core.testing.fake.FakeGameClock
import ru.xrtz.whichstate.core.testing.fake.FakeRoundHistoryRepository
import kotlin.random.Random
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class GameViewModelTest {
    @get:Rule
    val timeout: Timeout = Timeout.seconds(5)

    private val dispatcher = StandardTestDispatcher()

    private val cities = listOf(
        city("Springfield", UsState.MO),
        city("Columbus", UsState.OH),
        city("Austin", UsState.TX),
    )
    private val cityByName = cities.associateBy { it.name }

    private val clock = FakeGameClock()
    private val historyRepository = FakeRoundHistoryRepository()

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun city(name: String, state: UsState) =
        City(CityId("$name-${state.name}"), name, state, population = 100_000, minLevel = Level.ROOKIE)

    private fun runGameTest(body: suspend TestScope.(GameViewModel) -> Unit) = runTest {
        val viewModel = GameViewModel(
            level = Level.ROOKIE,
            startClassicRound = StartClassicRoundUseCase(FakeCityRepository(cities), Random(42)),
            finishClassicRound = FinishClassicRoundUseCase(historyRepository, FakeAdSessionRepository()),
            clock = clock,
        )

        backgroundScope.launch { viewModel.uiState.collect {} }
        dispatcher.scheduler.runCurrent()
        try {
            body(viewModel)
        } finally {
            viewModel.viewModelScope.cancel()
        }
    }

    @Test
    fun `round starts with the first shuffled city and a full timer`() = runGameTest { viewModel ->
        val state = viewModel.uiState.value

        assertTrue(state.cityName in cityByName.keys)
        assertEquals(30, state.secondsLeft)
        assertEquals(0, state.correct)
        assertEquals(0, state.wrong)
    }

    @Test
    fun `a correct tap shows a correct highlight and bumps the score once feedback is read`() =
        runGameTest { viewModel ->
        val city = cityByName.getValue(viewModel.uiState.value.cityName)
        viewModel.onAction(GameAction.TapState(city.state))
        dispatcher.scheduler.runCurrent()

        val afterTap = viewModel.uiState.value
        assertEquals(city.state, afterTap.highlight?.pickedState)
        assertEquals(Verdict.CORRECT, afterTap.highlight?.verdict)
        assertEquals(1, afterTap.correct)
    }

    @Test
    fun `feedback clears and the next city appears once its window elapses`() = runGameTest { viewModel ->
        val firstCity = viewModel.uiState.value.cityName
        viewModel.onAction(GameAction.TapState(cityByName.getValue(firstCity).state))

        clock.advanceBy(600)
        advanceTimeBy(200)
        dispatcher.scheduler.runCurrent()

        val afterFeedback = viewModel.uiState.value
        assertEquals(null, afterFeedback.highlight)
        assertTrue(afterFeedback.cityName != firstCity)
    }

    @Test
    fun `the countdown advances on its own without any taps`() = runGameTest { viewModel ->
        clock.advanceBy(5_000)
        advanceTimeBy(200)
        dispatcher.scheduler.runCurrent()

        assertEquals(25, viewModel.uiState.value.secondsLeft)
    }

    @Test
    fun `exiting to menu emits an effect without touching round state`() = runGameTest { viewModel ->
        viewModel.effects.test {
            viewModel.onAction(GameAction.ExitToMenu)
            assertEquals(GameEffect.ExitToMenu, awaitItem())
        }
    }

    @Test
    fun `time running out finishes the round and emits OpenResults with a saved roundId`() = runGameTest { viewModel ->
        viewModel.effects.test {
            clock.advanceBy(31_000)
            advanceTimeBy(200)
            dispatcher.scheduler.runCurrent()

            val effect = awaitItem()
            assertTrue(effect is GameEffect.OpenResults)
            assertEquals(RoundId(1), (effect as GameEffect.OpenResults).roundId)
        }
    }
}
