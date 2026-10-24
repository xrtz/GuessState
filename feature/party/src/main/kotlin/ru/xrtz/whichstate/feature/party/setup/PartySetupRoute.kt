package ru.xrtz.whichstate.feature.party.setup

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import ru.xrtz.whichstate.core.domain.model.PartyMode

@Composable
fun PartySetupRoute(
    mode: PartyMode,
    setupKey: String,
    onStartGame: (mode: PartyMode, teamNames: List<String>, penaltyOnSkip: Boolean) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PartySetupViewModel = hiltViewModel<PartySetupViewModel, PartySetupViewModel.Factory>(
        key = setupKey,
    ) { it.create(mode) },
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(viewModel) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is PartySetupEffect.StartGame -> onStartGame(effect.mode, effect.teamNames, effect.penaltyOnSkip)
                PartySetupEffect.Back -> onBack()
            }
        }
    }

    BackHandler(onBack = { viewModel.onAction(PartySetupAction.Back) })

    PartySetupScreen(state = state, onAction = viewModel::onAction, modifier = modifier)
}
