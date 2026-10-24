package ru.xrtz.whichstate.feature.party.setup

import app.cash.turbine.test
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import ru.xrtz.whichstate.core.domain.model.PartyMode
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class PartySetupViewModelTest {
    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `defaults match the site - two teams, no penalty, 3 rounds of 7 cities in 30 sec`() {
        val state = PartySetupViewModel(PartyMode.DESCRIBE).uiState.value

        assertEquals(listOf(2, 3, 4), state.teamCountOptions)
        assertEquals(2, state.teamCount)
        assertEquals(false, state.penaltyOnSkip)
        assertEquals(Triple(3, 7, 30), Triple(state.roundCount, state.citiesPerRound, state.secondsPerRound))
    }

    @Test
    fun `team names are capped at 20 characters`() {
        val viewModel = PartySetupViewModel(PartyMode.DESCRIBE)

        viewModel.onAction(PartySetupAction.ChangeTeamName(1, "x".repeat(25)))

        assertEquals(20, viewModel.uiState.value.teamNames[1].length)
    }

    @Test
    fun `start sends only the selected number of names, keeping hidden ones for later`() = runTest {
        val viewModel = PartySetupViewModel(PartyMode.GUESS_STATE)
        viewModel.onAction(PartySetupAction.SelectTeamCount(3))
        viewModel.onAction(PartySetupAction.ChangeTeamName(2, "Owls"))
        viewModel.onAction(PartySetupAction.SelectTeamCount(2))
        viewModel.onAction(PartySetupAction.SetPenaltyOnSkip(true))

        assertEquals("Owls", viewModel.uiState.value.teamNames[2])
        viewModel.effects.test {
            viewModel.onAction(PartySetupAction.Start)
            assertEquals(PartySetupEffect.StartGame(PartyMode.GUESS_STATE, listOf("", ""), true), awaitItem())
        }
    }
}
