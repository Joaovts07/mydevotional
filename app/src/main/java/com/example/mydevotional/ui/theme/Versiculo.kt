package com.example.mydevotional.ui.theme

data class Versiculo(
    val translation: Translation,
    val random_verse: RandomVerse
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
