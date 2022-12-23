package com.test.feature.posts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.test.core.common.util.Result
import com.test.core.domain.usecase.ClearDeletedPostUseCase
import com.test.core.domain.usecase.DeletePostUseCase
import com.test.core.domain.usecase.GetPostsUseCase
import com.test.core.model.Post
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject


@HiltViewModel
class PostsViewModel @Inject constructor(
    getPostsUseCase: GetPostsUseCase,
    private val deletePostUseCase: DeletePostUseCase,
    private val clearDeletedPostUseCase: ClearDeletedPostUseCase
) : ViewModel() {

    var posts: Flow<PagingData<Post>> = getPostsUseCase()
        .cachedIn(viewModelScope)

    private val _result = MutableStateFlow(Result.Nothing)
    val result: StateFlow<Result> = _result.asStateFlow()

    suspend fun deletePost(id: Int) {
        _result.tryEmit(deletePostUseCase(id))
    }

    suspend fun clearDeletedPost(post: Post) {
        clearDeletedPostUseCase(post)
    }
}