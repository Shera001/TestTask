package com.test.feature.posts

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.test.core.common.util.IODispatcher
import com.test.core.model.Post
import com.test.feature.posts.adapter.OnPostItemClickListener
import com.test.feature.posts.adapter.PostsAdapter
import com.test.feature.posts.adapter.PostsItemDecorator
import com.test.feature.posts.adapter.PostsLoaderStateAdapter
import com.test.feature.posts.add_edit.AddEditSheet
import com.test.feature.posts.databinding.FragmentPostsBinding
import com.test.feature.posts.utils.SwipeToDelete
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@AndroidEntryPoint
class PostsFragment : Fragment(R.layout.fragment_posts) {

    private var _viewBinding: FragmentPostsBinding? = null
    private val viewBinding get() = checkNotNull(_viewBinding)

    @Inject
    @IODispatcher
    lateinit var ioDispatcher: CoroutineDispatcher

    private val exceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
        val name = coroutineContext[CoroutineName] ?: "unknown"
        val error = throwable.message
        Log.e("AddEditSheet", "exceptionHandler: name = $name, error = $error")
    }

    private val scope: CoroutineScope by lazy {
        CoroutineScope(
            ioDispatcher +
                    SupervisorJob() +
                    exceptionHandler
        )
    }

    private val viewModel by viewModels<PostsViewModel>()

    private val postsAdapter by lazy {
        PostsAdapter(onPostItemClickListener)
    }

    private var isBottomSheetOpened = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _viewBinding = FragmentPostsBinding.inflate(inflater, container, false)
        return _viewBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindAdapter()

        postsAdapter.addLoadStateListener { state ->
            with(viewBinding) {
                postRv.isVisible = state.refresh != LoadState.Loading
                if (state.refresh == LoadState.Loading) {
                    postRv.isVisible = false
                    shimmerContainer.isVisible = true
                    shimmerContainer.startShimmer()
                } else {
                    shimmerContainer.stopShimmer()
                    shimmerContainer.isVisible = false
                    postRv.isVisible = true
                }
            }
        }

        viewBinding.fabAdd.setOnClickListener {
            openBottomSheet()
        }

        initSwipeToDelete()

        lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.posts.collectLatest { posts: PagingData<Post> ->
                    postsAdapter.submitData(posts)
                }
            }
        }
    }

    override fun onDestroyView() {
        _viewBinding = null
        super.onDestroyView()
    }

    private fun bindAdapter() {
        with(viewBinding.postRv) {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            addItemDecoration(PostsItemDecorator())
            adapter = postsAdapter.withLoadStateHeaderAndFooter(
                header = PostsLoaderStateAdapter(),
                footer = PostsLoaderStateAdapter()
            )
        }
    }

    private fun initSwipeToDelete() {
        val swipeToDeleteCallback = SwipeToDelete { position: Int?, item: Post? ->
            position ?: return@SwipeToDelete
            scope.launch {
                item?.let { viewModel.deletePost(it.id) }
            }
            showRestoreItemSnackbar(position, item)
        }
        ItemTouchHelper(swipeToDeleteCallback).attachToRecyclerView(viewBinding.postRv)
    }

    private fun showRestoreItemSnackbar(position: Int, item: Post?) {
        Snackbar.make(viewBinding.postRv, getString(R.string.deleted_message), Snackbar.LENGTH_LONG)
            .setAction("Undo") {
                scope.launch {
                    item?.let { post -> viewModel.clearDeletedPost(post) }
                }
                if (position == 0) {
                    viewBinding.postRv.smoothScrollToPosition(0)
                }
            }.show()
    }

    private val onPostItemClickListener = object : OnPostItemClickListener {
        override fun onClick(post: Post) {
            openBottomSheet(post)
        }
    }

    private fun openBottomSheet(post: Post? = null) {
        if (!isBottomSheetOpened) {
            AddEditSheet(post) {
                isBottomSheetOpened = false
            }.show(requireActivity().supportFragmentManager, "AddEdit")
            isBottomSheetOpened = true
        }
    }
}