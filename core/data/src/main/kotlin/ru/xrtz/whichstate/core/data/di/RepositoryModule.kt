package ru.xrtz.whichstate.core.data.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.xrtz.whichstate.core.data.repository.AdSessionRepositoryImpl
import ru.xrtz.whichstate.core.data.repository.AndroidLanguageRepository
import ru.xrtz.whichstate.core.data.repository.CityRepositoryImpl
import ru.xrtz.whichstate.core.data.repository.PercentileRepositoryImpl
import ru.xrtz.whichstate.core.data.repository.ProgressRepositoryImpl
import ru.xrtz.whichstate.core.data.repository.RoundHistoryRepositoryImpl
import ru.xrtz.whichstate.core.domain.repository.AdSessionRepository
import ru.xrtz.whichstate.core.domain.repository.CityRepository
import ru.xrtz.whichstate.core.domain.repository.LanguageRepository
import ru.xrtz.whichstate.core.domain.repository.PercentileRepository
import ru.xrtz.whichstate.core.domain.repository.ProgressRepository
import ru.xrtz.whichstate.core.domain.repository.RoundHistoryRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindCityRepository(impl: CityRepositoryImpl): CityRepository

    @Binds
    @Singleton
    abstract fun bindLanguageRepository(impl: AndroidLanguageRepository): LanguageRepository

    @Binds
    @Singleton
    abstract fun bindRoundHistoryRepository(impl: RoundHistoryRepositoryImpl): RoundHistoryRepository

    @Binds
    @Singleton
    abstract fun bindProgressRepository(impl: ProgressRepositoryImpl): ProgressRepository

    @Binds
    abstract fun bindPercentileRepository(impl: PercentileRepositoryImpl): PercentileRepository

    @Binds
    @Singleton
    abstract fun bindAdSessionRepository(impl: AdSessionRepositoryImpl): AdSessionRepository
}
