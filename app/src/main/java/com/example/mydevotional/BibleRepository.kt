package com.example.mydevotional

interface BibleRepository {
    fun getBibleBooks(): List<BibleBook>
    fun getChapters(bibleBook: BibleBook): Int
}