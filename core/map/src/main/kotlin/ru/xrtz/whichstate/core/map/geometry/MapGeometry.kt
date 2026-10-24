package ru.xrtz.whichstate.core.map.geometry

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import ru.xrtz.whichstate.core.domain.model.UsState

data class MapGeometry(val width: Float, val height: Float, val states: List<StateGeometry>)

data class StateGeometry(
    val code: String,
    val name: String,
    val usState: UsState?,
    val rings: List<List<Offset>>,
    val bbox: Rect,
    val label: Offset,
)
