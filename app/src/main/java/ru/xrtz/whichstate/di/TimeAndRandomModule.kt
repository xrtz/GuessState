package ru.xrtz.whichstate.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.xrtz.whichstate.core.domain.time.GameClock
import kotlin.random.Random

@Module
@InstallIn(SingletonComponent::class)
internal object TimeAndRandomModule {
    @Provides
    fun provideGameClock(): GameClock = GameClock { System.currentTimeMillis() }

    @Provides
    fun provideRandom(): Random = Random.Default
}
