package com.example.mydevotional.model

import com.google.gson.annotations.SerializedName

data class Verses(
    val bookId: String = "",
    @SerializedName("book_name")
    val bookName: String = "",
    val chapter: Int = 0,
    val verse: Int = 0,
    val text: String = "",
    val isFavorite: Boolean = false,
    val isRead: Boolean = false,
    val textChapter: String  = ""
)
