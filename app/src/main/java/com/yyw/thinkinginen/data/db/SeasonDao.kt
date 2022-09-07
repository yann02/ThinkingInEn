package com.yyw.thinkinginen.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.yyw.thinkinginen.entities.Season
import com.yyw.thinkinginen.entities.SeasonWithEpisodeAndMessages
import kotlinx.coroutines.flow.Flow

@Dao
interface SeasonDao {
    @Insert
    suspend fun insertAll(messages: List<Season>): List<Long>

    @Query("DELETE FROM Season")
    suspend fun deleteAll()

    @Transaction
    @Query("SELECT * FROM Season")
    fun loadAll(): Flow<List<SeasonWithEpisodeAndMessages>>
}