package ru.netology.nmedia.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.auth.User
import kotlinx.coroutines.launch
import ru.netology.nmedia.model.FeedModelState
import ru.netology.nmedia.repository.PostRepositoryImpl
import javax.inject.Inject

class SignInViewModel @Inject constructor(
    private val repositoryImpl: PostRepositoryImpl,
) : ViewModel() {

    private val _data = MutableLiveData<User>()
    val data: LiveData<User>
        get() = _data

    private val _dataState = MutableLiveData<FeedModelState>()
    val dataState: LiveData<FeedModelState>
        get() = _dataState

    fun loginAttempt(login: String, password: String) = viewModelScope.launch {
            try {
                _data.value = repositoryImpl.authUser(login, password)
            } catch (e: Exception) {
                _dataState.value = FeedModelState()
            }
    }
}