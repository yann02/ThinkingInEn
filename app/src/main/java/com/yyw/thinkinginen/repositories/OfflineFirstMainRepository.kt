package com.yyw.thinkinginen.repositories

import android.content.Context
import com.yyw.thinkinginen.data.db.AppDatabase
import com.yyw.thinkinginen.entities.ResData
import com.yyw.thinkinginen.utils.Synchronizer
import com.yyw.thinkinginen.utils.dataSync
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class OfflineFirstMainRepository @Inject constructor(
    private val appDatabase: AppDatabase,
    @ApplicationContext private val context: Context
) : MainRepository {
    override suspend fun syncWith(synchronizer: Synchronizer): Boolean = synchronizer.dataSync(
        context = context,
        deleteAll = ::clearAll,
        insertAll = ::insertAll,
    )

    private suspend fun clearAll() {
        appDatabase.seasonDao().deleteAll()
        appDatabase.episodeDao().deleteAll()
        appDatabase.messageDao().deleteAll()
    }

    private suspend fun insertAll(data: ResData) {
        val (seasons, episodes, messages) = data
        appDatabase.seasonDao().insertAll(seasons)
        appDatabase.episodeDao().insertAll(episodes)
        appDatabase.messageDao().insertAll(messages)
    }
}