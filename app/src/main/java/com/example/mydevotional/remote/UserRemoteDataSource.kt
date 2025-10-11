package com.example.mydevotional.remote

import com.example.mydevotional.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject


class UserRemoteDataSource @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) {
    suspend fun fetchUser(uid: String): User? {
        return try {
            val snapshot = firestore.collection("users")
                .document(uid)
                .get()
                .await()
            snapshot.toObject(User::class.java)
        } catch (e: Exception) {
            null
        }
    }

    fun listenUser(uid: String, onUserChanged: (User?) -> Unit) {
        firestore.collection("users")
            .document(uid)
            .addSnapshotListener { snapshot, _ ->
                onUserChanged(snapshot?.toObject(User::class.java))
            }
    }
}