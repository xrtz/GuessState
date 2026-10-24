package ru.xrtz.whichstate.core.ads.yandex

import android.content.Context
import com.yandex.mobile.ads.common.InitializationListener
import com.yandex.mobile.ads.common.YandexAds
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class YandexAdsInitializer @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private val started = AtomicBoolean(false)
    private val ready = CompletableDeferred<Unit>()

    suspend fun awaitReady() {
        if (started.compareAndSet(false, true)) {
            withContext(Dispatchers.Default) {
                YandexAds.initialize(
                    context,
                    object : InitializationListener {
                        override fun onInitializationCompleted() {
                            ready.complete(Unit)
                        }
                    },
                )
            }
        }
        ready.await()
    }
}
