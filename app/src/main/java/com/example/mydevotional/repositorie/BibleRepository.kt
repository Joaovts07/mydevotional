package com.example.mydevotional.repositorie

import com.example.mydevotional.BibleBook
import com.example.mydevotional.model.BibleResponse
import java.util.Date

interface BibleRepository {
    fun getBibleBooks(): List<BibleBook>
    fun getChapters(bibleBook: BibleBook): Int
    suspend fun getVerses(book: String, chapter: Int): Result<List<BibleResponse>>
    suspend fun getVersesForDay(date: Date): Result<List<BibleResponse>>

    /** Saves the passage references (e.g. "Genesis 1") of each date, keyed by "yyyy-MM-dd". */
    suspend fun savePassages(passagesByDate: Map<String, List<String>>): Result<Unit>
}
