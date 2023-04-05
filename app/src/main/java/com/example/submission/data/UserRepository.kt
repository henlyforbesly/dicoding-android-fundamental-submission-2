package com.example.submission.data

import android.content.Context
import com.example.submission.data.local.UserDao
import com.example.submission.data.local.UserRoomDatabase
import com.example.submission.data.local.entities.User
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class UserRepository(context: Context) {
    private val userDao: UserDao
    private val executorService: ExecutorService = Executors.newSingleThreadExecutor()

    init {
        val userRoomDatabase = UserRoomDatabase.getDatabase(context)
        userDao = userRoomDatabase.userDao()
    }

    fun insert(user: User) = executorService.execute { userDao.insert(user) }

    fun delete(user: User) = executorService.execute { userDao.delete(user) }

    fun getUserByUsername(username: String) = userDao.getUserByUsername(username)

    fun getAll() = userDao.getAll()
}