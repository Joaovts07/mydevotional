package com.example.mydevotional

import com.example.mydevotional.ui.theme.Verse

class GetBibleContentUseCase(
    private val repository: BibleRepository
) {
    suspend fun getBooks(): List<BibleBook> {
        return repository.getBibleBooks()
    }

    suspend fun getChapters(book: BibleBook): List<Int> {
        return repository.getChapters(book)
    }

    suspend fun getVerses(book: BibleBook, chapter: Int): List<Verse> {
        return repository.getVerses(book, chapter)
    }
}