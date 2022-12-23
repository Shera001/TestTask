package com.test.core.database.di

import com.test.core.database.AppDatabase
import com.test.core.database.dao.PostDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object DaosModule {

    @Provides
    fun providePostDao(appDatabase: AppDatabase): PostDao = appDatabase.postDao()
}