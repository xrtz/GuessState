package ru.xrtz.whichstate.core.data.local.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "rounds")
data class RoundEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val mode: String,
    val level: String,
    val startedAtMs: Long,
    val durationMs: Long,
    val correct: Int,
    val wrong: Int,
    val syncedAtMs: Long? = null,
)
