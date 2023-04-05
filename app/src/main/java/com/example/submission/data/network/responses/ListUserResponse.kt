package com.example.submission.data.network.responses

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

data class ListUserResponse(
    val items: List<User>
)

@Parcelize
data class User(
    val login: String,

    @field:SerializedName("avatar_url")
    val avatarUrl: String,
) : Parcelable
