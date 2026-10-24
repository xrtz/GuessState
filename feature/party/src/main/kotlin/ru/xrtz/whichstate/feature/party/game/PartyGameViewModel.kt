package ru.xrtz.whichstate.feature.party.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import ru.xrtz.whichstate.core.domain.game.PartyEvent
import ru.xrtz.whichstate.core.domain.game.PartyGameReducer
import ru.xrtz.whichstate.core.domain.game.PartyGameState
import ru.xrtz.whichstate.core.domain.game.PartyRules
import ru.xrtz.whichstate.core.domain.model.PartyMode
import ru.xrtz.whichstate.core.domain.policy.PartyScoring
import ru.xrtz.whichstate.core.domain.time.GameClock
import ru.xrtz.whichstate.core.domain.usecase.StartPartyGameUseCase

private const val TICK_INTERVAL_MS = 100L

@HiltViewModel(assistedFactory = PartyGameViewModel.Factory::class)
class PartyGameViewModel @AssistedInject constructor(
    @Assisted private val mode: PartyMode,
    @Assisted private val teamNames: List<String>,
    @Assisted private val penaltyOnSkip: Boolean,
    private val startPartyGame: StartPartyGameUseCase,
    private val scoring: PartyScoring,
    private val clock: GameClock,
) : ViewModel() {
    private val rules = PartyRules()
    private val reducer = PartyGameReducer(rules, scoring)

    private val game = MutableStateFlow<PartyGameState?>(null)

    private val nowMs = MutableStateFlow(clock.nowMs())
    private var ticker: Job? = null

    private val effectChannel = Channel<PartyGameEffect>(Channel.BUFFERED)
    val effects: Flow<PartyGameEffect> = effectChannel.receiveAsFlow()

    val uiState: StateFlow<PartyGameUiState> = combine(game, nowMs) { state, now ->
        state?.toUiState(rules, scoring, now) ?: PartyGameUiState.Loading
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), PartyGameUiState.Loading)

    init {
        newGame()
    }

    fun onAction(action: PartyGameAction) {
        when (action) {
            PartyGameAction.StartTurn -> dispatch(PartyEvent.StartTurn(clock.nowMs()))
            is PartyGameAction.Answer -> dispatch(PartyEvent.Answer(action.correct, clock.nowMs()))
            PartyGameAction.NextTurn -> dispatch(PartyEvent.NextTurn)
            PartyGameAction.Pause -> dispatch(PartyEvent.Pause(clock.nowMs()))
            PartyGameAction.Resume -> dispatch(PartyEvent.Resume(clock.nowMs()))
            PartyGameAction.PlayAgain -> newGame()
            PartyGameAction.ExitToMenu -> viewModelScope.launch { effectChannel.send(PartyGameEffect.ExitToMenu) }
        }
    }

    private fun newGame() {
        viewModelScope.launch {
            game.value = startPartyGame(mode, teamNames, penaltyOnSkip, rules)
        }
    }

    private fun dispatch(event: PartyEvent) {
        val current = game.value ?: return
        val next = reducer.reduce(current, event)
        game.value = next
        nowMs.value = clock.nowMs()
        syncTicker(next)
    }

    private fun syncTicker(state: PartyGameState) {
        val playing = state.phase is PartyGameState.Phase.Playing
        if (playing && ticker == null) {
            ticker = viewModelScope.launch {
                while (isActive) {
                    delay(TICK_INTERVAL_MS)
                    dispatch(PartyEvent.Tick(clock.nowMs()))
                }
            }
        } else if (!playing) {
            ticker?.cancel()
            ticker = null
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(mode: PartyMode, teamNames: List<String>, penaltyOnSkip: Boolean): PartyGameViewModel
    }
}
