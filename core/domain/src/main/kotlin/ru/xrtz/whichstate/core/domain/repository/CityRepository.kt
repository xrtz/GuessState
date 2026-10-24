package ru.xrtz.whichstate.core.domain.repository

import ru.xrtz.whichstate.core.domain.model.City

interface CityRepository {
    suspend fun cities(): List<City>
}
