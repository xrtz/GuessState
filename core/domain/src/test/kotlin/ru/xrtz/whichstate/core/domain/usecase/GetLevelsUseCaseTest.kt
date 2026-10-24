package ru.xrtz.whichstate.core.domain.usecase

import kotlinx.coroutines.runBlocking
import ru.xrtz.whichstate.core.domain.model.City
import ru.xrtz.whichstate.core.domain.model.CityId
import ru.xrtz.whichstate.core.domain.model.Level
import ru.xrtz.whichstate.core.domain.model.UsState
import ru.xrtz.whichstate.core.domain.repository.CityRepository
import kotlin.test.Test
import kotlin.test.assertEquals

class GetLevelsUseCaseTest {
    private fun city(name: String, minLevel: Level) =
        City(CityId(name), name, UsState.TX, population = 100_000, minLevel = minLevel)

    private class FakeCityRepository(private val cities: List<City>) : CityRepository {
        override suspend fun cities(): List<City> = cities
    }

    @Test
    fun `each level's city count includes every lower level's cities`() = runBlocking {
        val cities = listOf(
            city("Rookie City", Level.ROOKIE),
            city("Explorer City 1", Level.EXPLORER),
            city("Explorer City 2", Level.EXPLORER),
            city("Master City", Level.MASTER),
        )
        val useCase = GetLevelsUseCase(FakeCityRepository(cities))

        val levels = useCase().associateBy { it.level }

        assertEquals(1, levels.getValue(Level.ROOKIE).cityCount)
        assertEquals(3, levels.getValue(Level.EXPLORER).cityCount)
        assertEquals(4, levels.getValue(Level.MASTER).cityCount)
    }

    @Test
    fun `returns all three levels even with an empty city list`() = runBlocking {
        val useCase = GetLevelsUseCase(FakeCityRepository(emptyList()))

        val levels = useCase()

        assertEquals(setOf(Level.ROOKIE, Level.EXPLORER, Level.MASTER), levels.map { it.level }.toSet())
        assertEquals(0, levels.sumOf { it.cityCount })
    }
}
