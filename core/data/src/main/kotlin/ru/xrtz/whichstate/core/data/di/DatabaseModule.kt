package ru.xrtz.whichstate.core.data.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ru.xrtz.whichstate.core.data.local.db.WhichStateDatabase
import ru.xrtz.whichstate.core.data.local.db.dao.RoundDao
import ru.xrtz.whichstate.core.data.local.db.dao.StateProgressDao
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): WhichStateDatabase =
        Room.databaseBuilder(context, WhichStateDatabase::class.java, "which_state.db").build()

    @Provides
    fun provideRoundDao(database: WhichStateDatabase): RoundDao = database.roundDao()

    @Provides
    fun provideStateProgressDao(database: WhichStateDatabase): StateProgressDao = database.stateProgressDao()
}
