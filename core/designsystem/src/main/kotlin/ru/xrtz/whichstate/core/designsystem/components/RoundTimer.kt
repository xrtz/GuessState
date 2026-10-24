package ru.xrtz.whichstate.core.designsystem.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import ru.xrtz.whichstate.core.designsystem.theme.AtlasColors
import ru.xrtz.whichstate.core.designsystem.theme.AtlasTheme
import ru.xrtz.whichstate.core.designsystem.theme.AtlasType

@Composable
fun RoundTimer(
    secondsLeft: Int,
    isWarning: Boolean,
    modifier: Modifier = Modifier,
    fontSize: TextUnit = 18.sp,
) {
    val blinkAlpha = if (isWarning) {
        val transition = rememberInfiniteTransition(label = "timer-blink")
        val alpha by transition.animateFloat(
            initialValue = 1f,
            targetValue = 0.4f,
            animationSpec = infiniteRepeatable(tween(550, easing = FastOutSlowInEasing), RepeatMode.Reverse),
            label = "timer-blink-alpha",
        )
        alpha
    } else {
        1f
    }
    Text(
        text = "${secondsLeft}s",
        style = AtlasType.mono(size = fontSize, weight = FontWeight.W500, letterSpacing = 0.02.em),
        color = if (isWarning) AtlasColors.red else AtlasColors.cyan,
        textAlign = TextAlign.End,
        modifier = modifier
            .defaultMinSize(minWidth = 46.dp)
            .alpha(blinkAlpha),
    )
}

@Preview(showBackground = true, backgroundColor = 0xFFFBFAF6)
@Composable
private fun RoundTimerPreview() {
    AtlasTheme {
        Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
            RoundTimer(secondsLeft = 24, isWarning = false)
            RoundTimer(secondsLeft = 7, isWarning = true)
        }
    }
}
