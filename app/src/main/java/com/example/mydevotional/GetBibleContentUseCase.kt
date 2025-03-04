package com.example.mydevotional

import com.example.mydevotional.repositorie.BibleRepository
import com.example.mydevotional.ui.theme.Verse
import java.util.Date
import javax.inject.Inject

class GetBibleContentUseCase @Inject constructor(
    private val repository: BibleRepository
) {
    suspend fun getBooks(): List<BibleBook> {
        return repository.getBibleBooks()
    }

    suspend fun getChapters(book: BibleBook): Int {
        return repository.getChapters(book)
    }

    suspend fun getVerses(book: BibleBook, chapter: Int): List<Verse> {
        return repository.getVerses(book, chapter)
    }
    suspend fun getVerseForDate(date: Date): List<Verse> {
        return repository.getVersesForDay(date)
    }

}