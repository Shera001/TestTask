package com.test.feature.posts.adapter

import android.util.Log
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.test.core.model.Post

class PostsAdapter(
    private val onPostItemClickListener: OnPostItemClickListener
) : PagingDataAdapter<Post, PostItemViewHolder>(PostsDiffItemCallback) {

    override fun onBindViewHolder(holder: PostItemViewHolder, position: Int) {
        holder.onBind(getItem(position))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostItemViewHolder {
        return PostItemViewHolder.crete(parent, onPostItemClickListener)
    }

    companion object PostsDiffItemCallback : DiffUtil.ItemCallback<Post>() {
        override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean {
            return (oldItem.title == newItem.title) || (oldItem.body == newItem.title)
        }
    }
}