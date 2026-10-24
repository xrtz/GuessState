package ru.xrtz.whichstate.core.domain.usecase

import ru.xrtz.whichstate.core.domain.repository.AdSessionRepository
import javax.inject.Inject

class RecordInterstitialShownUseCase @Inject constructor(private val adSessionRepository: AdSessionRepository) {
    operator fun invoke() = adSessionRepository.onInterstitialShown()
}
