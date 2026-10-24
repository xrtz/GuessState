package ru.xrtz.whichstate.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import ru.xrtz.whichstate.core.designsystem.theme.AtlasColors
import ru.xrtz.whichstate.core.designsystem.theme.AtlasShapes
import ru.xrtz.whichstate.core.designsystem.theme.AtlasTheme
import ru.xrtz.whichstate.core.designsystem.theme.AtlasType

@Composable
fun SwipeCard(
    cityName: String,
    stateText: String,
    modifier: Modifier = Modifier,
    isStateHint: Boolean = false,
    offsetX: Dp = 0.dp,
    labelThreshold: Dp = 12.dp,
    correctLabel: String = "✓ Correct!",
    skipLabel: String = "✗ Skip",
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .graphicsLayer {
                translationX = offsetX.toPx()
                rotationZ = offsetX.value * 0.04f
            }
            .clip(AtlasShapes.corner)
            .background(AtlasColors.card)
            .border(1.dp, AtlasColors.hairline, AtlasShapes.corner)
            .padding(vertical = 38.dp, horizontal = 24.dp),
        contentAlignment = Alignment.Center,
    ) {
        androidx.compose.foundation.layout.Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = cityName.uppercase(),
                style = AtlasType.display(size = 34.sp, letterSpacing = (-0.02).em, lineHeight = 36.sp),
                color = AtlasColors.ink,
            )
            if (isStateHint) {
                Text(
                    text = stateText.uppercase(),
                    style = AtlasType.mono(size = 11.sp, letterSpacing = 0.1.em),
                    color = AtlasColors.muted.copy(alpha = 0.7f),
                    modifier = Modifier.padding(top = 12.dp),
                )
            } else {
                Text(
                    text = stateText.uppercase(),
                    style = AtlasType.mono(size = 20.sp, letterSpacing = 0.06.em),
                    color = AtlasColors.cyan,
                    modifier = Modifier.padding(top = 12.dp),
                )
            }
        }

        if (offsetX > labelThreshold) {
            SwipeLabel(text = correctLabel, color = AtlasColors.green, alignment = Alignment.CenterEnd)
        } else if (offsetX < -labelThreshold) {
            SwipeLabel(text = skipLabel, color = AtlasColors.red, alignment = Alignment.CenterStart)
        }
    }
}

@Composable
private fun SwipeLabel(text: String, color: Color, alignment: Alignment) {
    Box(Modifier.fillMaxWidth().padding(horizontal = 12.dp), contentAlignment = alignment) {
        Text(
            text = text.uppercase(),
            style = AtlasType.mono(size = 12.5.sp, weight = FontWeight.W500, letterSpacing = 0.08.em),
            color = color,
            modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .background(color.copy(alpha = 0.1f))
                .border(1.dp, color, RoundedCornerShape(4.dp))
                .padding(vertical = 4.dp, horizontal = 11.dp),
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF3F1EA, widthDp = 360)
@Composable
private fun SwipeCardPreview() {
    AtlasTheme {
        androidx.compose.foundation.layout.Column(
            verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(16.dp),
        ) {
            SwipeCard(cityName = "Springfield", stateText = "MO", isStateHint = false)
            SwipeCard(cityName = "Springfield", stateText = "Describe — no saying the city name!", isStateHint = true)
            SwipeCard(cityName = "Springfield", stateText = "MO", offsetX = 40.dp)
            SwipeCard(cityName = "Springfield", stateText = "MO", offsetX = (-40).dp)
        }
    }
}
