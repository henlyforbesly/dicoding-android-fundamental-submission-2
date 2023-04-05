package com.example.submission.data.local

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.submission.data.local.entities.User

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(user: User)

    @Delete
    fun delete(user: User)

    @Query("SELECT * FROM users WHERE username = :username")
    fun getUserByUsername(username: String): LiveData<User>

    @Query("SELECT * FROM users")
    fun getAll(): LiveData<List<User>>
}