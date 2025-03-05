package com.example.mydevotional.usecase

import com.example.mydevotional.repositorie.BibleRepository
import com.example.mydevotional.ui.theme.Verse
import javax.inject.Inject

class GetVerseBibleUseCase @Inject constructor(
    private val repository: BibleRepository
) {
    suspend operator fun invoke(bibleBook: String, verseNumber: Int): List<Verse> {
        return repository.getVerses(book = bibleBook, chapter = verseNumber)
    }
}