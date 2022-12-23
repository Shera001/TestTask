package com.test.feature.posts.adapter

import com.test.core.model.Post

interface OnPostItemClickListener {
    fun onClick(post: Post)
}