package ru.xrtz.whichstate.core.ads.yandex.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.xrtz.whichstate.core.ads.BannerAds
import ru.xrtz.whichstate.core.ads.InterstitialAds
import ru.xrtz.whichstate.core.ads.yandex.AndroidBannerAds
import ru.xrtz.whichstate.core.ads.yandex.AndroidInterstitialAds
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class AdsModule {
    @Binds
    @Singleton
    abstract fun bindInterstitialAds(impl: AndroidInterstitialAds): InterstitialAds

    @Binds
    @Singleton
    abstract fun bindBannerAds(impl: AndroidBannerAds): BannerAds
}
