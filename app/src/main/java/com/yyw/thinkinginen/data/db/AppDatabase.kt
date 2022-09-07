package com.yyw.thinkinginen.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.yyw.thinkinginen.entities.Episode
import com.yyw.thinkinginen.entities.Message
import com.yyw.thinkinginen.entities.Season

@Database(
    entities = [Season::class, Episode::class, Message::class],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun seasonDao(): SeasonDao
    abstract fun episodeDao(): EpisodeDao
    abstract fun messageDao(): MessageDao

    companion object {
        private const val databaseName = "peppa"

        fun buildDatabase(context: Context): AppDatabase {
            return Room.databaseBuilder(context, AppDatabase::class.java, databaseName).build()
        }
    }
}