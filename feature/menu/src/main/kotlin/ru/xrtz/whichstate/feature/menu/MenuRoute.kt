package ru.xrtz.whichstate.feature.menu

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import ru.xrtz.whichstate.core.domain.model.Level
import ru.xrtz.whichstate.core.domain.model.PartyMode

@Composable
fun MenuRoute(
    onLevelSelected: (Level) -> Unit,
    onPartyModeSelected: (PartyMode) -> Unit,
    onHistorySelected: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MenuViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(viewModel) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is MenuEffect.OpenLevel -> onLevelSelected(effect.level)
                is MenuEffect.OpenParty -> onPartyModeSelected(effect.mode)
                MenuEffect.OpenHistory -> onHistorySelected()
            }
        }
    }

    MenuScreen(state = state, onAction = viewModel::onAction, modifier = modifier)
}
