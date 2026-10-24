package ru.xrtz.whichstate.feature.party.setup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.xrtz.whichstate.core.domain.game.PartyRules
import ru.xrtz.whichstate.core.domain.model.PartyMode

@HiltViewModel(assistedFactory = PartySetupViewModel.Factory::class)
class PartySetupViewModel @AssistedInject constructor(
    @Assisted mode: PartyMode,
) : ViewModel() {
    private val rules = PartyRules()
    private val _uiState = MutableStateFlow(PartySetupUiState.initial(mode, rules))
    val uiState: StateFlow<PartySetupUiState> = _uiState.asStateFlow()

    private val effectChannel = Channel<PartySetupEffect>(Channel.BUFFERED)
    val effects: Flow<PartySetupEffect> = effectChannel.receiveAsFlow()

    fun onAction(action: PartySetupAction) {
        when (action) {
            is PartySetupAction.SelectTeamCount -> if (action.count in rules.teamCounts) {
                _uiState.update { it.copy(teamCount = action.count) }
            }
            is PartySetupAction.ChangeTeamName -> _uiState.update { state ->
                val names = state.teamNames.toMutableList()
                names[action.index] = action.name.take(rules.maxTeamNameLength)
                state.copy(teamNames = names)
            }
            is PartySetupAction.SetPenaltyOnSkip -> _uiState.update { it.copy(penaltyOnSkip = action.enabled) }
            PartySetupAction.Start -> _uiState.value.let { state ->
                send(PartySetupEffect.StartGame(state.mode, state.teamNames.take(state.teamCount), state.penaltyOnSkip))
            }
            PartySetupAction.Back -> send(PartySetupEffect.Back)
        }
    }

    private fun send(effect: PartySetupEffect) {
        viewModelScope.launch { effectChannel.send(effect) }
    }

    @AssistedFactory
    interface Factory {
        fun create(mode: PartyMode): PartySetupViewModel
    }
}
