package com.example.mydevotional.model

data class Verse(
    val translation: Translation,
    val random_verse: RandomVerse,
    val verses: List<Verses>,
    val text: String = ""
)

data class Translation(
    val identifier: String,
    val name: String,
    val language: String,
    val language_code: String,
    val license: String
)

data class RandomVerse(
    val book_id: String,
    val book: String,
    val chapter: Int,
    val verse: Int,
    val text: String
)
data class Verses(
    val book_id: String,
    val book_name: String,
    val chapter: Int,
    val verse: Int,
    val text: String,
    val isFavorite: Boolean = false
)
