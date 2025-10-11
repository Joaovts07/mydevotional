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
    suspend fun fetchUser(): User? {
        val userId = auth.currentUser?.uid ?: return null
        val snapshot = firestore.collection("users")
            .document(userId)
            .get()
            .await()

        return snapshot.toObject(User::class.java)?.copy(id = userId)
    }

    fun listenUser(onUserChanged: (User?) -> Unit) {
        val userId = auth.currentUser?.uid ?: return
        firestore.collection("users")
            .document(userId)
            .addSnapshotListener { snapshot, _ ->
                val user = snapshot?.toObject(User::class.java)?.copy(id = userId)
                onUserChanged(user)
            }
    }
}