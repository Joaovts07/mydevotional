package com.example.mydevotional.usecase

import com.example.mydevotional.model.BibleResponse
import com.example.mydevotional.repositorie.BibleRepository
import com.example.mydevotional.model.Verses
import javax.inject.Inject

class GetVerseBibleUseCase @Inject constructor(
    private val repository: BibleRepository
) {
    suspend operator fun invoke(bibleBook: String, verseNumber: Int): List<BibleResponse> {
        return repository.getVerses(book = bibleBook, chapter = verseNumber)
    }
}