package ru.xrtz.whichstate.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
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
fun PartyModeCard(
    name: String,
    description: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .clip(AtlasShapes.corner)
            .background(AtlasColors.card)
            .border(1.dp, AtlasColors.hairline, AtlasShapes.corner)
            .clickable(onClick = onClick)
            .padding(horizontal = 18.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Text(
            text = name.uppercase(),
            style = AtlasType.display(size = 17.6.sp, letterSpacing = (-0.01).em, lineHeight = 19.sp),
            color = AtlasColors.ink,
        )
        Text(
            text = description,
            style = AtlasType.body(size = 13.1.sp),
            color = AtlasColors.muted,
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF3F1EA, widthDp = 200)
@Composable
private fun PartyModeCardPreview() {
    AtlasTheme {
        PartyModeCard(
            name = "Describe",
            description = "Describe the city — teammate guesses its name",
            onClick = {},
        )
    }
}
