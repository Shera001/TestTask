package com.test.core.domain.usecase

import com.test.core.common.util.Result
import com.test.core.domain.repository.PostsRepository
import javax.inject.Inject

class AddPostUseCase @Inject constructor(
    private val repository: PostsRepository
) {

    suspend operator fun invoke(title: String, body: String): Result {
        if (title.isEmpty() || body.isEmpty()) {
            return Result.Empty
        }
        return repository.addPost(title, body)
    }
}