package ru.xrtz.whichstate.core.map

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.CustomAccessibilityAction
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.customActions
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import ru.xrtz.whichstate.core.designsystem.components.StateTileStatus
import ru.xrtz.whichstate.core.designsystem.theme.AtlasColors
import ru.xrtz.whichstate.core.domain.model.UsState
import ru.xrtz.whichstate.core.map.geometry.MapGeometry
import ru.xrtz.whichstate.core.map.hittest.StateHitTester

private const val MIN_ZOOM = 1f
private const val MAX_ZOOM = 8f
private val TAP_TOLERANCE_DP = 12.dp
private val STROKE_WIDTH_DP = 1.dp

@Composable
fun UsMap(
    onStateTapped: (UsState) -> Unit,
    modifier: Modifier = Modifier,
    highlight: MapHighlight? = null,
) {
    val loaded = rememberMapGeometry()
    var zoom by remember { mutableFloatStateOf(MIN_ZOOM) }
    var pan by remember { mutableStateOf(Offset.Zero) }
    val density = LocalDensity.current
    val tapToleranceSourceUnits = with(density) { TAP_TOLERANCE_DP.toPx() }
    val strokeWidthSourceUnits = with(density) { STROKE_WIDTH_DP.toPx() }
    val mapDescription = stringResource(R.string.map_content_description)

    Box(
        modifier = modifier
            .fillMaxSize()
            .mapAccessibilityActions(mapDescription, enabled = highlight == null, onStateTapped)
            .then(
                if (loaded == null) {
                    Modifier
                } else {
                    Modifier
                        .pointerInput(loaded.geometry) {
                            detectTransformGestures { _, panDelta, zoomDelta, _ ->
                                zoom = (zoom * zoomDelta).coerceIn(MIN_ZOOM, MAX_ZOOM)
                                pan += panDelta
                            }
                        }
                        .pointerInput(loaded.geometry, highlight) {
                            detectTapGestures { tapOffset ->
                                if (highlight != null) return@detectTapGestures
                                val viewSize = Size(size.width.toFloat(), size.height.toFloat())
                                val transform = MapTransform(viewSize, loaded.geometry, zoom, pan)
                                val sourcePoint = transform.toSource(tapOffset)
                                val tolerance = tapToleranceSourceUnits / transform.totalScale
                                StateHitTester
                                    .hitTest(sourcePoint, loaded.geometry.states, tolerance)
                                    ?.usState
                                    ?.let(onStateTapped)
                            }
                        }
                },
            ),
    ) {
        if (loaded != null) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val transform = MapTransform(size, loaded.geometry, zoom, pan)
                withTransform({
                    translate(transform.origin.x, transform.origin.y)
                    scale(transform.totalScale, transform.totalScale, pivot = Offset.Zero)
                }) {
                    val strokeWidth = strokeWidthSourceUnits / transform.totalScale
                    loaded.geometry.states.forEach { state ->
                        val path = loaded.paths[state.code] ?: return@forEach
                        val status = state.usState?.let { highlight?.statusOf(it) } ?: StateTileStatus.DEFAULT
                        drawPath(path, color = status.toFillColor())
                        drawPath(path, color = AtlasColors.mapStroke, style = Stroke(width = strokeWidth))
                    }
                }
            }
        }
    }
}

private fun Modifier.mapAccessibilityActions(
    description: String,
    enabled: Boolean,
    onStateTapped: (UsState) -> Unit,
): Modifier = if (!enabled) {
    this
} else {
    semantics {
        contentDescription = description
        customActions = UsState.entries.map { state ->
            CustomAccessibilityAction(state.fullName) {
                onStateTapped(state)
                true
            }
        }
    }
}

private fun StateTileStatus.toFillColor(): Color = when (this) {
    StateTileStatus.DEFAULT -> AtlasColors.mapFill
    StateTileStatus.CORRECT -> AtlasColors.green
    StateTileStatus.WRONG -> AtlasColors.red
    StateTileStatus.NEAR_MISS -> AtlasColors.cyan
}

internal class MapTransform(viewSize: Size, geometry: MapGeometry, zoom: Float, pan: Offset) {
    private val fitScale = minOf(viewSize.width / geometry.width, viewSize.height / geometry.height)
    val totalScale = fitScale * zoom
    private val geometryCenter = Offset(geometry.width / 2f, geometry.height / 2f)
    private val viewCenter = Offset(viewSize.width / 2f, viewSize.height / 2f)

    val origin: Offset = viewCenter + pan - geometryCenter * totalScale

    fun toSource(viewPoint: Offset): Offset = (viewPoint - origin) / totalScale
}
