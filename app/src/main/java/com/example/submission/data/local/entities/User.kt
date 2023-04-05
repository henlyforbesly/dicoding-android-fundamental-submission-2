package com.example.submission.data.local.entities

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity("users")
@Parcelize
data class User(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "username")
    val login: String,

    @ColumnInfo(name = "avatar_url")
    val avatarUrl: String,
) : Parcelable