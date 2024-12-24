package ru.netology.nmedia.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation
import ru.netology.nmedia.BuildConfig
import ru.netology.nmedia.BuildConfig.BASE_URL
import ru.netology.nmedia.databinding.FragmentImageBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.view.load

class ImageFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val binding = FragmentImageBinding.inflate(
            inflater,
            container,
            false
        )

        binding.apply {
            attachmentAll.visibility = View.GONE
            attachmentAll.load("${BASE_URL}/media/${arguments?.getString("image")}")
            attachmentAll.visibility = View.VISIBLE
        }

        return binding.root
    }
}