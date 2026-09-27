package com.example.mydevotional.remote

import com.example.mydevotional.model.User
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirestoreUserRemoteDataSource @Inject constructor(
    private val firestore: FirebaseFirestore
) : UserRemoteDataSource {

    override suspend fun fetchUser(uid: String): User? {
        return try {
            userDocument(uid).get().await().toUser()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            null
        }
    }

    override fun observeUser(uid: String): Flow<User?> = callbackFlow {
        val registration = userDocument(uid).addSnapshotListener { snapshot, error ->
            if (error != null) {
                // e.g. PERMISSION_DENIED after logout: stop listening instead of crashing.
                close()
                return@addSnapshotListener
            }
            trySend(snapshot?.toUser())
        }
        awaitClose { registration.remove() }
    }

    private fun userDocument(uid: String) = firestore.collection("users").document(uid)

    // The document id is the uid; it isn't stored as a field, so set it explicitly.
    private fun DocumentSnapshot.toUser(): User? = toObject(User::class.java)?.copy(id = id)
}
