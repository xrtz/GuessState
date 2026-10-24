package ru.xrtz.whichstate.core.domain.usecase

import ru.xrtz.whichstate.core.domain.model.HistoryAttempt
import ru.xrtz.whichstate.core.domain.model.Level
import ru.xrtz.whichstate.core.domain.model.LevelHistory
import ru.xrtz.whichstate.core.domain.model.RoundHistoryEntry
import ru.xrtz.whichstate.core.domain.policy.PercentileTable
import ru.xrtz.whichstate.core.domain.repository.RoundHistoryRepository
import javax.inject.Inject

class GetHistoryUseCase @Inject constructor(
    private val roundHistoryRepository: RoundHistoryRepository,
    private val percentileTable: PercentileTable,
) {
    suspend operator fun invoke(): List<LevelHistory> = Level.entries.map { level ->
        LevelHistory(
            level = level,
            best = roundHistoryRepository.bestForLevel(level)?.toAttempt(),
            recent = roundHistoryRepository.recentForLevel(level, RECENT_LIMIT).map { it.toAttempt() },
        )
    }

    private fun RoundHistoryEntry.toAttempt() = HistoryAttempt(
        roundId = roundId,
        level = level,
        correct = correct,
        wrong = wrong,
        percentile = percentileTable.percentile(level, correct),
        playedAtMs = playedAtMs,
    )

    private companion object {
        const val RECENT_LIMIT = 100
    }
}
