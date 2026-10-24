package ru.xrtz.whichstate.core.designsystem.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import ru.xrtz.whichstate.core.designsystem.theme.AtlasColors
import ru.xrtz.whichstate.core.designsystem.theme.AtlasTheme
import ru.xrtz.whichstate.core.designsystem.theme.AtlasType

@Composable
fun Overline(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = AtlasColors.muted,
) {
    Text(
        text = text.uppercase(),
        modifier = modifier,
        style = AtlasType.overline(),
        color = color,
    )
}

@Preview(showBackground = true, backgroundColor = 0xFFF3F1EA)
@Composable
private fun OverlinePreview() {
    AtlasTheme { Overline("United States · Geography") }
}
