package ru.xrtz.whichstate.core.domain.usecase

import kotlinx.coroutines.flow.first
import ru.xrtz.whichstate.core.domain.model.RoundId
import ru.xrtz.whichstate.core.domain.model.RoundSummary
import ru.xrtz.whichstate.core.domain.policy.AccuracyCalculator
import ru.xrtz.whichstate.core.domain.policy.PercentileTable
import ru.xrtz.whichstate.core.domain.policy.RankPolicy
import ru.xrtz.whichstate.core.domain.policy.WorstMissPolicy
import ru.xrtz.whichstate.core.domain.repository.ProgressRepository
import ru.xrtz.whichstate.core.domain.repository.RoundHistoryRepository
import javax.inject.Inject

class GetRoundSummaryUseCase @Inject constructor(
    private val roundHistoryRepository: RoundHistoryRepository,
    private val progressRepository: ProgressRepository,
    private val percentileTable: PercentileTable,
    private val rankPolicy: RankPolicy,
    private val worstMissPolicy: WorstMissPolicy,
    private val accuracyCalculator: AccuracyCalculator,
) {
    suspend operator fun invoke(id: RoundId): RoundSummary {
        val result = roundHistoryRepository.get(id)
        val correct = result.correctCount
        val percentile = percentileTable.percentile(result.level, correct)
        val mastered = progressRepository.observeMastered().first()
        val rank = rankPolicy.rank(percentile, correct)

        return RoundSummary(
            level = result.level,
            correct = correct,
            wrong = result.wrongCount,
            percentile = percentile,
            rankTier = rank.tier,
            rankIndex = rank.index,
            worstMiss = worstMissPolicy.worstMiss(result.answers),
            accuracyByState = accuracyCalculator.accuracyByState(result.answers),
            masteredCount = mastered.size,
            answers = result.answers,
        )
    }
}
