package com.example.submission.ui.detail

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.submission.data.UserRepository
import com.example.submission.data.local.entities.User
import com.example.submission.data.network.ApiConfig
import com.example.submission.data.network.responses.UserDetailResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class DetailViewModel(context: Context) : ViewModel() {
    private val userRepository: UserRepository = UserRepository(context)

    private val _userDetail = MutableLiveData<UserDetailResponse>()
    val userDetail: LiveData<UserDetailResponse> = _userDetail

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isError = MutableLiveData<Boolean>()
    val isError: LiveData<Boolean> = _isError

    fun getUserDetail(username: String) {
        _isLoading.value = true

        ApiConfig.getApiService().getUserDetail(username).enqueue(object : Callback<UserDetailResponse> {
                override fun onResponse(
                    call: Call<UserDetailResponse>,
                    response: Response<UserDetailResponse>
                ) {
                    _isLoading.value = false

                    if (response.isSuccessful) {
                        _isError.value = false
                        response.body()?.let {
                            _userDetail.value = it
                        }
                    } else {
                        _isError.value = true
                    }
                }

                override fun onFailure(call: Call<UserDetailResponse>, t: Throwable) {
                    _isLoading.value = false
                    _isError.value = true
                }
            })
    }

    fun checkIsFavorited(username: String) = userRepository.getUserByUsername(username)

    fun insertUserAsFavorite(user: User) = userRepository.insert(user)

    fun deleteUserFromFavorite(user: User) = userRepository.delete(user)
}