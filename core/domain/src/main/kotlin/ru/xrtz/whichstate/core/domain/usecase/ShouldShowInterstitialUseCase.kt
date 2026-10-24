package ru.xrtz.whichstate.core.domain.usecase

import ru.xrtz.whichstate.core.domain.policy.AdFrequencyPolicy
import ru.xrtz.whichstate.core.domain.repository.AdSessionRepository
import javax.inject.Inject

class ShouldShowInterstitialUseCase @Inject constructor(
    private val adSessionRepository: AdSessionRepository,
    private val adFrequencyPolicy: AdFrequencyPolicy,
) {
    operator fun invoke(): Boolean = adFrequencyPolicy.shouldShow(adSessionRepository.roundsSinceInterstitial())
}
