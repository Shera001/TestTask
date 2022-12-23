package com.test.core.domain.repository

import androidx.paging.PagingData
import com.test.core.common.util.Result
import com.test.core.model.Post
import kotlinx.coroutines.flow.Flow

interface PostsRepository {

    fun getPosts(): Flow<PagingData<Post>>

    suspend fun deletePost(id: Int): Result

    suspend fun addPost(title: String, body: String): Result

    suspend fun editPost(post: Post): Result

//    suspend fun addDeletedPost(id: Int)

    suspend fun clearDeletedPostById(post: Post)
}