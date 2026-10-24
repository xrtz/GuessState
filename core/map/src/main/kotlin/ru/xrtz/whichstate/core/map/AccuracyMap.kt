package ru.xrtz.whichstate.core.map

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.unit.dp
import ru.xrtz.whichstate.core.designsystem.theme.AtlasColors
import ru.xrtz.whichstate.core.domain.model.UsState

private val STROKE_WIDTH_DP = 0.7.dp
private val ACCURACY_STROKE = AtlasColors.ink.copy(alpha = 0.4f)

@Composable
fun AccuracyMap(accuracy: Map<UsState, Float>, modifier: Modifier = Modifier) {
    val loaded = rememberMapGeometry()

    if (loaded != null) {
        Canvas(modifier = modifier.fillMaxSize()) {
            val transform = MapTransform(size, loaded.geometry, zoom = 1f, pan = Offset.Zero)
            withTransform({
                translate(transform.origin.x, transform.origin.y)
                scale(transform.totalScale, transform.totalScale, pivot = Offset.Zero)
            }) {
                val strokeWidth = STROKE_WIDTH_DP.toPx() / transform.totalScale
                loaded.geometry.states.forEach { state ->
                    val path = loaded.paths[state.code] ?: return@forEach
                    val ratio = state.usState?.let { accuracy[it] }

                    if (ratio == null) {
                        drawPath(path, color = AtlasColors.mapFill)
                    } else {
                        clipPath(path) {
                            val splitY = state.bbox.top + state.bbox.height * ratio
                            drawRect(
                                color = AtlasColors.green,
                                topLeft = Offset(state.bbox.left, state.bbox.top),
                                size = Size(state.bbox.width, splitY - state.bbox.top),
                            )
                            drawRect(
                                color = AtlasColors.red,
                                topLeft = Offset(state.bbox.left, splitY),
                                size = Size(state.bbox.width, state.bbox.bottom - splitY),
                            )
                        }
                    }
                    drawPath(path, color = ACCURACY_STROKE, style = Stroke(width = strokeWidth))
                }
            }
        }
    }
}
