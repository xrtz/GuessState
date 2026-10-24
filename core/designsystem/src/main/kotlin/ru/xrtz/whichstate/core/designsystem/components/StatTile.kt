package ru.xrtz.whichstate.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
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

enum class StatTileVariant { NEUTRAL, CORRECT, WRONG, NET }

@Composable
fun StatTile(
    value: String,
    label: String,
    modifier: Modifier = Modifier,
    variant: StatTileVariant = StatTileVariant.NEUTRAL,
) {
    val valueColor = when (variant) {
        StatTileVariant.NEUTRAL -> AtlasColors.ink
        StatTileVariant.CORRECT -> AtlasColors.green
        StatTileVariant.WRONG -> AtlasColors.red
        StatTileVariant.NET -> AtlasColors.cyan
    }
    Column(
        modifier = modifier
            .clip(AtlasShapes.corner)
            .background(AtlasColors.paper)
            .border(1.dp, AtlasColors.hairline, AtlasShapes.corner)
            .padding(vertical = 12.dp, horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            value,
            style = AtlasType.display(size = 30.sp, letterSpacing = 0.em, lineHeight = 30.sp),
            color = valueColor,
        )
        Text(
            label.uppercase(),
            style = AtlasType.mono(size = 10.sp, letterSpacing = 0.16.em),
            color = AtlasColors.muted,
            modifier = Modifier.padding(top = 6.dp),
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFBFAF6, widthDp = 200)
@Composable
private fun StatTilePreview() {
    AtlasTheme {
        androidx.compose.foundation.layout.Row(
            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(14.dp),
        ) {
            StatTile("12", "Correct", variant = StatTileVariant.CORRECT, modifier = Modifier.weight(1f))
            StatTile("3", "Wrong", variant = StatTileVariant.WRONG, modifier = Modifier.weight(1f))
        }
    }
}
