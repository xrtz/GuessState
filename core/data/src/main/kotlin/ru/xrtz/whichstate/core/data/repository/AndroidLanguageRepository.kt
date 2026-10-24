package ru.xrtz.whichstate.core.data.repository

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import ru.xrtz.whichstate.core.domain.model.AppLanguage
import ru.xrtz.whichstate.core.domain.repository.LanguageRepository
import javax.inject.Inject

internal class AndroidLanguageRepository @Inject constructor() : LanguageRepository {
    override fun current(): AppLanguage {
        val tag = AppCompatDelegate.getApplicationLocales().takeIf { !it.isEmpty }?.get(0)?.language
        return AppLanguage.entries.firstOrNull { it.tag == tag } ?: AppLanguage.ENGLISH
    }

    override fun set(language: AppLanguage) {
        AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(language.tag))
    }
}
