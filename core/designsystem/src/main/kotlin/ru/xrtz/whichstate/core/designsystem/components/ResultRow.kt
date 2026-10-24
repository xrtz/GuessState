package ru.xrtz.whichstate.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import ru.xrtz.whichstate.core.designsystem.theme.AtlasColors
import ru.xrtz.whichstate.core.designsystem.theme.AtlasTheme
import ru.xrtz.whichstate.core.designsystem.theme.AtlasType

enum class ResultRowVariant { CORRECT, WRONG, NEAR_MISS }

private data class ResultRowColors(val icon: Color, val city: Color, val stateCode: Color)

private fun colorsFor(variant: ResultRowVariant): ResultRowColors = when (variant) {
    ResultRowVariant.CORRECT -> ResultRowColors(AtlasColors.green, AtlasColors.ink, AtlasColors.ink)
    ResultRowVariant.WRONG -> ResultRowColors(AtlasColors.red, AtlasColors.muted, AtlasColors.red)
    ResultRowVariant.NEAR_MISS -> ResultRowColors(AtlasColors.cyan, AtlasColors.muted, AtlasColors.cyan)
}

@Composable
fun ResultRow(cityName: String, stateText: String, variant: ResultRowVariant, modifier: Modifier = Modifier) {
    val colors = colorsFor(variant)
    val icon = when (variant) {
        ResultRowVariant.CORRECT -> "✓"
        ResultRowVariant.WRONG -> "✗"
        ResultRowVariant.NEAR_MISS -> "≈"
    }
    Row(
        modifier = modifier.padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(icon, style = AtlasType.mono(size = 14.sp), color = colors.icon, modifier = Modifier.width(18.dp))
        Text(
            cityName,
            style = AtlasType.body(size = 15.sp),
            color = colors.city,
            modifier = Modifier.weight(1f),
        )
        Text(
            stateText,
            style = AtlasType.mono(size = 12.5.sp, letterSpacing = 0.06.em),
            color = colors.stateCode,
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF3F1EA, widthDp = 340)
@Composable
private fun ResultRowPreview() {
    AtlasTheme {
        androidx.compose.foundation.layout.Column {
            ResultRow("New York", "NY", ResultRowVariant.CORRECT)
            ResultRow("Springfield", "MA → MO", ResultRowVariant.WRONG)
            ResultRow("Fort Worth", "OK → TX", ResultRowVariant.NEAR_MISS)
        }
    }
}
