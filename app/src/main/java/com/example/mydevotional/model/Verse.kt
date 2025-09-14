package com.example.mydevotional.model

data class Verses(
    val bookId: String = "",
    val bookName: String = "",
    val chapter: Int = 0,
    val verse: Int = 0,
    val text: String = "",
    val isFavorite: Boolean = false,
    val isRead: Boolean = false,
    val textChapter: String  = ""
)
