package com.yyw.thinkinginen.di

import android.content.Context
import android.content.SharedPreferences
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import com.yyw.thinkinginen.constants.PreferenceKeys.NAME
import com.yyw.thinkinginen.data.DataStorePreferenceStorage
import com.yyw.thinkinginen.data.PreferenceStorage
import com.yyw.thinkinginen.data.db.AppDatabase
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

    //    @Singleton
//    @Provides
//    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
//        return AppDatabase.buildDatabase(context)
//    }
    @Singleton
    @Provides
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase = Room.databaseBuilder(
        context,
        AppDatabase::class.java,
        "peppa-db"
    ).build()
}