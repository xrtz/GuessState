package ru.xrtz.whichstate.core.domain.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.xrtz.whichstate.core.domain.model.MasteryProgress
import ru.xrtz.whichstate.core.domain.repository.ProgressRepository
import javax.inject.Inject

class ObserveMasteryUseCase @Inject constructor(private val progressRepository: ProgressRepository) {
    operator fun invoke(): Flow<MasteryProgress> =
        progressRepository.observeMastered().map { mastered -> MasteryProgress(mastered) }
}
