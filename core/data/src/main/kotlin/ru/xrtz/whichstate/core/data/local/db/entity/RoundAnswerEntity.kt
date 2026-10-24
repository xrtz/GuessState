package ru.xrtz.whichstate.core.data.local.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "round_answers",
    foreignKeys = [
        ForeignKey(
            entity = RoundEntity::class,
            parentColumns = ["id"],
            childColumns = ["roundId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("roundId")],
)
data class RoundAnswerEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val roundId: Long,
    val position: Int,
    val cityId: String,
    val pickedState: String,
    val verdict: String,
    val atMs: Long,
)
