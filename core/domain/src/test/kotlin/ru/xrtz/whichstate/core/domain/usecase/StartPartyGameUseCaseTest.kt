package ru.xrtz.whichstate.core.domain.usecase

import kotlinx.coroutines.runBlocking
import ru.xrtz.whichstate.core.domain.game.PartyGameState
import ru.xrtz.whichstate.core.domain.model.City
import ru.xrtz.whichstate.core.domain.model.CityId
import ru.xrtz.whichstate.core.domain.model.Level
import ru.xrtz.whichstate.core.domain.model.PartyMode
import ru.xrtz.whichstate.core.domain.model.UsState
import ru.xrtz.whichstate.core.domain.repository.CityRepository
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull

class StartPartyGameUseCaseTest {
    private val cities = (1..100).map { City(CityId("c$it"), "City $it", UsState.TX, it, Level.MASTER) }
    private val useCase = StartPartyGameUseCase(
        object : CityRepository {
            override suspend fun cities() = cities
        },
        Random(7),
    )

    @Test
    fun `builds one pack of 7 per team per round with no repeated city`() = runBlocking {
        val state = useCase(PartyMode.DESCRIBE, listOf("A", "B", "C", "D"), penaltyOnSkip = true)

        assertEquals(12, state.turns.size)
        state.turns.forEach { assertEquals(7, it.size) }
        assertEquals(84, state.turns.flatten().map { it.id }.toSet().size)
        assertEquals(PartyGameState.Phase.Handoff, state.phase)
        assertEquals(3, state.totalRounds)
    }

    @Test
    fun `blank names fall back to the default label and long names are cut to 20`() = runBlocking {
        val state = useCase(PartyMode.GUESS_STATE, listOf("   ", "  A very very long team name  "), false)

        assertNull(state.teams[0].customName)
        assertEquals("A very very long tea", state.teams[1].customName)
        assertEquals(listOf(1, 2), state.teams.map { it.number })
    }

    @Test
    fun `rejects team counts outside 2 to 4`() {
        assertFailsWith<IllegalArgumentException> {
            runBlocking { useCase(PartyMode.DESCRIBE, listOf("Solo"), false) }
        }
    }
}
