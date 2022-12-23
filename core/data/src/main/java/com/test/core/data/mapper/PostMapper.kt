package com.test.core.data.mapper

import com.test.core.database.entity.PostEntity
import com.test.core.model.Post

fun Post.toPostEntity(): PostEntity = PostEntity(
    id = id,
    userId = userId,
    title = title,
    body = body
)