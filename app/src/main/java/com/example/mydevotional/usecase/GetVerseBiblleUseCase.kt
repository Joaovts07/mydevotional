package com.example.mydevotional.usecase

import com.example.mydevotional.BibleBook
import com.example.mydevotional.repositorie.BibleRepository
import com.example.mydevotional.ui.theme.Verse
import javax.inject.Inject

class GetVerseBiblleUseCase @Inject constructor(
    private val repository: BibleRepository
) {
    suspend operator fun invoke(bibleBook: BibleBook, verseNumber: Int): List<Verse> {
        return repository.getVerses(book = bibleBook, chapter = verseNumber)
    }
}