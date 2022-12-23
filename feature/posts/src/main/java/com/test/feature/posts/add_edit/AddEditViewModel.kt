package com.test.feature.posts.add_edit

import androidx.lifecycle.ViewModel
import com.test.core.common.util.Result
import com.test.core.domain.usecase.AddPostUseCase
import com.test.core.domain.usecase.EditPostUseCase
import com.test.core.model.Post
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class AddEditViewModel @Inject constructor(
    private val addPostUseCase: AddPostUseCase,
    private val editPostUseCase: EditPostUseCase
) : ViewModel() {

    private val _result = MutableStateFlow(Result.Nothing)
    val result: StateFlow<Result> = _result.asStateFlow()

    suspend fun addPost(title: String, body: String) {
        _result.tryEmit(addPostUseCase(title, body))
    }

    suspend fun editPost(post: Post) {
        _result.tryEmit(editPostUseCase(post))
    }
}