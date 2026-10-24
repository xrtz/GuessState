package ru.xrtz.whichstate.core.designsystem.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.xrtz.whichstate.core.designsystem.theme.AtlasColors
import ru.xrtz.whichstate.core.designsystem.theme.AtlasTheme

@Composable
fun AtlasToggle(checked: Boolean, onCheckedChange: (Boolean) -> Unit, modifier: Modifier = Modifier) {
    val trackShape = RoundedCornerShape(50)
    val thumbInset by animateDpAsState(targetValue = if (checked) 22.dp else 2.dp, label = "toggle-thumb")
    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = modifier
            .toggleable(
                value = checked,
                interactionSource = interactionSource,
                indication = null,
                role = Role.Switch,
                onValueChange = onCheckedChange,
            )
            .minimumInteractiveComponentSize()
            .size(width = 44.dp, height = 24.dp)
            .clip(trackShape)
            .background(if (checked) AtlasColors.cyan.copy(alpha = 0.15f) else AtlasColors.paper)
            .border(1.dp, if (checked) AtlasColors.cyan else AtlasColors.hairline, trackShape),
    ) {
        Box(
            Modifier
                .align(Alignment.CenterStart)
                .padding(start = thumbInset)
                .size(18.dp)
                .clip(CircleShape)
                .background(if (checked) AtlasColors.cyan else AtlasColors.muted),
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF3F1EA)
@Composable
private fun AtlasTogglePreview() {
    AtlasTheme {
        androidx.compose.foundation.layout.Row(
            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(16.dp),
        ) {
            AtlasToggle(checked = false, onCheckedChange = {})
            AtlasToggle(checked = true, onCheckedChange = {})
        }
    }
}
