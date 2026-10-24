package ru.xrtz.whichstate.feature.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import ru.xrtz.whichstate.core.domain.model.Level
import ru.xrtz.whichstate.core.domain.model.LevelHistory
import ru.xrtz.whichstate.core.domain.usecase.GetHistoryUseCase
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val getHistory: GetHistoryUseCase,
) : ViewModel() {
    private var history: List<LevelHistory> = emptyList()

    private val _uiState = MutableStateFlow<HistoryUiState>(HistoryUiState.Loading)
    val uiState: StateFlow<HistoryUiState> = _uiState.asStateFlow()

    private val effectChannel = Channel<HistoryEffect>(Channel.BUFFERED)
    val effects: Flow<HistoryEffect> = effectChannel.receiveAsFlow()

    init {
        viewModelScope.launch {
            history = getHistory()
            _uiState.value = history.toUiState(Level.ROOKIE)
        }
    }

    fun onAction(action: HistoryAction) {
        when (action) {
            is HistoryAction.SelectLevel -> _uiState.value = history.toUiState(action.level)
            HistoryAction.Back -> sendEffect(HistoryEffect.Back)
        }
    }

    private fun sendEffect(effect: HistoryEffect) {
        viewModelScope.launch { effectChannel.send(effect) }
    }
}
