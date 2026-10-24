package ru.xrtz.whichstate.core.domain.model

data class CityId(val value: String)

data class City(
    val id: CityId,
    val name: String,
    val state: UsState,
    val population: Int,
    val minLevel: Level,
)
