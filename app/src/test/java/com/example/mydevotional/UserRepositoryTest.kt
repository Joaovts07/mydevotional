package com.example.mydevotional

import com.example.mydevotional.local.UserDao
import com.example.mydevotional.local.UserEntity
import com.example.mydevotional.model.User
import com.example.mydevotional.remote.UserRemoteDataSource
import com.example.mydevotional.repositorie.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class UserRepositoryTest {

    private val testUser = User(id = "uid123", name = "Ana", email = "ana@example.com", translation = "NVI")

    private val userDao = FakeUserDao()
    private val remote = FakeUserRemoteDataSource(testUser)
    private val repository = UserRepository(userDao, remote)

    @Test
    fun syncUser_insertsUserIntoDatabase() = runTest {
        repository.syncUser("uid123")

        assertEquals(testUser, repository.getProfile().first())
    }

    @Test
    fun syncUser_withUnknownUser_keepsDatabaseEmpty() = runTest {
        repository.syncUser("other")

        assertNull(repository.getProfile().first())
    }

    @Test
    fun observeRemoteUser_mirrorsChangesAndStopsWhenCancelled() = runTest {
        val job = launch { repository.observeRemoteUser("uid123") }
        runCurrent()

        remote.updates.emit(testUser.copy(name = "Ana Maria"))
        runCurrent()
        assertEquals("Ana Maria", repository.getProfile().first()?.name)

        job.cancel()
        runCurrent()
        assertEquals(0, remote.updates.subscriptionCount.value)
    }
}

private class FakeUserRemoteDataSource(private val user: User) : UserRemoteDataSource {
    val updates = MutableSharedFlow<User?>()

    override suspend fun fetchUser(uid: String): User? = user.takeIf { it.id == uid }

    override fun observeUser(uid: String): Flow<User?> = updates
}

private class FakeUserDao : UserDao {
    private val user = MutableStateFlow<UserEntity?>(null)

    override fun getUser(): Flow<UserEntity?> = user

    override suspend fun insertUser(user: UserEntity) {
        this.user.value = user
    }

    override suspend fun clearUser() {
        user.value = null
    }
}
