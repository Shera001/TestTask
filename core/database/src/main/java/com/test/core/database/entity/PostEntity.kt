package com.test.core.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.test.core.model.PostModel

@Entity(tableName = "posts")
data class PostEntity(
    @PrimaryKey
    override val id: Int,
    override val userId: Int,
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
