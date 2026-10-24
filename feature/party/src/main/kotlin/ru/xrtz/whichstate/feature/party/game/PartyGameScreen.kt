package ru.xrtz.whichstate.feature.party.game

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import ru.xrtz.whichstate.core.designsystem.theme.AtlasColors
import ru.xrtz.whichstate.core.designsystem.theme.AtlasShapes
import ru.xrtz.whichstate.core.designsystem.theme.AtlasType
import ru.xrtz.whichstate.feature.party.R

@Composable
fun PartyGameScreen(state: PartyGameUiState, onAction: (PartyGameAction) -> Unit, modifier: Modifier = Modifier) {
    val screenModifier = modifier.fillMaxSize().background(AtlasColors.paper)
    when (state) {
        PartyGameUiState.Loading -> Box(screenModifier)
        is PartyGameUiState.Handoff -> HandoffContent(state, onAction, screenModifier)
        is PartyGameUiState.Playing -> PlayingContent(state, onAction, screenModifier)
        is PartyGameUiState.RoundEnd -> RoundEndContent(state, onAction, screenModifier)
        is PartyGameUiState.GameEnd -> GameEndContent(state, onAction, screenModifier)
    }
}

@Composable
internal fun teamName(label: TeamLabel): String =
    label.customName ?: stringResource(R.string.party_team_default, label.number)

@Composable
internal fun PartyBackButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    Text(
        text = stringResource(R.string.party_back_to_menu),
        style = AtlasType.mono(size = 11.2.sp, letterSpacing = 0.16.em),
        color = AtlasColors.muted,
        modifier = modifier
            .clickable(onClick = onClick)
            .minimumInteractiveComponentSize()
            .clip(AtlasShapes.corner)
            .border(1.dp, AtlasColors.hairline, AtlasShapes.corner)
            .padding(horizontal = 12.dp, vertical = 6.dp),
    )
}
