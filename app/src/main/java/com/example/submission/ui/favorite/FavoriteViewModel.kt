package com.example.submission.ui.favorite

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.submission.data.UserRepository

class FavoriteViewModel(context: Context) : ViewModel() {
    private val userRepository: UserRepository = UserRepository(context)

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun getAllFavorites() = userRepository.getAll()
}