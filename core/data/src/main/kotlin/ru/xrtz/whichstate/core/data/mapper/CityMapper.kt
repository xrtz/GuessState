package ru.xrtz.whichstate.core.data.mapper

import ru.xrtz.whichstate.core.data.asset.dto.CityDto
import ru.xrtz.whichstate.core.domain.model.City
import ru.xrtz.whichstate.core.domain.model.CityId
import ru.xrtz.whichstate.core.domain.model.Level
import ru.xrtz.whichstate.core.domain.model.UsState

internal fun CityDto.toDomain(): City = City(
    id = CityId(id),
    name = name,
    state = UsState.valueOf(state),
    population = population,
    minLevel = Level.entries[minLevel - 1],
)
