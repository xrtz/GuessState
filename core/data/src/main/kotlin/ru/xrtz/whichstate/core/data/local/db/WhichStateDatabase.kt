package ru.xrtz.whichstate.core.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import ru.xrtz.whichstate.core.data.local.db.dao.RoundDao
import ru.xrtz.whichstate.core.data.local.db.dao.StateProgressDao
import ru.xrtz.whichstate.core.data.local.db.entity.RoundAnswerEntity
import ru.xrtz.whichstate.core.data.local.db.entity.RoundEntity
import ru.xrtz.whichstate.core.data.local.db.entity.StateProgressEntity

@Database(
    entities = [RoundEntity::class, RoundAnswerEntity::class, StateProgressEntity::class],
    version = 1,
    exportSchema = true,
)
abstract class WhichStateDatabase : RoomDatabase() {
    abstract fun roundDao(): RoundDao
    abstract fun stateProgressDao(): StateProgressDao
}
