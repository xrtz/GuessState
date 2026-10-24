package ru.xrtz.whichstate.core.data.asset

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import ru.xrtz.whichstate.core.data.asset.dto.CityDto
import ru.xrtz.whichstate.core.data.mapper.toDomain
import ru.xrtz.whichstate.core.domain.model.City
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CitiesAssetDataSource @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private val json = Json { ignoreUnknownKeys = true }
    private val mutex = Mutex()

    @Volatile
    private var cached: List<City>? = null

    suspend fun cities(): List<City> {
        cached?.let { return it }
        return mutex.withLock {
            cached?.let { return@withLock it }
            val text = withContext(Dispatchers.IO) {
                context.assets.open(ASSET_FILE_NAME).bufferedReader().use { it.readText() }
            }
            val cities = withContext(Dispatchers.Default) {
                json.decodeFromString<List<CityDto>>(text).map { it.toDomain() }
            }
            cached = cities
            cities
        }
    }

    private companion object {
        const val ASSET_FILE_NAME = "cities.json"
    }
}
