package ru.xrtz.whichstate.core.data.asset.dto

import kotlinx.serialization.Serializable

@Serializable
internal data class CityDto(
    val id: String,
    val name: String,
    val state: String,
    val population: Int,
    val rank: Int,
    val minLevel: Int,
)
