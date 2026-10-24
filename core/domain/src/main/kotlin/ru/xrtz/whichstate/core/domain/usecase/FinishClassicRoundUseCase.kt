package ru.xrtz.whichstate.core.domain.usecase

import ru.xrtz.whichstate.core.domain.model.RoundId
import ru.xrtz.whichstate.core.domain.model.RoundResult
import ru.xrtz.whichstate.core.domain.repository.AdSessionRepository
import ru.xrtz.whichstate.core.domain.repository.RoundHistoryRepository
import javax.inject.Inject

class FinishClassicRoundUseCase @Inject constructor(
    private val roundHistoryRepository: RoundHistoryRepository,
    private val adSessionRepository: AdSessionRepository,
) {
    suspend operator fun invoke(result: RoundResult): RoundId {
        val roundId = roundHistoryRepository.saveFinished(result)
        adSessionRepository.onRoundFinished()
        return roundId
    }
}
