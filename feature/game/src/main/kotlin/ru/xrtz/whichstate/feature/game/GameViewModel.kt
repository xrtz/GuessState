package ru.xrtz.whichstate.feature.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import ru.xrtz.whichstate.core.domain.game.ClassicRoundReducer
import ru.xrtz.whichstate.core.domain.game.ClassicRoundState
import ru.xrtz.whichstate.core.domain.game.RoundEvent
import ru.xrtz.whichstate.core.domain.game.RoundRules
import ru.xrtz.whichstate.core.domain.model.Level
import ru.xrtz.whichstate.core.domain.model.RoundResult
import ru.xrtz.whichstate.core.domain.policy.AnswerJudge
import ru.xrtz.whichstate.core.domain.time.GameClock
import ru.xrtz.whichstate.core.domain.usecase.FinishClassicRoundUseCase
import ru.xrtz.whichstate.core.domain.usecase.StartClassicRoundUseCase

private const val TICK_INTERVAL_MS = 100L

@HiltViewModel(assistedFactory = GameViewModel.Factory::class)
class GameViewModel @AssistedInject constructor(
    @Assisted private val level: Level,
    private val startClassicRound: StartClassicRoundUseCase,
    private val finishClassicRound: FinishClassicRoundUseCase,
    private val clock: GameClock,
) : ViewModel() {
    private val rules = RoundRules()
    private val reducer = ClassicRoundReducer(AnswerJudge(), rules)

    private val roundState = MutableStateFlow<ClassicRoundState?>(null)

    private val nowMs = MutableStateFlow(clock.nowMs())
    private val effectChannel = Channel<GameEffect>(Channel.BUFFERED)
    val effects: Flow<GameEffect> = effectChannel.receiveAsFlow()

    val uiState: StateFlow<GameUiState> = combine(roundState, nowMs) { round, now ->
        round?.toUiState(rules, now) ?: GameUiState()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), GameUiState())

    init {
        viewModelScope.launch {
            val questions = startClassicRound(level)
            val first = questions.firstOrNull() ?: return@launch
            roundState.value = ClassicRoundState.start(first, questions.drop(1), clock.nowMs())
            runTicker()
        }
    }

    fun onAction(action: GameAction) {
        when (action) {
            is GameAction.TapState -> dispatch(RoundEvent.Tap(action.state, clock.nowMs()))
            GameAction.Pause -> dispatch(RoundEvent.Pause(clock.nowMs()))
            GameAction.Resume -> dispatch(RoundEvent.Resume(clock.nowMs()))
            GameAction.ExitToMenu -> sendEffect(GameEffect.ExitToMenu)
        }
    }

    private fun dispatch(event: RoundEvent) {
        val current = roundState.value ?: return
        roundState.value = reducer.reduce(current, event)
        nowMs.value = clock.nowMs()
    }

    private fun sendEffect(effect: GameEffect) {
        viewModelScope.launch { effectChannel.send(effect) }
    }

    private suspend fun runTicker() {
        while (true) {
            delay(TICK_INTERVAL_MS)
            val current = roundState.value ?: continue
            val now = clock.nowMs()
            val next = reducer.reduce(current, RoundEvent.Tick(now))
            roundState.value = next
            nowMs.value = now
            if (next.phase is ClassicRoundState.Phase.Finished) {
                finishRound(next)
                return
            }
        }
    }

    private suspend fun finishRound(state: ClassicRoundState) {
        val result = RoundResult(
            level = level,
            answers = state.answers,
            startedAtMs = state.startedAtMs,
            durationMs = rules.duration.inWholeMilliseconds,
        )
        val roundId = finishClassicRound(result)
        effectChannel.send(GameEffect.OpenResults(roundId))
    }

    @AssistedFactory
    interface Factory {
        fun create(level: Level): GameViewModel
    }
}
