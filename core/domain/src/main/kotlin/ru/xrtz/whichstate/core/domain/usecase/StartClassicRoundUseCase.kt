package ru.xrtz.whichstate.core.domain.usecase

import ru.xrtz.whichstate.core.domain.model.Level
import ru.xrtz.whichstate.core.domain.model.Question
import ru.xrtz.whichstate.core.domain.repository.CityRepository
import javax.inject.Inject
import kotlin.random.Random

class StartClassicRoundUseCase @Inject constructor(
    private val cityRepository: CityRepository,
    private val random: Random,
) {
    suspend operator fun invoke(level: Level): List<Question> {
        val pool = cityRepository.cities().filter { it.minLevel <= level }
        val acceptedStatesByName = pool.groupBy { it.name }
            .mapValues { (_, cities) -> cities.map { it.state }.toSet() }

        return pool.shuffled(random).map { city -> Question(city, acceptedStatesByName.getValue(city.name)) }
    }
}
