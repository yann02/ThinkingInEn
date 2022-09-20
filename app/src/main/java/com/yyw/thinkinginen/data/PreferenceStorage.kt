package com.yyw.thinkinginen.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.yyw.thinkinginen.constants.PreferenceKeys.SCROLL_POSITION
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

interface PreferenceStorage {
    val onScrollPosition: Flow<Int>
    suspend fun scrollPosition(position: Int)
}

@Singleton
class DataStorePreferenceStorage @Inject constructor(private val dataStore: DataStore<Preferences>) : PreferenceStorage {
    override val onScrollPosition: Flow<Int> = dataStore.data.map { it[SCROLL_POSITION] ?: 0 }

    override suspend fun scrollPosition(position: Int) {
        dataStore.edit { it[SCROLL_POSITION] = position }
    }
}