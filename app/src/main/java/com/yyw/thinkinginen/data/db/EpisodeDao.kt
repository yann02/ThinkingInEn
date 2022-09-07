package com.yyw.thinkinginen.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.yyw.thinkinginen.entities.Episode
import com.yyw.thinkinginen.entities.EpisodeWithMessages
import com.yyw.thinkinginen.entities.SeasonWithEpisodeAndMessages
import kotlinx.coroutines.flow.Flow

@Dao
interface EpisodeDao {
    @Insert
    suspend fun insertAll(messages: List<Episode>): List<Long>

    @Query("DELETE FROM Episode")
    suspend fun deleteAll()
}