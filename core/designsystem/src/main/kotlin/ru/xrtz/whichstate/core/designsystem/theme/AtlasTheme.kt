package ru.xrtz.whichstate.core.designsystem.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val AtlasColorScheme = lightColorScheme(
    background = AtlasColors.paper,
    surface = AtlasColors.card,
    onBackground = AtlasColors.ink,
    onSurface = AtlasColors.ink,
    primary = AtlasColors.cyan,
    error = AtlasColors.red,
)

@Composable
fun AtlasTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = AtlasColorScheme,
        content = content,
    )
}
