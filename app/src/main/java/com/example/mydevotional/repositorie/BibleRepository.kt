package com.example.mydevotional.repositorie

import com.example.mydevotional.BibleBook
import com.example.mydevotional.model.Verses
import java.util.Date

interface BibleRepository {
    fun getBibleBooks(): List<BibleBook>
    fun getChapters(bibleBook: BibleBook): Int
    suspend fun getVerses(book: String, chapter: Int): List<Verses>
    suspend fun getVersesForDay(date: Date): List<Verses>
    suspend fun searchReadingDaily(date: Date): Any?
}