package ru.xrtz.whichstate.feature.results.di

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.xrtz.whichstate.core.domain.policy.AdFrequencyPolicy
import ru.xrtz.whichstate.feature.results.share.AndroidShareCardRenderer
import ru.xrtz.whichstate.feature.results.share.ShareCardRenderer

@Module
@InstallIn(SingletonComponent::class)
internal abstract class ResultsModule {
    @Binds
    abstract fun bindShareCardRenderer(impl: AndroidShareCardRenderer): ShareCardRenderer

    companion object {
        @Provides
        fun provideAdFrequencyPolicy(): AdFrequencyPolicy = AdFrequencyPolicy()
    }
}
