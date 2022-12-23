package com.test.core.data

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.test.core.model.Post
import com.test.core.network.PostsApiService
import javax.inject.Inject

class PostsPagingSource @Inject constructor(
    private val apiService: PostsApiService
) : PagingSource<Int, Post>() {

    override fun getRefreshKey(state: PagingState<Int, Post>): Int? {
        val position = state.anchorPosition ?: return null
        val page = state.closestPageToPosition(position) ?: return null
        return page.prevKey?.plus(1) ?: page.nextKey?.minus(1)
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Post> {
        val page: Int = params.key ?: 1
        val pageSize: Int = params.loadSize
        try {
            val posts = checkNotNull(
                apiService.getPosts(page, pageSize)
            )
            val nextKey = if (posts.size < pageSize) null else (page + 1)
            val prevKey = if (page == 1) null else (page - 1)
            return LoadResult.Page(posts, prevKey, nextKey)
        } catch (e: Exception) {
            Log.e("TAG", "load: ", e.cause)
            return LoadResult.Error(e.cause ?: error("exception"))
        }
    }
}