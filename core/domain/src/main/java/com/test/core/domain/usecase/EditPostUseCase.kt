package com.test.core.domain.usecase

import com.test.core.common.util.Result
import com.test.core.domain.repository.PostsRepository
import com.test.core.model.Post
import javax.inject.Inject

class EditPostUseCase @Inject constructor(
    private val repository: PostsRepository
) {

    suspend operator fun invoke(post: Post): Result {
        return if (post.body.isEmpty() || post.title.isEmpty()) {
            Result.Empty
        } else {
            repository.editPost(post)
        }
    }
}