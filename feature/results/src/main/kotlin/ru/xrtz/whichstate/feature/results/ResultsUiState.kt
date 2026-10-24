package ru.xrtz.whichstate.feature.results

import ru.xrtz.whichstate.core.designsystem.components.ResultRowVariant
import ru.xrtz.whichstate.core.domain.model.Level
import ru.xrtz.whichstate.core.domain.model.UsState

sealed interface ResultsUiState {
    data object Loading : ResultsUiState

    data class Loaded(
        val level: Level,
        val rankTier: Int,
        val rankIndex: Int,
        val correct: Int,
        val total: Int,
        val percentile: Int,

        val shameCityName: String?,
        val shamePickedState: String?,
        val accuracyByState: Map<UsState, Float>,
        val wrong: Int,
        val masteredCount: Int,
        val totalStates: Int,
        val cities: List<ResultRowUiModel>,
        val isSharing: Boolean = false,
    ) : ResultsUiState
}

data class ResultRowUiModel(val cityName: String, val stateText: String, val variant: ResultRowVariant)
