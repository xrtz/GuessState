package ru.xrtz.whichstate.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import ru.xrtz.whichstate.core.designsystem.theme.AtlasColors
import ru.xrtz.whichstate.core.designsystem.theme.AtlasShapes
import ru.xrtz.whichstate.core.designsystem.theme.AtlasTheme
import ru.xrtz.whichstate.core.designsystem.theme.AtlasType

enum class AtlasButtonVariant { PRIMARY, SECONDARY, INK }

private data class ButtonColors(val background: Color, val content: Color, val border: Color?)

private fun colorsFor(variant: AtlasButtonVariant): ButtonColors = when (variant) {
    AtlasButtonVariant.PRIMARY -> ButtonColors(AtlasColors.red, Color.White, border = null)

    AtlasButtonVariant.SECONDARY -> ButtonColors(AtlasColors.paper, AtlasColors.ink, border = AtlasColors.hairline)

    AtlasButtonVariant.INK -> ButtonColors(AtlasColors.ink, AtlasColors.paper, border = null)
}

@Composable
fun AtlasButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: AtlasButtonVariant = AtlasButtonVariant.PRIMARY,
    enabled: Boolean = true,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val colors = colorsFor(variant)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 48.dp)
            .graphicsLayer(alpha = if (pressed || !enabled) 0.88f else 1f)
            .clip(AtlasShapes.corner)
            .background(colors.background)
            .let { if (colors.border != null) it.border(1.dp, colors.border, AtlasShapes.corner) else it }
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled,
                onClick = onClick,
            )
            .padding(vertical = 13.dp, horizontal = 16.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text.uppercase(),
            style = AtlasType.mono(size = 13.sp, weight = FontWeight.W500, letterSpacing = 0.12.em),
            color = colors.content,
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF3F1EA, widthDp = 320)
@Composable
private fun AtlasButtonPreview() {
    AtlasTheme {
        androidx.compose.foundation.layout.Column(
            verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(12.dp),
            modifier = Modifier.padding(16.dp),
        ) {
            AtlasButton("Play again", {}, variant = AtlasButtonVariant.PRIMARY)
            AtlasButton("Change level", {}, variant = AtlasButtonVariant.SECONDARY)
            AtlasButton("Share result", {}, variant = AtlasButtonVariant.INK)
        }
    }
}
