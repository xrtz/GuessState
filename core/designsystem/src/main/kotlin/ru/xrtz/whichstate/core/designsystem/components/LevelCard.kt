package ru.xrtz.whichstate.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import ru.xrtz.whichstate.core.designsystem.theme.AtlasColors
import ru.xrtz.whichstate.core.designsystem.theme.AtlasShapes
import ru.xrtz.whichstate.core.designsystem.theme.AtlasTheme
import ru.xrtz.whichstate.core.designsystem.theme.AtlasType

@Composable
fun LevelCard(
    rank: Int,
    name: String,
    meta: String,
    difficulty: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    difficultyMax: Int = 3,
) {
    Column(
        modifier = modifier
            .clip(AtlasShapes.corner)
            .background(AtlasColors.card)
            .border(1.dp, AtlasColors.hairline, AtlasShapes.corner)
            .clickable(onClick = onClick)
            .defaultMinSize(minHeight = 116.dp)
            .padding(horizontal = 18.dp, vertical = 20.dp),
    ) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(
                text = "0$rank",
                style = AtlasType.mono(size = 11.5.sp, letterSpacing = 0.18.em),
                color = AtlasColors.muted,
            )
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                repeat(difficultyMax) { i -> DifficultyDot(filled = i < difficulty) }
            }
        }
        Spacer(Modifier.weight(1f))
        Text(
            text = name.uppercase(),
            style = AtlasType.display(size = 20.sp, letterSpacing = (-0.01).em, lineHeight = 22.sp),
            color = AtlasColors.ink,
        )
        Text(
            text = meta,
            style = AtlasType.mono(size = 11.5.sp, letterSpacing = 0.08.em),
            color = AtlasColors.muted,
            modifier = Modifier.padding(top = 6.dp),
        )
    }
}

@Composable
private fun DifficultyDot(filled: Boolean) {
    Box(
        Modifier
            .size(7.dp)
            .clip(CircleShape)
            .background(if (filled) AtlasColors.cyan else androidx.compose.ui.graphics.Color.Transparent)
            .border(1.dp, if (filled) AtlasColors.cyan else AtlasColors.muted, CircleShape),
    )
}

@Preview(showBackground = true, backgroundColor = 0xFFF3F1EA, widthDp = 200)
@Composable
private fun LevelCardPreview() {
    AtlasTheme {
        LevelCard(rank = 1, name = "Rookie", meta = "50 cities · 30 sec", difficulty = 1, onClick = {})
    }
}
