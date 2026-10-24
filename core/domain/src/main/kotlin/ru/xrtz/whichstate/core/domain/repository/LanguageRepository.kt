package ru.xrtz.whichstate.core.domain.repository

import ru.xrtz.whichstate.core.domain.model.AppLanguage

interface LanguageRepository {
    fun current(): AppLanguage
    fun set(language: AppLanguage)
}
