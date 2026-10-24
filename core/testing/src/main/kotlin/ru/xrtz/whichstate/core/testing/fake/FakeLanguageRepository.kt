package ru.xrtz.whichstate.core.testing.fake

import ru.xrtz.whichstate.core.domain.model.AppLanguage
import ru.xrtz.whichstate.core.domain.repository.LanguageRepository

class FakeLanguageRepository(initial: AppLanguage = AppLanguage.ENGLISH) : LanguageRepository {
    private var language = initial

    override fun current(): AppLanguage = language

    override fun set(language: AppLanguage) {
        this.language = language
    }
}
