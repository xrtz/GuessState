package ru.xrtz.whichstate.core.map.asset

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import ru.xrtz.whichstate.core.domain.model.UsState
import ru.xrtz.whichstate.core.map.geometry.MapGeometry
import ru.xrtz.whichstate.core.map.geometry.StateGeometry

internal fun MapAssetJson.toGeometry(): MapGeometry =
    MapGeometry(width = width, height = height, states = states.map { it.toGeometry() })

internal fun StateGeometryJson.toGeometry(): StateGeometry = StateGeometry(
    code = code,
    name = name,
    usState = runCatching { UsState.valueOf(code) }.getOrNull(),

    rings = rings.flatten().map { ring -> ring.map { (x, y) -> Offset(x, y) } },
    bbox = Rect(left = bbox[0], top = bbox[1], right = bbox[2], bottom = bbox[3]),
    label = Offset(label[0], label[1]),
)
