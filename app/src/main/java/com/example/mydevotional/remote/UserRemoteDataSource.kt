package com.example.mydevotional.remote

import com.example.mydevotional.model.User
import kotlinx.coroutines.flow.Flow

interface UserRemoteDataSource {
    suspend fun fetchUser(uid: String): User?

    /** Emits the user every time the remote document changes, until collection is cancelled. */
    fun observeUser(uid: String): Flow<User?>
}
