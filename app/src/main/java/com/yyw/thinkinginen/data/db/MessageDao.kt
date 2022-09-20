package com.yyw.thinkinginen.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.yyw.thinkinginen.entities.Message

@Dao
interface MessageDao {
    @Insert
    suspend fun insertAll(messages: List<Message>): List<Long>

    @Update
    suspend fun updateMessages(vararg msg: Message)

    @Query("DELETE FROM Message")
    suspend fun deleteAll()
}