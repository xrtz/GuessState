package ru.xrtz.whichstate.feature.menu

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import kotlin.math.cos
import kotlin.math.sin
import ru.xrtz.whichstate.core.designsystem.theme.AtlasColors
import ru.xrtz.whichstate.core.designsystem.theme.AtlasShapes

@Composable
internal fun HistoryButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    val description = stringResource(R.string.menu_open_history)
    Box(
        modifier = modifier
            .clickable(onClick = onClick)
            .minimumInteractiveComponentSize()
            .size(40.dp)
            .clip(AtlasShapes.corner)
            .background(AtlasColors.card)
            .border(1.dp, AtlasColors.hairline, AtlasShapes.corner)
            .semantics { contentDescription = description },
        contentAlignment = Alignment.Center,
    ) {
        HistoryIcon(color = AtlasColors.ink, modifier = Modifier.size(16.dp))
    }
}

@Composable
private fun HistoryIcon(color: Color, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val stroke = 1.6.dp.toPx()
        val radius = (size.minDimension - stroke) / 2f
        val center = Offset(size.width / 2f, size.height / 2f)
        val topLeft = Offset(center.x - radius, center.y - radius)

        drawArc(
            color = color,
            startAngle = 55f,
            sweepAngle = 250f,
            useCenter = false,
            topLeft = topLeft,
            size = Size(radius * 2f, radius * 2f),
            style = Stroke(width = stroke, cap = StrokeCap.Round),
        )

        val endAngleRad = Math.toRadians(55.0 + 250.0)
        val tip = Offset(
            center.x + radius * cos(endAngleRad).toFloat(),
            center.y + radius * sin(endAngleRad).toFloat(),
        )
        val chevron = radius * 0.55f
        drawLine(color, tip, Offset(tip.x - chevron, tip.y - chevron * 0.35f), stroke, StrokeCap.Round)
        drawLine(color, tip, Offset(tip.x - chevron * 0.2f, tip.y + chevron * 0.6f), stroke, StrokeCap.Round)

        drawLine(color, center, Offset(center.x, center.y - radius * 0.55f), stroke, StrokeCap.Round)
        drawLine(color, center, Offset(center.x + radius * 0.4f, center.y), stroke, StrokeCap.Round)
    }
}
