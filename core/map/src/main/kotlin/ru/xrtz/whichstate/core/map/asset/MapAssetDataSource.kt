package ru.xrtz.whichstate.core.map.asset

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import ru.xrtz.whichstate.core.map.geometry.MapGeometry

class MapAssetDataSource(private val context: Context) {
    private val json = Json { ignoreUnknownKeys = true }
    private val mutex = Mutex()

    @Volatile
    private var cached: MapGeometry? = null

    suspend fun geometry(): MapGeometry {
        cached?.let { return it }
        return mutex.withLock {
            cached?.let { return@withLock it }
            val text = withContext(Dispatchers.IO) {
                context.assets.open(ASSET_FILE_NAME).bufferedReader().use { it.readText() }
            }
            val geometry = withContext(Dispatchers.Default) {
                json.decodeFromString(MapAssetJson.serializer(), text).toGeometry()
            }
            cached = geometry
            geometry
        }
    }

    private companion object {
        const val ASSET_FILE_NAME = "us_states_albers.json"
    }
}
