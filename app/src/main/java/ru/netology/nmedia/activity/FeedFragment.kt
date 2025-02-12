package ru.netology.nmedia.activity

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import ru.netology.nmedia.R
import ru.netology.nmedia.adapter.OnInteractionListener
import ru.netology.nmedia.adapter.PostsAdapter
import ru.netology.nmedia.databinding.FragmentFeedBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.viewmodel.AuthViewModel
import ru.netology.nmedia.viewmodel.PostViewModel

class FeedFragment : Fragment() {

    private val viewModel: PostViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentFeedBinding.inflate(inflater, container, false)

        val adapter = PostsAdapter(object : OnInteractionListener {
            override fun onEdit(post: Post) {
                viewModel.edit(post)
            }

            override fun onLike(post: Post) {
                viewModel.likeById(post.id)
            }

            override fun onDisLike(post: Post) {
                viewModel.dislikeById(post.id)
            }

            override fun onRemove(post: Post) {
                viewModel.removeById(post.id)
            }

            override fun onShare(post: Post) {
                val intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, post.content)
                    type = "text/plain"
                }

                val shareIntent =
                    Intent.createChooser(intent, getString(R.string.chooser_share_post))
                startActivity(shareIntent)
            }

            /////////////////////////////////////////////
            //IMAGE
            override fun onImage(image: String) {
                val bundle = Bundle().apply {
                    putString("image", image)
                }
                findNavController().navigate(R.id.action_feedFragment_to_imageFragment, bundle)
            }
            /////////////////////////////////////////////


        })
        binding.list.adapter = adapter
        viewModel.dataState.observe(viewLifecycleOwner) { state ->
            binding.progress.isVisible = state.loading
            binding.swiperefresh.isRefreshing = state.refreshing
            if (state.error) {
                Snackbar.make(binding.root, R.string.error_loading, Snackbar.LENGTH_LONG)
                    .setAction(R.string.retry_loading) { viewModel.loadPosts() }
                    .show()
            }
        }
        viewModel.data.observe(viewLifecycleOwner) { state ->
            adapter.submitList(state.posts)
            binding.emptyText.isVisible = state.empty
        }

        ///////////////////////////////////////
        // FLOW
        //viewModel.newPosts.observe(viewLifecycleOwner){
        //    println(it)
        //}

        //  изначально кнопка "Свежие записи" невидна
        binding.newPosts.visibility = View.GONE

        viewModel.newPosts.observe(viewLifecycleOwner) {
            println(it)

            // если новые посты есть, то кнопка "Свежие записи" видна
            if (it > 0) {
                binding.newPosts.visibility = View.VISIBLE //видимость элемента - виден
            }
        }

        // при нажатии на кнопку "Свежие записи" кнопка исчезает
        binding.newPosts.setOnClickListener {
            binding.newPosts.visibility = View.GONE //видимость элемента - невиден
            binding.list.scrollToPosition(0) //При нажатии на кн. происходит скролл RecyclerView к самому верху
            viewModel.loadNewPosts()
        }

        //////////////////////////////////////


        binding.swiperefresh.setOnRefreshListener {
            viewModel.refreshPosts()
        }


        binding.fab.setOnClickListener {
            findNavController().navigate(R.id.action_feedFragment_to_newPostFragment)
        }

        return binding.root
    }
}
