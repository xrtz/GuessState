package ru.xrtz.whichstate.core.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import ru.xrtz.whichstate.core.data.local.db.entity.StateProgressEntity

@Dao
interface StateProgressDao {
    @Query("SELECT * FROM state_progress WHERE state = :state")
    suspend fun get(state: String): StateProgressEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: StateProgressEntity)

    @Query("SELECT * FROM state_progress WHERE firstMasteredAtMs IS NOT NULL")
    fun observeMastered(): Flow<List<StateProgressEntity>>
}
