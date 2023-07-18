package com.yyw.thinkinginen.di

import com.yyw.thinkinginen.repositories.MainRepository
import com.yyw.thinkinginen.repositories.OfflineFirstMainRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface DataModule {
    @Binds
    fun bindsMainRepository(
        mainRepository: OfflineFirstMainRepository,
    ): MainRepository
}