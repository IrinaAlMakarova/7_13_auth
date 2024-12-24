package ru.netology.nmedia.repository

import android.annotation.SuppressLint
import android.net.http.NetworkException
import android.provider.MediaStore
import androidx.lifecycle.*
import androidx.room.util.copyAndClose
import com.google.android.gms.common.api.ApiException
import com.google.firebase.firestore.auth.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okio.IOException
import ru.netology.nmedia.api.*
import ru.netology.nmedia.dao.PostDao
import ru.netology.nmedia.dto.Attachment
import ru.netology.nmedia.dto.AttachmentType
import ru.netology.nmedia.dto.Media
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.entity.PostEntity
import ru.netology.nmedia.entity.toDto
import ru.netology.nmedia.entity.toEntity
import ru.netology.nmedia.error.ApiError
import ru.netology.nmedia.error.NetworkError
import ru.netology.nmedia.error.UnknownError
import ru.netology.nmedia.model.PhotoModel
import java.io.File


class PostRepositoryImpl(private val dao: PostDao) : PostRepository {
    ////////////////////////////////////////////////////////////////
    //override val data = dao.getAll().map(List<PostEntity>::toDto)

    // Flow
    override val data = dao.getAll().map {
        it.map { it.toDto() }
    }

    override fun getNewer(id: Int): Flow<Int> = flow {
        while (true) {
            delay(10_000)
            val response = PostsApi.service.getNewer(id.toLong())
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            val body = response.body() ?: throw ApiError(response.code(), response.message())
            //dao.insert(body.toEntity())
            // 1сп.
            //dao.insert(body.toEntity().map {it.copy(visibility = 0)})
            // 2сп.
            dao.backgroundInsert(body.toEntity().map { it.copy(visibility = 0) })

            emit(body.size)
        }
    }
        .catch { it.printStackTrace() }
        .flowOn(Dispatchers.Default)


    override suspend fun getNewPosts() {
        try {
            dao.updateNewPosts() // Добавление новых постов (видимы)
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }
    ////////////////////////////////////////////////////////////////


    //////////////////////////
    //IMAGE
    override suspend fun saveWithAttachment(post: Post, photoModel: PhotoModel) {
        try {
            val media = upload(photoModel.file)
            val response = PostsApi.service.save(
                post.copy(
                    attachment = Attachment(
                        url = media.id,
                        AttachmentType.IMAGE
                    )
                )

            )
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }

            val body = response.body() ?: throw ApiError(response.code(), response.message())
            dao.insert(PostEntity.fromDto(body))
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    private suspend fun upload(file: File): Media {
        try {
            val response = PostsApi.service.upload(
                MultipartBody.Part.createFormData(
                    "file",
                    file.name,
                    file.asRequestBody()
                )
            )

            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }

            return response.body() ?: throw ApiError(response.code(), response.message())
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }
    //////////////////////////


    ///////////////////////////////////
    // Auth
    fun authUser(login: String?, password: String?) {

    }
    //////////////////////////////////


    override suspend fun getAll() {
        try {
            val response = PostsApi.service.getAll()
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }

            val body = response.body() ?: throw ApiError(response.code(), response.message())
            dao.insert(body.toEntity())
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun save(post: Post) {
        try {
            val response = PostsApi.service.save(post)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }

            val body = response.body() ?: throw ApiError(response.code(), response.message())
            dao.insert(PostEntity.fromDto(body))
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun removeById(id: Long) {
        try {
            dao.removeById(id) // Удаление записи из локальной БД

            val response = PostsApi.service.removeById(id)  // Отправка запроса в API (HTTP)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }

        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun likeById(id: Long) {
        try {
            dao.likeById(id) // Модифицикация записи в локальной БД

            val response = PostsApi.service.likeById(id) // Отправка запроса в API (HTTP)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }

        } catch (e: IOException) {
            dao.dislikeById(id)
            throw NetworkError
        } catch (e: Exception) {
            dao.dislikeById(id)
            throw UnknownError
        }
    }

    override suspend fun dislikeById(id: Long) {
        try {
            dao.dislikeById(id) // Модифицикация записи в локальной БД

            val response = PostsApi.service.dislikeById(id) // Отправка запроса в API (HTTP)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }

        } catch (e: IOException) {
            dao.likeById(id)
            throw NetworkError
        } catch (e: Exception) {
            dao.likeById(id)
            throw UnknownError
        }
    }
}