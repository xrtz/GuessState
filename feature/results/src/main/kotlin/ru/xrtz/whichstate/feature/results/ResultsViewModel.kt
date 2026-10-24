package ru.xrtz.whichstate.feature.results

import android.app.Activity
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
import kotlinx.coroutines.launch
import ru.xrtz.whichstate.core.domain.model.RoundId
import ru.xrtz.whichstate.core.domain.model.RoundSummary
import ru.xrtz.whichstate.core.domain.usecase.GetRoundSummaryUseCase
import ru.xrtz.whichstate.feature.results.share.ShareCardRenderer

@HiltViewModel(assistedFactory = ResultsViewModel.Factory::class)
class ResultsViewModel @AssistedInject constructor(
    @Assisted private val roundId: RoundId,
    private val getRoundSummary: GetRoundSummaryUseCase,
    private val shareCardRenderer: ShareCardRenderer,
    private val interstitialGate: ResultsInterstitialGate,
) : ViewModel() {
    private var summary: RoundSummary? = null

    private val _uiState = MutableStateFlow<ResultsUiState>(ResultsUiState.Loading)
    val uiState: StateFlow<ResultsUiState> = _uiState.asStateFlow()

    private val effectChannel = Channel<ResultsEffect>(Channel.BUFFERED)
    val effects: Flow<ResultsEffect> = effectChannel.receiveAsFlow()

    init {
        viewModelScope.launch {
            val loaded = getRoundSummary(roundId)
            summary = loaded
            interstitialGate.prepareForExit()
            _uiState.value = loaded.toUiState()
        }
    }

    fun onAction(action: ResultsAction) {
        when (action) {
            is ResultsAction.PlayAgain -> summary?.let {
                exit(action.activity, ResultsEffect.PlayAgain(it.level))
            }
            is ResultsAction.ChangeLevel -> exit(action.activity, ResultsEffect.ChangeLevel)
            ResultsAction.Share -> share()
        }
    }

    private fun exit(activity: Activity, effect: ResultsEffect) {
        viewModelScope.launch {
            interstitialGate.showIfDue(activity)
            effectChannel.send(effect)
        }
    }

    private fun share() {
        val current = summary ?: return
        val loaded = _uiState.value as? ResultsUiState.Loaded ?: return
        if (loaded.isSharing) return
        _uiState.value = loaded.copy(isSharing = true)
        viewModelScope.launch {
            val uri = shareCardRenderer.render(current)
            (_uiState.value as? ResultsUiState.Loaded)?.let { _uiState.value = it.copy(isSharing = false) }
            effectChannel.send(ResultsEffect.ShareImage(uri))
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(roundId: RoundId): ResultsViewModel
    }
}
