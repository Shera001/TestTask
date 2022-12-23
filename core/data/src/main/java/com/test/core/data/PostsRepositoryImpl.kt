package com.test.core.data

import android.util.Log
import androidx.paging.*
import com.test.core.common.util.Result
import com.test.core.data.mapper.toPostEntity
import com.test.core.database.dao.PostDao
import com.test.core.database.entity.DeletedPostEntity
import com.test.core.domain.repository.PostsRepository
import com.test.core.model.Post
import com.test.core.network.PostsApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class PostsRepositoryImpl @Inject constructor(
    private val postDao: PostDao,
    private val apiService: PostsApiService,
    private val postsPagingSource: PostsPagingSource,
    private val postsRemoteMediator: PostsRemoteMediator
) : PostsRepository {

    @OptIn(ExperimentalPagingApi::class)
    override fun getPosts(): Flow<PagingData<Post>> {
        return Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE
            ),
            remoteMediator = postsRemoteMediator
        ) {
            postDao.getPagingSource()
        }
            .flow
            .map { pagingData ->
                pagingData.map { postEntity -> Post(postEntity) }
            }
    }

    override suspend fun deletePost(id: Int): Result {
        return try {
            postDao.deletePostById(id)
            val x = DeletedPostEntity(id = id)
            postDao.addDeletedPost(x)
            Log.e(TAG, "deletePost: $x")
            apiService.deletePost(id)
            Result.Success
        } catch (e: Exception) {
            Log.e(TAG, "deletePost: ${e.localizedMessage}", e.cause)
            Result.Error
        }
    }

    override suspend fun addPost(title: String, body: String): Result {
        return try {
            val newPost = apiService.addPost(title, body)
            postDao.insert(newPost.toPostEntity())
            Result.Success
        } catch (e: Exception) {
            Log.e(TAG, "addPost: ${e.localizedMessage}", e.cause)
            Result.Error
        }
    }

    override suspend fun editPost(post: Post): Result {
        return try {
            apiService.editPost(post.id, post)
            postDao.update(post.toPostEntity())
            Result.Success
        } catch (e: Exception) {
            Log.e(TAG, "editPost: ${e.localizedMessage}", e.cause)
            Result.Error
        }
    }

    override suspend fun clearDeletedPostById(post: Post) {
        postDao.clearDeletedPostById(post.id)
        postDao.insert(post.toPostEntity())
    }

    private companion object {
        const val PAGE_SIZE = 10
        const val TAG = "PostsRepositoryImpl"
    }
}