package com.example.mydevotional

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.mydevotional.local.AppDatabase
import com.example.mydevotional.local.UserDao
import com.example.mydevotional.repositorie.UserRepository
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

class UserRepositoryTest {

    private lateinit var db: AppDatabase
    private lateinit var userDao: UserDao
    private lateinit var repository: UserRepository

    @Before
    fun setup() {
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).allowMainThreadQueries()
            .build()
        userDao = db.userDao()

        val fakeRemote = FakeUserRemoteDataSource(testUser)
        repository = UserRepository(userDao, fakeRemote)
    }

    @After
    fun tearDown() {
        db.close()
    }


    @Test
    fun syncUser_insertsUserIntoDatabase() = runBlocking {
        val fakeRemote = FakeUserRemoteDataSource(testUser)
        val inMemoryDb = Room.inMemoryDatabaseBuilder(
            context, AppDatabase::class.java
        ).build()
        val userDao = inMemoryDb.userDao()
        val repository = UserRepository(userDao, fakeRemote)

        repository.syncUser("uid123")

        val storedUser = userDao.getUserNow("uid123")
        assertEquals("uid123", storedUser.id)
    }
}