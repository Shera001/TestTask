package com.test.core.domain.usecase

import androidx.paging.PagingData
import com.test.core.domain.repository.PostsRepository
import com.test.core.model.Post
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetPostsUseCase @Inject constructor(
    private val repository: PostsRepository
) {

    operator fun invoke(): Flow<PagingData<Post>> {
        return repository.getPosts()
    }
}