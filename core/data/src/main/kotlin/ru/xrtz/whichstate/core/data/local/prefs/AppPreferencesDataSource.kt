package ru.xrtz.whichstate.core.data.local.prefs

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "which_state_prefs")

@Singleton
class AppPreferencesDataSource @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    val isFirstLaunch: Flow<Boolean> =
        context.dataStore.data.map { prefs -> prefs[Keys.FIRST_LAUNCH_DONE] != true }

    suspend fun markLaunched() {
        context.dataStore.edit { prefs -> prefs[Keys.FIRST_LAUNCH_DONE] = true }
    }

    private object Keys {
        val FIRST_LAUNCH_DONE = booleanPreferencesKey("first_launch_done")
    }
}
