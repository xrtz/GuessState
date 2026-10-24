package ru.xrtz.whichstate.core.domain.game

import ru.xrtz.whichstate.core.domain.game.PartyGameState.Phase
import ru.xrtz.whichstate.core.domain.model.City
import ru.xrtz.whichstate.core.domain.model.CityId
import ru.xrtz.whichstate.core.domain.model.Level
import ru.xrtz.whichstate.core.domain.model.PartyMode
import ru.xrtz.whichstate.core.domain.model.PartyTeam
import ru.xrtz.whichstate.core.domain.model.UsState
import ru.xrtz.whichstate.core.domain.policy.PartyScoring
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertSame

class PartyGameReducerTest {
    private val rules = PartyRules()
    private val reducer = PartyGameReducer(rules, PartyScoring())

    private fun city(i: Int) = City(CityId("c$i"), "City $i", UsState.TX, population = 1_000, minLevel = Level.ROOKIE)

    private fun game(teamCount: Int = 2, penaltyOnSkip: Boolean = false, citiesPerTurn: Int = 3): PartyGameState {
        val turnCount = teamCount * rules.totalRounds
        return PartyGameState(
            mode = PartyMode.GUESS_STATE,
            penaltyOnSkip = penaltyOnSkip,
            teams = (1..teamCount).map { PartyTeam(it, customName = null) },
            turns = (0 until turnCount).map { t -> (0 until citiesPerTurn).map { city(t * 100 + it) } },
            turnIndex = 0,
            phase = Phase.Handoff,
            turnResults = emptyList(),
        )
    }

    private fun PartyGameState.reduce(vararg events: PartyEvent) = events.fold(this) { s, e -> reducer.reduce(s, e) }

    @Test
    fun `start turn begins playing the first card`() {
        val state = game().reduce(PartyEvent.StartTurn(nowMs = 1_000))

        val playing = assertIs<Phase.Playing>(state.phase)
        assertEquals(0, playing.cityIndex)
        assertEquals(1_000, playing.startedAtMs)
    }

    @Test
    fun `correct adds a point to the current team only and advances the card`() {
        val state = game().reduce(PartyEvent.StartTurn(0), PartyEvent.Answer(correct = true, nowMs = 100))

        assertEquals(listOf(1, 0), state.teams.map { it.score })
        assertEquals(1, assertIs<Phase.Playing>(state.phase).cityIndex)
        assertEquals(1, state.turnResults.size)
    }

    @Test
    fun `skip scores zero without penalty and minus one with it`() {
        val noPenalty = game(penaltyOnSkip = false).reduce(PartyEvent.StartTurn(0), PartyEvent.Answer(false, 100))
        val withPenalty = game(penaltyOnSkip = true).reduce(PartyEvent.StartTurn(0), PartyEvent.Answer(false, 100))

        assertEquals(0, noPenalty.teams[0].score)
        assertEquals(-1, withPenalty.teams[0].score)
    }

    @Test
    fun `answering the last card of the pack ends the round`() {
        val state = game(citiesPerTurn = 2).reduce(
            PartyEvent.StartTurn(0),
            PartyEvent.Answer(true, 100),
            PartyEvent.Answer(true, 200),
        )

        assertEquals(Phase.RoundEnd, state.phase)
        assertEquals(2, state.turnResults.size)
    }

    @Test
    fun `tick past 30 seconds ends the round`() {
        val playing = game().reduce(PartyEvent.StartTurn(0))

        assertSame(playing.phase, reducer.reduce(playing, PartyEvent.Tick(29_999)).phase)
        assertEquals(Phase.RoundEnd, reducer.reduce(playing, PartyEvent.Tick(30_000)).phase)
    }

    @Test
    fun `an answer arriving after time is up ends the round without scoring`() {
        val state = game().reduce(PartyEvent.StartTurn(0), PartyEvent.Answer(true, 30_500))

        assertEquals(Phase.RoundEnd, state.phase)
        assertEquals(0, state.teams[0].score)
        assertEquals(0, state.turnResults.size)
    }

    @Test
    fun `pause freezes the clock and ignores answers until resumed`() {
        val paused = game().reduce(PartyEvent.StartTurn(0), PartyEvent.Pause(10_000))

        val ignored = paused.reduce(PartyEvent.Answer(true, 50_000), PartyEvent.Tick(50_000))
        assertEquals(0, ignored.teams[0].score)
        assertIs<Phase.Playing>(ignored.phase)

        val resumed = paused.reduce(PartyEvent.Resume(60_000), PartyEvent.Tick(75_000))
        val playing = assertIs<Phase.Playing>(resumed.phase)
        assertEquals(25_000, playing.elapsedMs(75_000))
        assertEquals(Phase.RoundEnd, reducer.reduce(resumed, PartyEvent.Tick(80_000)).phase)
    }

    @Test
    fun `next turn rotates teams and counts rounds`() {
        var state = game(teamCount = 2)
        state = state.reduce(PartyEvent.StartTurn(0), PartyEvent.Tick(30_000), PartyEvent.NextTurn)

        assertEquals(Phase.Handoff, state.phase)
        assertEquals(2, state.currentTeam.number)
        assertEquals(1, state.currentRound)

        state = state.reduce(PartyEvent.StartTurn(0), PartyEvent.Tick(30_000), PartyEvent.NextTurn)
        assertEquals(1, state.currentTeam.number)
        assertEquals(2, state.currentRound)
    }

    @Test
    fun `the last turn's next goes to game end after every team played every round`() {
        var state = game(teamCount = 3)
        repeat(3 * rules.totalRounds) {
            state = state.reduce(PartyEvent.StartTurn(0), PartyEvent.Tick(30_000), PartyEvent.NextTurn)
        }

        assertEquals(Phase.GameEnd, state.phase)
    }

    @Test
    fun `a new turn starts with an empty results list`() {
        val state = game().reduce(
            PartyEvent.StartTurn(0),
            PartyEvent.Answer(true, 100),
            PartyEvent.Tick(30_000),
            PartyEvent.NextTurn,
            PartyEvent.StartTurn(40_000),
        )

        assertEquals(0, state.turnResults.size)
    }

    @Test
    fun `events for other phases are ignored`() {
        val handoff = game()
        assertSame(handoff, reducer.reduce(handoff, PartyEvent.Answer(true, 0)))
        assertSame(handoff, reducer.reduce(handoff, PartyEvent.NextTurn))

        val playing = handoff.reduce(PartyEvent.StartTurn(0))
        assertSame(playing, reducer.reduce(playing, PartyEvent.StartTurn(5)))
    }

    @Test
    fun `teams with identical names still score separately`() {
        val base = game()
        val sameNames = base.copy(teams = base.teams.map { it.copy(customName = "Cats") })
        val state = sameNames.reduce(PartyEvent.StartTurn(0), PartyEvent.Answer(true, 100))

        assertEquals(listOf(1, 0), state.teams.map { it.score })
    }
}
