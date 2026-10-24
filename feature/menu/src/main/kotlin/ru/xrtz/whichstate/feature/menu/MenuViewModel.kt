package ru.xrtz.whichstate.feature.menu

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import ru.xrtz.whichstate.core.domain.usecase.GetCurrentLanguageUseCase
import ru.xrtz.whichstate.core.domain.usecase.GetLevelsUseCase
import ru.xrtz.whichstate.core.domain.usecase.ObserveMasteryUseCase
import ru.xrtz.whichstate.core.domain.usecase.ToggleLanguageUseCase
import javax.inject.Inject

@HiltViewModel
class MenuViewModel @Inject constructor(
    getLevels: GetLevelsUseCase,
    observeMastery: ObserveMasteryUseCase,
    getCurrentLanguage: GetCurrentLanguageUseCase,
    private val toggleLanguage: ToggleLanguageUseCase,
) : ViewModel() {
    private val effectChannel = Channel<MenuEffect>(Channel.BUFFERED)
    val effects: Flow<MenuEffect> = effectChannel.receiveAsFlow()

    private val language = MutableStateFlow(getCurrentLanguage())

    val uiState: StateFlow<MenuUiState> = combine(
        flow { emit(getLevels()) },
        observeMastery(),
        language,
        ::toMenuUiState,
    ).stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), MenuUiState())

    fun onAction(action: MenuAction) {
        when (action) {
            is MenuAction.SelectLevel -> sendEffect(MenuEffect.OpenLevel(action.level))
            is MenuAction.SelectPartyMode -> sendEffect(MenuEffect.OpenParty(action.mode))
            MenuAction.ToggleLanguage -> language.value = toggleLanguage()
            MenuAction.OpenHistory -> sendEffect(MenuEffect.OpenHistory)
        }
    }

    private fun sendEffect(effect: MenuEffect) {
        viewModelScope.launch { effectChannel.send(effect) }
    }
}
