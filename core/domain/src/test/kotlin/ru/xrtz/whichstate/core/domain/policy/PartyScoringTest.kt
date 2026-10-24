package ru.xrtz.whichstate.core.domain.policy

import ru.xrtz.whichstate.core.domain.model.City
import ru.xrtz.whichstate.core.domain.model.CityId
import ru.xrtz.whichstate.core.domain.model.Level
import ru.xrtz.whichstate.core.domain.model.PartyCardResult
import ru.xrtz.whichstate.core.domain.model.PartyTeam
import ru.xrtz.whichstate.core.domain.model.UsState
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class PartyScoringTest {
    private val scoring = PartyScoring()
    private val city = City(CityId("a"), "Austin", UsState.TX, 1, Level.ROOKIE)

    @Test
    fun `turn points match the site's correct minus skipped-if-penalty`() {
        val results = listOf(true, true, false, true, false).map { PartyCardResult(city, it) }

        assertEquals(3, scoring.turnPoints(results, penaltyOnSkip = false))
        assertEquals(1, scoring.turnPoints(results, penaltyOnSkip = true))
    }

    @Test
    fun `standings sort by score and keep team order on ties`() {
        val teams = listOf(PartyTeam(1, null, 2), PartyTeam(2, null, 5), PartyTeam(3, null, 2))

        assertEquals(listOf(2, 1, 3), scoring.standings(teams).map { it.number })
    }

    @Test
    fun `tie only when the top two scores are equal`() {
        assertTrue(scoring.isTie(listOf(PartyTeam(1, null, 4), PartyTeam(2, null, 4), PartyTeam(3, null, 1))))
        assertFalse(scoring.isTie(listOf(PartyTeam(1, null, 4), PartyTeam(2, null, 3), PartyTeam(3, null, 3))))
    }
}
