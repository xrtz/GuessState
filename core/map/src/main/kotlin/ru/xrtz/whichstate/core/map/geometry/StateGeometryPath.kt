package ru.xrtz.whichstate.core.map.geometry

import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathFillType

fun StateGeometry.toPath(): Path = Path().apply {
    fillType = PathFillType.EvenOdd
    rings.forEach { ring ->
        if (ring.isEmpty()) return@forEach
        moveTo(ring.first().x, ring.first().y)
        for (i in 1 until ring.size) {
            lineTo(ring[i].x, ring[i].y)
        }
        close()
    }
}
