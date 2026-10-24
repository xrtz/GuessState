package ru.xrtz.whichstate.core.domain.usecase

import kotlinx.coroutines.runBlocking
import ru.xrtz.whichstate.core.domain.model.City
import ru.xrtz.whichstate.core.domain.model.CityId
import ru.xrtz.whichstate.core.domain.model.Level
import ru.xrtz.whichstate.core.domain.model.UsState
import ru.xrtz.whichstate.core.domain.repository.CityRepository
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class StartClassicRoundUseCaseTest {
    private val fixturePool = listOf(
        City(CityId("new-york-ny"), "New York", UsState.NY, population = 8_804_190, minLevel = Level.ROOKIE),
        City(CityId("columbus-oh"), "Columbus", UsState.OH, population = 905_748, minLevel = Level.ROOKIE),
        City(CityId("columbus-ga"), "Columbus", UsState.GA, population = 206_922, minLevel = Level.MASTER),
        City(CityId("springfield-mo"), "Springfield", UsState.MO, population = 169_176, minLevel = Level.MASTER),
        City(CityId("springfield-ma"), "Springfield", UsState.MA, population = 155_929, minLevel = Level.MASTER),
    )

    private class FakeCityRepository(private val cities: List<City>) : CityRepository {
        override suspend fun cities(): List<City> = cities
    }

    private fun useCase(cities: List<City> = fixturePool, random: Random = Random(seed = 42)) =
        StartClassicRoundUseCase(FakeCityRepository(cities), random)

    @Test
    fun `at Master level a same-named city accepts every matching state in the pool`() = runBlocking {
        val questions = useCase()(Level.MASTER)

        val springfield = questions.single { it.city.name == "Springfield" && it.city.state == UsState.MO }
        assertEquals(setOf(UsState.MO, UsState.MA), springfield.acceptedStates)
    }

    @Test
    fun `at Rookie level a same-named city not yet unlocked elsewhere accepts only its own state`() = runBlocking {
        val questions = useCase()(Level.ROOKIE)

        val columbus = questions.single { it.city.name == "Columbus" }
        assertEquals(UsState.OH, columbus.city.state)
        assertEquals(setOf(UsState.OH), columbus.acceptedStates)
    }

    @Test
    fun `the pool is filtered to cities unlocked at or below the requested level`() = runBlocking {
        val questions = useCase()(Level.ROOKIE)

        assertEquals(setOf("New York", "Columbus"), questions.map { it.city.name }.toSet())
    }

    @Test
    fun `Master level includes the full pool`() = runBlocking {
        val questions = useCase()(Level.MASTER)

        assertEquals(fixturePool.size, questions.size)
    }

    @Test
    fun `the returned pool is shuffled using the injected Random, not a fixed order`() = runBlocking {
        val a = useCase(random = Random(seed = 1))(Level.MASTER).map { it.city.id }
        val b = useCase(random = Random(seed = 2))(Level.MASTER).map { it.city.id }

        assertEquals(a.toSet(), b.toSet())
        assertTrue(a != b)
    }
}
