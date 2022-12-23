package com.test.core.domain.usecase

import com.test.core.domain.repository.PostsRepository
import com.test.core.model.Post
import javax.inject.Inject

class ClearDeletedPostUseCase @Inject constructor(
    private val repository: PostsRepository
) {

    suspend operator fun invoke(post: Post) {
        repository.clearDeletedPostById(post)
    }
}