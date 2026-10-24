package ru.xrtz.whichstate.feature.game

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import ru.xrtz.whichstate.core.designsystem.components.Overline
import ru.xrtz.whichstate.core.designsystem.components.RoundTimer
import ru.xrtz.whichstate.core.designsystem.components.ScoreBadge
import ru.xrtz.whichstate.core.designsystem.components.ScoreBadgeVariant
import ru.xrtz.whichstate.core.designsystem.theme.AtlasColors
import ru.xrtz.whichstate.core.designsystem.theme.AtlasShapes
import ru.xrtz.whichstate.core.designsystem.theme.AtlasTheme
import ru.xrtz.whichstate.core.designsystem.theme.AtlasType
import ru.xrtz.whichstate.core.domain.model.UsState
import ru.xrtz.whichstate.core.map.NortheastTiles
import ru.xrtz.whichstate.core.map.UsMap

@Composable
fun GameScreen(state: GameUiState, onAction: (GameAction) -> Unit, modifier: Modifier = Modifier) {
    BoxWithConstraints(modifier = modifier.fillMaxSize().background(AtlasColors.paper)) {
        if (maxWidth > maxHeight) {
            GameScreenLandscape(state, onAction)
        } else {
            GameScreenPortrait(state, onAction)
        }
    }
}

@Composable
private fun GameScreenPortrait(state: GameUiState, onAction: (GameAction) -> Unit) {
    Column(modifier = Modifier.fillMaxSize()) {
        GameHeader(state, onAction)
        CityPrompt(state)
        MapArea(state, onAction)
    }
}

@Composable
private fun GameScreenLandscape(state: GameUiState, onAction: (GameAction) -> Unit) {
    val onStateTapped: (UsState) -> Unit = { onAction(GameAction.TapState(it)) }
    Column(modifier = Modifier.fillMaxSize()) {
        GameHeader(state, onAction)
        Row(modifier = Modifier.weight(1f).fillMaxWidth().navigationBarsPadding()) {
            Column(
                modifier = Modifier
                    .width(240.dp)
                    .fillMaxHeight()
                    .padding(start = 16.dp, end = 8.dp, top = 8.dp, bottom = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                CityPrompt(state, compact = true)
                Box(Modifier.weight(1f))
                NortheastTiles(onStateTapped = onStateTapped, highlight = state.highlight)
            }
            UsMap(
                onStateTapped = onStateTapped,
                highlight = state.highlight,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .padding(end = 16.dp, top = 8.dp, bottom = 12.dp),
            )
        }
    }
}

@Composable
private fun GameHeader(state: GameUiState, onAction: (GameAction) -> Unit) {
    Column(modifier = Modifier.fillMaxWidth().background(AtlasColors.card)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            GameBackButton(onClick = { onAction(GameAction.ExitToMenu) })
            Row(horizontalArrangement = Arrangement.spacedBy(18.dp)) {
                ScoreBadge(state.correct, ScoreBadgeVariant.CORRECT)
                ScoreBadge(state.wrong, ScoreBadgeVariant.WRONG)
            }
            RoundTimer(secondsLeft = state.secondsLeft, isWarning = state.isTimeWarning)
        }
        Box(Modifier.fillMaxWidth().height(1.dp).background(AtlasColors.cyan))
    }
}

@Composable
private fun GameBackButton(onClick: () -> Unit) {
    Text(
        text = stringResource(R.string.game_back_to_menu),
        style = AtlasType.mono(size = 11.2.sp, letterSpacing = 0.16.em),
        color = AtlasColors.muted,
        modifier = Modifier
            .clickable(onClick = onClick)
            .minimumInteractiveComponentSize()
            .clip(AtlasShapes.corner)
            .border(1.dp, AtlasColors.hairline, AtlasShapes.corner)
            .padding(horizontal = 12.dp, vertical = 6.dp),
    )
}

@Composable
private fun CityPrompt(state: GameUiState, compact: Boolean = false) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(AtlasColors.paper)
            .padding(
                top = if (compact) 4.dp else 26.dp,
                bottom = if (compact) 10.dp else 18.dp,
                start = 20.dp,
                end = 20.dp,
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Overline(stringResource(R.string.game_prompt))
        AnimatedContent(
            targetState = state.cityName,
            transitionSpec = {
                (fadeIn(tween(220)) + slideInVertically(tween(220)) { it / 6 }) togetherWith fadeOut(tween(90))
            },
            label = "city-name",
        ) { cityName ->
            Text(
                text = cityName.uppercase(),
                style = AtlasType.display(
                    size = if (compact) 19.sp else 40.sp,
                    letterSpacing = (-0.02).em,
                    lineHeight = if (compact) 21.sp else 42.sp,
                ),
                color = AtlasColors.ink,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = if (compact) 8.dp else 12.dp),
            )
        }
        if (state.isNearMiss) {
            Text(
                text = stringResource(R.string.game_near_miss_message).uppercase(),
                style = AtlasType.mono(size = if (compact) 9.sp else 11.sp, letterSpacing = 0.14.em),
                color = AtlasColors.cyan,
                modifier = Modifier.padding(top = 4.dp),
            )
        }
    }
}

@Composable
private fun MapArea(state: GameUiState, onAction: (GameAction) -> Unit) {
    val onStateTapped: (UsState) -> Unit = { onAction(GameAction.TapState(it)) }
    Column(modifier = Modifier.fillMaxSize().navigationBarsPadding()) {
        UsMap(onStateTapped = onStateTapped, highlight = state.highlight, modifier = Modifier.weight(1f))
        NortheastTiles(
            onStateTapped = onStateTapped,
            highlight = state.highlight,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp),
        )
    }
}

@Preview(showBackground = true, widthDp = 380, heightDp = 700)
@Composable
private fun GameScreenPreview() {
    AtlasTheme {
        GameScreen(
            state = GameUiState(cityName = "Springfield", secondsLeft = 24, correct = 3, wrong = 1),
            onAction = {},
        )
    }
}
