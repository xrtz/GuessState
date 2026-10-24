package ru.xrtz.whichstate.core.data.local.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "state_progress")
data class StateProgressEntity(
    @PrimaryKey val state: String,
    val correctCount: Int,
    val wrongCount: Int,
    val firstMasteredAtMs: Long?,
)
