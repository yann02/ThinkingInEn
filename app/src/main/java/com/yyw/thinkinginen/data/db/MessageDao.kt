package com.yyw.thinkinginen.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.yyw.thinkinginen.entities.Message
import kotlinx.coroutines.flow.Flow

@Dao
interface MessageDao {
    @Insert
    suspend fun insertAll(messages: List<Message>): List<Long>

    @Query("SELECT * FROM Message")
    fun loadAll(): Flow<List<Message>>
}