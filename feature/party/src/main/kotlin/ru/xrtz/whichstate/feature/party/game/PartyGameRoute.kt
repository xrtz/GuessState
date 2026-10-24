package ru.xrtz.whichstate.feature.party.game

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.LocalView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import ru.xrtz.whichstate.core.domain.model.PartyMode

@Composable
fun PartyGameRoute(
    mode: PartyMode,
    teamNames: List<String>,
    penaltyOnSkip: Boolean,
    gameKey: String,
    onExitToMenu: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PartyGameViewModel = hiltViewModel<PartyGameViewModel, PartyGameViewModel.Factory>(
        key = gameKey,
    ) { it.create(mode, teamNames, penaltyOnSkip) },
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(viewModel) {
        viewModel.effects.collect { effect ->
            when (effect) {
                PartyGameEffect.ExitToMenu -> onExitToMenu()
            }
        }
    }

    val lifecycleOwner = LocalLifecycleOwner.current
    val currentOnAction by rememberUpdatedState(viewModel::onAction)
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_STOP -> currentOnAction(PartyGameAction.Pause)
                Lifecycle.Event.ON_START -> currentOnAction(PartyGameAction.Resume)
                else -> Unit
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    if (state is PartyGameUiState.Playing) {
        val view = LocalView.current
        DisposableEffect(view) {
            view.keepScreenOn = true
            onDispose { view.keepScreenOn = false }
        }
    }

    BackHandler(onBack = { viewModel.onAction(PartyGameAction.ExitToMenu) })

    PartyGameScreen(state = state, onAction = viewModel::onAction, modifier = modifier)
}
