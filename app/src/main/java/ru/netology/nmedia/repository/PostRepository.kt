package ru.netology.nmedia.repository

import androidx.lifecycle.LiveData
import kotlinx.coroutines.flow.Flow
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.model.PhotoModel

interface PostRepository {
    ////////////////////////////////////
    //val data: LiveData<List<Post>>

    // Flow
    val data: Flow<List<Post>>
    fun getNewer(id: Int): Flow<Int>
    suspend fun getNewPosts() // вновь загруженные посты
    ///////////////////////////////////

    suspend fun getAll()
    suspend fun save(post: Post)

    //////////////////////////
    //IMAGE
    suspend fun saveWithAttachment(post: Post, photoModel: PhotoModel)
    //////////////////////////

    suspend fun removeById(id: Long)
    suspend fun likeById(id: Long)
    suspend fun dislikeById(id: Long)
}
