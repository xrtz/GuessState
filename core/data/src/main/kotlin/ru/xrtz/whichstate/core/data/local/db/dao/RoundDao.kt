package ru.xrtz.whichstate.core.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import ru.xrtz.whichstate.core.data.local.db.entity.RoundAnswerEntity
import ru.xrtz.whichstate.core.data.local.db.entity.RoundEntity

@Dao
interface RoundDao {
    @Insert
    suspend fun insertRound(round: RoundEntity): Long

    @Insert
    suspend fun insertAnswers(answers: List<RoundAnswerEntity>)

    @Query("SELECT * FROM rounds WHERE id = :roundId")
    suspend fun getRound(roundId: Long): RoundEntity?

    @Query("SELECT * FROM round_answers WHERE roundId = :roundId ORDER BY position ASC")
    suspend fun getAnswers(roundId: Long): List<RoundAnswerEntity>

    @Query(
        "SELECT * FROM rounds WHERE mode = :mode AND level = :level " +
            "ORDER BY correct DESC, wrong ASC, startedAtMs DESC LIMIT 1",
    )
    suspend fun getBestForLevel(mode: String, level: String): RoundEntity?

    @Query("SELECT * FROM rounds WHERE mode = :mode AND level = :level ORDER BY startedAtMs DESC LIMIT :limit")
    suspend fun getRecentForLevel(mode: String, level: String, limit: Int): List<RoundEntity>
}
