package ru.xrtz.whichstate.core.map.asset

import kotlinx.serialization.Serializable

@Serializable
internal data class MapAssetJson(
    val width: Float,
    val height: Float,
    val states: List<StateGeometryJson>,
)

@Serializable
internal data class StateGeometryJson(
    val code: String,
    val name: String,
    val rings: List<List<List<List<Float>>>>,
    val bbox: List<Float>,
    val label: List<Float>,
)
