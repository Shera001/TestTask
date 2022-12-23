package com.test.feature.posts.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.test.core.model.Post
import com.test.feature.posts.databinding.ItemPostBinding

class PostItemViewHolder(
    private val binding: ItemPostBinding,
    private val onPostItemClickListener: OnPostItemClickListener
) : ViewHolder(binding.root) {

    var post: Post? = null

    fun onBind(item: Post?) {
        post = item
        binding.apply {
            titleTv.text = item?.title
            bodyTv.text = item?.body

            root.setOnClickListener {
                item?.let { it1 -> onPostItemClickListener.onClick(it1) }
            }
        }
    }

    companion object {
        fun crete(
            viewGroup: ViewGroup,
            onPostItemClickListener: OnPostItemClickListener
        ): PostItemViewHolder {
            return PostItemViewHolder(
                ItemPostBinding.inflate(
                    LayoutInflater.from(viewGroup.context),
                    viewGroup,
                    false
                ),
                onPostItemClickListener
            )
        }
    }
}