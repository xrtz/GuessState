package ru.xrtz.whichstate.core.domain.usecase

import ru.xrtz.whichstate.core.domain.model.AppLanguage
import ru.xrtz.whichstate.core.domain.repository.LanguageRepository
import javax.inject.Inject

class GetCurrentLanguageUseCase @Inject constructor(private val languageRepository: LanguageRepository) {
    operator fun invoke(): AppLanguage = languageRepository.current()
}
