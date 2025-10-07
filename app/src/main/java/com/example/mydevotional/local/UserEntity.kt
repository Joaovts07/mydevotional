package com.example.mydevotional.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.mydevotional.model.User


@Entity(tableName = "user")
data class UserEntity(
    @PrimaryKey val id: String,
    val name: String,
    val email: String,
    val translation: String
)

fun UserEntity.toDomain(): User = User(
    id = id,
    name = name,
    email = email,
    translation = translation
)

fun User.toEntity(): UserEntity = UserEntity(
    id = id,
    name = name,
    email = email,
    translation = translation
)