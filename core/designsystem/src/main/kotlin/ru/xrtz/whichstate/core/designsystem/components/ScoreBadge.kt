package ru.xrtz.whichstate.core.designsystem.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.xrtz.whichstate.core.designsystem.theme.AtlasColors
import ru.xrtz.whichstate.core.designsystem.theme.AtlasTheme
import ru.xrtz.whichstate.core.designsystem.theme.AtlasType

enum class ScoreBadgeVariant { CORRECT, WRONG }

@Composable
fun ScoreBadge(count: Int, variant: ScoreBadgeVariant, modifier: Modifier = Modifier) {
    val color = when (variant) {
        ScoreBadgeVariant.CORRECT -> AtlasColors.green
        ScoreBadgeVariant.WRONG -> AtlasColors.red
    }
    val symbol = when (variant) {
        ScoreBadgeVariant.CORRECT -> "✓"
        ScoreBadgeVariant.WRONG -> "✗"
    }
    Row(modifier, horizontalArrangement = Arrangement.spacedBy(5.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(symbol, style = AtlasType.mono(size = 15.sp, weight = FontWeight.W500), color = color)
        Text(count.toString(), style = AtlasType.mono(size = 15.sp, weight = FontWeight.W500), color = color)
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFBFAF6)
@Composable
private fun ScoreBadgePreview() {
    AtlasTheme {
        Row(horizontalArrangement = Arrangement.spacedBy(18.dp)) {
            ScoreBadge(12, ScoreBadgeVariant.CORRECT)
            ScoreBadge(3, ScoreBadgeVariant.WRONG)
        }
    }
}
