package com.test.core.data.di

import com.test.core.data.PostsRepositoryImpl
import com.test.core.domain.repository.PostsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface DataModule {

    @Binds
    fun bindsPostsRepository(repository: PostsRepositoryImpl): PostsRepository
}