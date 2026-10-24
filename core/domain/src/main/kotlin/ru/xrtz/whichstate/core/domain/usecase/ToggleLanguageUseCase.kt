package ru.xrtz.whichstate.core.domain.usecase

import ru.xrtz.whichstate.core.domain.model.AppLanguage
import ru.xrtz.whichstate.core.domain.repository.LanguageRepository
import javax.inject.Inject

class ToggleLanguageUseCase @Inject constructor(private val languageRepository: LanguageRepository) {
    operator fun invoke(): AppLanguage {
        val next = if (languageRepository.current() == AppLanguage.ENGLISH) AppLanguage.RUSSIAN else AppLanguage.ENGLISH
        languageRepository.set(next)
        return next
    }
}
