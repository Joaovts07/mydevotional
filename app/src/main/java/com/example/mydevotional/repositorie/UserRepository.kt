package com.example.mydevotional.repositorie

import com.example.mydevotional.local.UserDao
import com.example.mydevotional.local.toDomain
import com.example.mydevotional.local.toEntity
import com.example.mydevotional.model.User
import com.example.mydevotional.remote.UserRemoteDataSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val userDao: UserDao,
    private val remoteDataSource: UserRemoteDataSource
) {

    fun getUser(): Flow<User?> {
        return userDao.getUser().map { it?.toDomain() }
    }

    suspend fun syncUser(uid: String) {
        val user = remoteDataSource.fetchUser(uid)
        if (user != null) {
            userDao.insertUser(user.toEntity())
        }
    }

    fun observeRemoteUser(uid: String) {
        remoteDataSource.listenUser(uid) { user ->
            if (user != null) {
                CoroutineScope(Dispatchers.IO).launch {
                    userDao.insertUser(user.toEntity())
                }
            }
        }
    }

    suspend fun clearUser() {
        userDao.clearUser()
    }
}