package com.example.mydevotional.repositorie

import com.example.mydevotional.local.UserDao
import com.example.mydevotional.local.toDomain
import com.example.mydevotional.local.toEntity
import com.example.mydevotional.model.User
import com.example.mydevotional.remote.UserRemoteDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val userDao: UserDao,
    private val remoteDataSource: UserRemoteDataSource
) {

    fun getProfile(): Flow<User?> {
        return userDao.getUser().map { it?.toDomain() }
    }

    suspend fun syncUser(uid: String) {
        val user = remoteDataSource.fetchUser(uid)
        if (user != null) {
            userDao.insertUser(user.toEntity())
        }
    }

    /** Mirrors the remote user into the local database until the calling coroutine is cancelled. */
    suspend fun observeRemoteUser(uid: String) {
        remoteDataSource.observeUser(uid)
            .filterNotNull()
            .collect { user -> userDao.insertUser(user.toEntity()) }
    }

    suspend fun clearUser() {
        userDao.clearUser()
    }
}