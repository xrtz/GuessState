package ru.xrtz.whichstate.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import ru.xrtz.whichstate.core.designsystem.theme.AtlasColors
import ru.xrtz.whichstate.core.designsystem.theme.AtlasShapes
import ru.xrtz.whichstate.core.designsystem.theme.AtlasTheme
import ru.xrtz.whichstate.core.designsystem.theme.AtlasType

enum class StateTileStatus { DEFAULT, CORRECT, WRONG, NEAR_MISS }

private data class TileColors(val background: Color, val content: Color, val border: Color)

private fun colorsFor(status: StateTileStatus): TileColors = when (status) {
    StateTileStatus.DEFAULT -> TileColors(AtlasColors.paper, AtlasColors.ink, AtlasColors.hairline)
    StateTileStatus.CORRECT -> TileColors(AtlasColors.green, Color.White, AtlasColors.green)
    StateTileStatus.WRONG -> TileColors(AtlasColors.red, Color.White, AtlasColors.red)
    StateTileStatus.NEAR_MISS -> TileColors(AtlasColors.cyan, Color.White, AtlasColors.cyan)
}

@Composable
fun StateTile(
    code: String,
    modifier: Modifier = Modifier,
    status: StateTileStatus = StateTileStatus.DEFAULT,
    onClick: (() -> Unit)? = null,
    contentDescription: String = code,
) {
    val colors = colorsFor(status)
    Box(
        modifier = modifier
            .let { if (onClick != null) it.clickable(onClick = onClick) else it }
            .minimumInteractiveComponentSize()
            .size(DpSize(44.dp, 36.dp))
            .clip(AtlasShapes.corner)
            .background(colors.background)
            .border(1.dp, colors.border, AtlasShapes.corner)
            .semantics { this.contentDescription = contentDescription },
        contentAlignment = Alignment.Center,
    ) {
        Text(
            code,
            style = AtlasType.mono(size = 11.sp, weight = FontWeight.W500, letterSpacing = 0.04.em),
            color = colors.content,
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFBFAF6)
@Composable
private fun StateTilePreview() {
    AtlasTheme {
        androidx.compose.foundation.layout.Row(
            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(4.dp),
        ) {
            StateTile("NH", status = StateTileStatus.DEFAULT)
            StateTile("VT", status = StateTileStatus.CORRECT)
            StateTile("MA", status = StateTileStatus.WRONG)
            StateTile("RI", status = StateTileStatus.NEAR_MISS)
        }
    }
}
