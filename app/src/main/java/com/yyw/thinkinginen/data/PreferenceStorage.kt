package com.yyw.thinkinginen.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.yyw.thinkinginen.constants.PreferenceKeys.CURRENT_EPISODE
import com.yyw.thinkinginen.constants.PreferenceKeys.CURRENT_SEASON
import com.yyw.thinkinginen.constants.PreferenceKeys.SCROLL_POSITION
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

interface PreferenceStorage {
    val onScrollPosition: Flow<Int>
    suspend fun scrollPosition(position: Int)
    val onCurrentSeason: Flow<Int>
    suspend fun currentSeason(season: Int)
    val onCurrentEpisode: Flow<Int>
    suspend fun currentEpisode(episode: Int)
}

@Singleton
class DataStorePreferenceStorage @Inject constructor(private val dataStore: DataStore<Preferences>) : PreferenceStorage {
    override val onScrollPosition: Flow<Int> = dataStore.data.map { it[SCROLL_POSITION] ?: 0 }

    override suspend fun scrollPosition(position: Int) {
        dataStore.edit { it[SCROLL_POSITION] = position }
    }

    override val onCurrentSeason: Flow<Int> = dataStore.data.map { it[CURRENT_SEASON] ?: 0 }

    override suspend fun currentSeason(season: Int) {
        dataStore.edit { it[CURRENT_SEASON] = season }
    }

    override val onCurrentEpisode: Flow<Int> = dataStore.data.map { it[CURRENT_EPISODE] ?: 0 }

    override suspend fun currentEpisode(episode: Int) {
        dataStore.edit { it[CURRENT_EPISODE] = episode }
    }
}