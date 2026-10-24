package ru.xrtz.whichstate.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.xrtz.whichstate.core.designsystem.theme.AtlasColors
import ru.xrtz.whichstate.core.designsystem.theme.AtlasShapes
import ru.xrtz.whichstate.core.designsystem.theme.AtlasTheme
import ru.xrtz.whichstate.core.designsystem.theme.AtlasType

@Composable
fun AtlasTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
) {
    var focused by remember { mutableStateOf(false) }
    val textStyle: TextStyle = AtlasType.body(size = 15.sp).copy(color = AtlasColors.ink)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(AtlasShapes.corner)
            .background(AtlasColors.card)
            .border(1.dp, if (focused) AtlasColors.cyan else AtlasColors.hairline, AtlasShapes.corner)
            .padding(horizontal = 14.dp, vertical = 12.dp),
    ) {
        if (value.isEmpty()) {
            androidx.compose.material3.Text(placeholder, style = textStyle, color = AtlasColors.muted)
        }
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            textStyle = textStyle,
            singleLine = true,
            cursorBrush = SolidColor(AtlasColors.cyan),
            modifier = Modifier
                .fillMaxWidth()
                .onFocusChanged { focused = it.isFocused },
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF3F1EA, widthDp = 320)
@Composable
private fun AtlasTextFieldPreview() {
    AtlasTheme {
        AtlasTextField(value = "", onValueChange = {}, placeholder = "Team name")
    }
}
