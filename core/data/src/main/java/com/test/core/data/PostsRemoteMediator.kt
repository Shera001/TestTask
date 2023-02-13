package com.test.core.data

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.test.core.common.util.*
import com.test.core.database.dao.PostDao
import com.test.core.database.entity.PostEntity
import com.test.core.network.PostsApiService
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@OptIn(ExperimentalPagingApi::class)
class PostsRemoteMediator @Inject constructor(
    private val dao: PostDao,
    private val apiService: PostsApiService,
    @IODispatcher private val ioDispatcher: CoroutineDispatcher,
    @DefaultDispatcher private val defaultDispatcher: CoroutineDispatcher
) : RemoteMediator<Int, PostEntity>() {

    private var pageIndex = 0

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, PostEntity>
    ): MediatorResult {

        pageIndex =
            getPageIndex(loadType) ?: return MediatorResult.Success(endOfPaginationReached = true)

        val pageSize = state.config.pageSize
        val page = pageIndex * pageSize
        return try {
            withContext(ioDispatcher) {
                val posts = fetchPosts(page, pageSize)
                launch {
                    if (loadType == LoadType.REFRESH) {
                        dao.refresh(posts)
                    } else {
                        dao.insertAll(posts)
                    }
                }
                MediatorResult.Success(
                    endOfPaginationReached = posts.size < pageSize
                )
            }
        } catch (e: Exception) {
            MediatorResult.Error(e)
        }
    }

    private fun getPageIndex(loadType: LoadType): Int? {
        pageIndex = when (loadType) {
            LoadType.REFRESH -> 0
            LoadType.PREPEND -> return null
            LoadType.APPEND -> ++pageIndex
        }
        return pageIndex
    }

    private suspend fun fetchPosts(
        page: Int,
        pageSize: Int
    ): List<PostEntity> = withContext(defaultDispatcher) {
        try {
            val deletedPosts = async {
                dao.getAllDeletedPosts()
            }
            val newPosts = async {
                apiService.getPosts(page, pageSize)
            }
            when (val result = newPosts.await()) {
                is ApiSuccess -> {
                    result.data.filter { predicate ->
                        predicate.id !in deletedPosts.await().map { it.id }
                    }
                        .map { post -> PostEntity(post) }
                }
                is ApiError -> {
                    emptyList()
                }
                is ApiException -> {
                    emptyList()
                }
            }
        } catch (e: Exception) {
            emptyList()
        }
    }
}