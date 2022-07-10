package com.yyw.thinkinginen.di

import android.content.Context
import android.content.SharedPreferences
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.preferencesDataStore
import com.yyw.thinkinginen.constants.PreferenceKeys.NAME
import com.yyw.thinkinginen.data.DataStorePreferenceStorage
import com.yyw.thinkinginen.data.PreferenceStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideIoDispatcher() = Dispatchers.IO

    /**
     * 初始化datastore
     * [DataStore]：本地缓存类，用于替代[SharedPreferences]
     */
    val Context.dataStore by preferencesDataStore(NAME)

    @Singleton
    @Provides
    fun providePreferenceStorage(@ApplicationContext context: Context): PreferenceStorage =
        DataStorePreferenceStorage(context.dataStore)
}