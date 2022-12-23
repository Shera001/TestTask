package com.test.core.model

data class Post(
    override val userId: Int,
    override val id: Int,
    override val title: String,
    override val body: String
) : PostModel {

    constructor(postModel: PostModel) : this(
        id = postModel.id,
        userId = postModel.userId,
        title = postModel.title,
        body = postModel.body
    )
}
