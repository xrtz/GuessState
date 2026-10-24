package ru.xrtz.whichstate.feature.game

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import ru.xrtz.whichstate.core.domain.model.Level
import ru.xrtz.whichstate.core.domain.model.RoundId

@Composable
fun GameRoute(
    level: Level,
    onExitToMenu: () -> Unit,
    onRoundFinished: (RoundId) -> Unit,
    modifier: Modifier = Modifier,

    roundKey: String,
    viewModel: GameViewModel = hiltViewModel<GameViewModel, GameViewModel.Factory>(key = roundKey) { it.create(level) },
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(viewModel) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is GameEffect.OpenResults -> onRoundFinished(effect.roundId)
                GameEffect.ExitToMenu -> onExitToMenu()
            }
        }
    }

    val lifecycleOwner = LocalLifecycleOwner.current
    val currentOnAction by rememberUpdatedState(viewModel::onAction)
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_STOP -> currentOnAction(GameAction.Pause)
                Lifecycle.Event.ON_START -> currentOnAction(GameAction.Resume)
                else -> Unit
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    BackHandler(onBack = { viewModel.onAction(GameAction.ExitToMenu) })

    GameScreen(state = state, onAction = viewModel::onAction, modifier = modifier)
}
