package com.test.core.domain.usecase

import com.test.core.common.util.Result
import com.test.core.domain.repository.PostsRepository
import javax.inject.Inject

class DeletePostUseCase @Inject constructor(
    private val postsRepository: PostsRepository
) {

    suspend operator fun invoke(id: Int): Result = postsRepository.deletePost(id)
}