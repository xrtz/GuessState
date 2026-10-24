package ru.xrtz.whichstate.core.testing.fake

import ru.xrtz.whichstate.core.domain.model.City
import ru.xrtz.whichstate.core.domain.repository.CityRepository

class FakeCityRepository(private val cities: List<City> = emptyList()) : CityRepository {
    override suspend fun cities(): List<City> = cities
}
