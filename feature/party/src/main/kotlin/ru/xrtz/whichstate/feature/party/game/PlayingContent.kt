package ru.xrtz.whichstate.feature.party.game

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import ru.xrtz.whichstate.core.designsystem.components.RoundTimer
import ru.xrtz.whichstate.core.designsystem.components.SwipeCard
import ru.xrtz.whichstate.core.designsystem.theme.AtlasColors
import ru.xrtz.whichstate.core.designsystem.theme.AtlasShapes
import ru.xrtz.whichstate.core.designsystem.theme.AtlasType
import ru.xrtz.whichstate.core.domain.model.PartyMode
import ru.xrtz.whichstate.feature.party.R
import kotlin.math.abs

private val SWIPE_THRESHOLD = 60.dp
private val LABEL_THRESHOLD = 30.dp
private val FLY_OUT_DISTANCE = 500.dp
private const val FLY_OUT_MS = 280
private const val SNAP_BACK_MS = 200

private data class OutgoingCard(
    val key: String,
    val cityName: String,
    val stateCode: String,
    val startOffsetPx: Float,
    val correct: Boolean,
)

@Composable
internal fun PlayingContent(
    state: PartyGameUiState.Playing,
    onAction: (PartyGameAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    val density = LocalDensity.current
    val scope = rememberCoroutineScope()
    val drag = remember(state.cardKey) { Animatable(0f) }
    var outgoing by remember { mutableStateOf<OutgoingCard?>(null) }
    val swipeThresholdPx = with(density) { SWIPE_THRESHOLD.toPx() }

    val answer by rememberUpdatedState { correct: Boolean ->
        if (outgoing == null) {
            outgoing = OutgoingCard(state.cardKey, state.cityName, state.stateCode, drag.value, correct)
            scope.launch { drag.snapTo(0f) }
            onAction(PartyGameAction.Answer(correct))
        }
    }

    Column(modifier.navigationBarsPadding().padding(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 24.dp)) {
        PlayingHeader(state)

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .pointerInput(state.cardKey) {
                    detectHorizontalDragGestures(
                        onDragEnd = {
                            when {
                                drag.value > swipeThresholdPx -> answer(true)
                                drag.value < -swipeThresholdPx -> answer(false)
                                else -> scope.launch { drag.animateTo(0f, tween(SNAP_BACK_MS)) }
                            }
                        },
                        onDragCancel = { scope.launch { drag.animateTo(0f, tween(SNAP_BACK_MS)) } },
                        onHorizontalDrag = { change, amount ->
                            change.consume()
                            scope.launch { drag.snapTo(drag.value + amount) }
                        },
                    )
                },
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Box(Modifier.widthIn(max = 340.dp)) {
                CardFace(state.mode, state.cityName, state.stateCode, offsetPx = drag.value)
                outgoing?.let { card -> FlyingCard(state.mode, card, onFinished = { outgoing = null }) }
            }
            Row(Modifier.widthIn(max = 340.dp).fillMaxWidth().padding(top = 12.dp, start = 4.dp, end = 4.dp)) {
                SwipeHint(stringResource(R.string.party_swipe_arrow_skip), Modifier.weight(1f))
                SwipeHint(stringResource(R.string.party_swipe_arrow_correct), textAlign = TextAlign.End)
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(14.dp), modifier = Modifier.padding(top = 12.dp)) {
            TintedButton(stringResource(R.string.party_button_skip), AtlasColors.red, Modifier.weight(1f)) {
                answer(false)
            }
            TintedButton(stringResource(R.string.party_button_correct), AtlasColors.green, Modifier.weight(1f)) {
                answer(true)
            }
        }
    }
}

@Composable
private fun PlayingHeader(state: PartyGameUiState.Playing) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
        RoundTimer(state.secondsLeft, state.isTimeWarning, fontSize = 29.sp)
        Text(
            text = stringResource(R.string.party_progress, state.cardNumber, state.cardCount),
            style = AtlasType.mono(size = 15.sp, letterSpacing = 0.06.em),
            color = AtlasColors.muted,
            textAlign = TextAlign.End,
            modifier = Modifier.weight(1f),
        )
    }
    Text(
        text = stringResource(
            if (state.mode == PartyMode.DESCRIBE) R.string.party_hint_describe else R.string.party_hint_guess_state,
        ).uppercase(),
        style = AtlasType.mono(size = 10.5.sp, letterSpacing = 0.16.em),
        color = AtlasColors.muted,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
    )
}

@Composable
private fun CardFace(
    mode: PartyMode,
    cityName: String,
    stateCode: String,
    offsetPx: Float,
    modifier: Modifier = Modifier,
) {
    val density = LocalDensity.current
    SwipeCard(
        cityName = cityName,
        stateText = stateCode,
        isStateHint = mode == PartyMode.DESCRIBE,
        offsetX = with(density) { offsetPx.toDp() },
        labelThreshold = LABEL_THRESHOLD,
        correctLabel = stringResource(R.string.party_swipe_label_correct),
        skipLabel = stringResource(R.string.party_swipe_label_skip),
        modifier = modifier,
    )
}

@Composable
private fun FlyingCard(mode: PartyMode, card: OutgoingCard, onFinished: () -> Unit) {
    val flyOutPx = with(LocalDensity.current) { FLY_OUT_DISTANCE.toPx() }
    val offset = remember(card) { Animatable(card.startOffsetPx) }
    LaunchedEffect(card) {
        offset.animateTo(if (card.correct) flyOutPx else -flyOutPx, tween(FLY_OUT_MS))
        onFinished()
    }
    CardFace(
        mode = mode,
        cityName = card.cityName,
        stateCode = card.stateCode,
        offsetPx = offset.value,
        modifier = Modifier.graphicsLayer { alpha = 1f - (abs(offset.value) / flyOutPx).coerceIn(0f, 1f) },
    )
}

@Composable
private fun SwipeHint(text: String, modifier: Modifier = Modifier, textAlign: TextAlign = TextAlign.Start) {
    Text(
        text = text.uppercase(),
        style = AtlasType.mono(size = 10.5.sp, letterSpacing = 0.1.em),
        color = AtlasColors.muted.copy(alpha = 0.5f),
        textAlign = textAlign,
        modifier = modifier,
    )
}

@Composable
private fun TintedButton(text: String, color: Color, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Text(
        text = text.uppercase(),
        style = AtlasType.mono(size = 13.5.sp, weight = FontWeight.W500, letterSpacing = 0.1.em),
        color = color,
        textAlign = TextAlign.Center,
        modifier = modifier
            .clickable(onClick = onClick)
            .minimumInteractiveComponentSize()
            .clip(AtlasShapes.corner)
            .background(color.copy(alpha = 0.08f))
            .border(1.dp, color.copy(alpha = 0.3f), AtlasShapes.corner)
            .padding(vertical = 15.dp),
    )
}
