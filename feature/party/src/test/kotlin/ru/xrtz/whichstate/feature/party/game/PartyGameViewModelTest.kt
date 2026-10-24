package ru.xrtz.whichstate.feature.party.game

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
import ru.xrtz.whichstate.core.domain.model.PartyMode
import ru.xrtz.whichstate.core.domain.model.UsState
import ru.xrtz.whichstate.core.domain.policy.PartyScoring
import ru.xrtz.whichstate.core.domain.usecase.StartPartyGameUseCase
import ru.xrtz.whichstate.core.testing.fake.FakeCityRepository
import ru.xrtz.whichstate.core.testing.fake.FakeGameClock
import kotlin.random.Random
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNull

@OptIn(ExperimentalCoroutinesApi::class)
class PartyGameViewModelTest {
    @get:Rule
    val timeout: Timeout = Timeout.seconds(5)

    private val dispatcher = StandardTestDispatcher()
    private val clock = FakeGameClock()
    private val cities = (1..60).map { City(CityId("c$it"), "City $it", UsState.TX, it, Level.MASTER) }

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun runPartyTest(
        teamNames: List<String> = listOf("", "Owls"),
        penaltyOnSkip: Boolean = false,
        body: suspend TestScope.(PartyGameViewModel) -> Unit,
    ) = runTest {
        val viewModel = PartyGameViewModel(
            mode = PartyMode.GUESS_STATE,
            teamNames = teamNames,
            penaltyOnSkip = penaltyOnSkip,
            startPartyGame = StartPartyGameUseCase(FakeCityRepository(cities), Random(1)),
            scoring = PartyScoring(),
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

    private fun PartyGameViewModel.act(action: PartyGameAction) {
        onAction(action)
        dispatcher.scheduler.runCurrent()
    }

    @Test
    fun `game opens on the first team's handoff with default and custom names`() = runPartyTest { viewModel ->
        val handoff = assertIs<PartyGameUiState.Handoff>(viewModel.uiState.value)

        assertEquals(1, handoff.round)
        assertEquals(3, handoff.totalRounds)
        assertEquals(TeamLabel(1, null), handoff.team)
        assertEquals(listOf(TeamLabel(1, null), TeamLabel(2, "Owls")), handoff.scoreboard.map { it.team })
    }

    @Test
    fun `playing counts down on its own and shows the card position`() = runPartyTest { viewModel ->
        viewModel.act(PartyGameAction.StartTurn)
        clock.advanceBy(4_000)
        advanceTimeBy(200)
        dispatcher.scheduler.runCurrent()

        val playing = assertIs<PartyGameUiState.Playing>(viewModel.uiState.value)
        assertEquals(26, playing.secondsLeft)
        assertEquals(1, playing.cardNumber)
        assertEquals(7, playing.cardCount)
    }

    @Test
    fun `seven answers end the turn with the site's points text`() = runPartyTest(penaltyOnSkip = true) { viewModel ->
        viewModel.act(PartyGameAction.StartTurn)
        repeat(5) { viewModel.act(PartyGameAction.Answer(correct = true)) }
        repeat(2) { viewModel.act(PartyGameAction.Answer(correct = false)) }

        val roundEnd = assertIs<PartyGameUiState.RoundEnd>(viewModel.uiState.value)
        assertEquals(5, roundEnd.correct)
        assertEquals(2, roundEnd.skipped)
        assertEquals("+3", roundEnd.pointsText)
        assertEquals(7, roundEnd.results.size)
    }

    @Test
    fun `time running out ends the turn`() = runPartyTest { viewModel ->
        viewModel.act(PartyGameAction.StartTurn)
        clock.advanceBy(30_000)
        advanceTimeBy(200)
        dispatcher.scheduler.runCurrent()

        assertIs<PartyGameUiState.RoundEnd>(viewModel.uiState.value)
    }

    @Test
    fun `a tied game has no winner`() = runPartyTest { viewModel ->
        repeat(6) {
            viewModel.act(PartyGameAction.StartTurn)
            clock.advanceBy(30_000)
            advanceTimeBy(200)
            dispatcher.scheduler.runCurrent()
            viewModel.act(PartyGameAction.NextTurn)
        }

        val gameEnd = assertIs<PartyGameUiState.GameEnd>(viewModel.uiState.value)
        assertNull(gameEnd.winner)
        assertEquals(listOf(0, 0), gameEnd.standings.map { it.place })
    }

    @Test
    fun `play again starts over at round one with scores reset`() = runPartyTest { viewModel ->
        viewModel.act(PartyGameAction.StartTurn)
        viewModel.act(PartyGameAction.Answer(correct = true))
        clock.advanceBy(30_000)
        advanceTimeBy(200)
        dispatcher.scheduler.runCurrent()
        viewModel.act(PartyGameAction.PlayAgain)

        val handoff = assertIs<PartyGameUiState.Handoff>(viewModel.uiState.value)
        assertEquals(1, handoff.round)
        assertEquals(listOf(0, 0), handoff.scoreboard.map { it.score })
    }

    @Test
    fun `exit to menu emits an effect`() = runPartyTest { viewModel ->
        viewModel.effects.test {
            viewModel.onAction(PartyGameAction.ExitToMenu)
            assertEquals(PartyGameEffect.ExitToMenu, awaitItem())
        }
    }
}
