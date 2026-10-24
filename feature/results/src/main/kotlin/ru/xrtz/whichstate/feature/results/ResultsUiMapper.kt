package ru.xrtz.whichstate.feature.results

import ru.xrtz.whichstate.core.designsystem.components.ResultRowVariant
import ru.xrtz.whichstate.core.domain.model.Answer
import ru.xrtz.whichstate.core.domain.model.RoundSummary
import ru.xrtz.whichstate.core.domain.model.Verdict

internal fun RoundSummary.toUiState(isSharing: Boolean = false): ResultsUiState.Loaded = ResultsUiState.Loaded(
    level = level,
    rankTier = rankTier,
    rankIndex = rankIndex,
    correct = correct,
    total = correct + wrong,
    percentile = percentile,
    shameCityName = worstMiss?.question?.city?.name,
    shamePickedState = worstMiss?.picked?.name,
    accuracyByState = accuracyByState,
    wrong = wrong,
    masteredCount = masteredCount,
    totalStates = totalStates,
    cities = answers.map { it.toRowUiModel() },
    isSharing = isSharing,
)

private fun Answer.toRowUiModel(): ResultRowUiModel {
    val correctState = question.city.state
    return when (verdict) {
        Verdict.CORRECT -> ResultRowUiModel(question.city.name, correctState.name, ResultRowVariant.CORRECT)
        Verdict.WRONG -> ResultRowUiModel(
            question.city.name,
            "${picked.name} → ${correctState.name}",
            ResultRowVariant.WRONG,
        )
        Verdict.NEAR_MISS -> ResultRowUiModel(
            question.city.name,
            "${picked.name} → ${correctState.name} ~",
            ResultRowVariant.NEAR_MISS,
        )
    }
}
