package com.test.feature.posts.add_edit

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.test.core.common.util.Result
import com.test.core.common.util.IODispatcher
import com.test.core.model.Post
import com.test.feature.posts.R
import com.test.feature.posts.databinding.AddEditSheetBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import javax.inject.Inject

@AndroidEntryPoint
class AddEditSheet(
    private var post: Post? = null,
    private val onClosed: () -> Unit
) : BottomSheetDialogFragment(R.layout.add_edit_sheet) {

    private var _viewBinding: AddEditSheetBinding? = null
    private val viewBinding get() = requireNotNull(_viewBinding)

    private val viewModel by viewModels<AddEditViewModel>()

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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _viewBinding = AddEditSheetBinding.inflate(inflater, container, false)
        return _viewBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(viewBinding) {
            if (post != null) {
                caption.text = getString(R.string.edit)
                addBtn.text = getString(R.string.edit)
                title.setText(post!!.title)
                body.setText(post!!.body)
            }
            addBtn.setOnClickListener {
                val title = this.title.text.toString()
                val body = this.body.text.toString()
                if (post != null) {
                    scope.launch {
                        viewModel.editPost(
                            Post(
                                userId = post!!.userId,
                                id = post!!.id,
                                title = title,
                                body = body,
                            )
                        )
                    }
                } else {
                    scope.launch {
                        viewModel.addPost(title, body)
                    }
                }
            }
        }

        lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.result.collect { result ->
                    when (result) {
                        Result.Empty -> showMessage(getString(R.string.empty_filed))
                        Result.Error -> showMessage(getString(R.string.error_message))
                        Result.Success -> {
                            showMessage(getString(R.string.done))
                            this@AddEditSheet.dismiss()
                        }
                        Result.Nothing -> {}
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        onClosed()
        _viewBinding = null
        super.onDestroyView()
    }

    private fun showMessage(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}