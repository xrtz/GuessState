package ru.xrtz.whichstate.core.domain.usecase

import ru.xrtz.whichstate.core.domain.model.Level
import ru.xrtz.whichstate.core.domain.model.LevelInfo
import ru.xrtz.whichstate.core.domain.repository.CityRepository
import javax.inject.Inject

class GetLevelsUseCase @Inject constructor(private val cityRepository: CityRepository) {
    suspend operator fun invoke(): List<LevelInfo> {
        val cities = cityRepository.cities()
        return Level.entries.map { level -> LevelInfo(level, cities.count { it.minLevel <= level }) }
    }
}
