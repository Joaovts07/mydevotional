package com.example.mydevotional.repositorie

import com.example.mydevotional.BibleBook
import com.example.mydevotional.ui.theme.Verse
import java.util.Date

interface BibleRepository {
    fun getBibleBooks(): List<BibleBook>
    fun getChapters(bibleBook: BibleBook): Int
    suspend fun getVerses(book: String, chapter: Int): List<Verse>
    suspend fun getVersesForDay(date: Date): List<Verse>
    suspend fun searchReadingDaily(date: Date): Any?
}