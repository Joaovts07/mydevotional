package com.example.mydevotional.usecase

import com.example.mydevotional.model.BibleResponse
import com.example.mydevotional.repositorie.BibleRepository
import javax.inject.Inject

class GetVerseBibleUseCase @Inject constructor(
    private val repository: BibleRepository,
    private val favoriteVerseUseCase: FavoriteVerseUseCase
) {
    suspend operator fun invoke(bibleBook: String, verseNumber: Int): List<BibleResponse> {
        val bibleResponses = repository.getVerses(book = bibleBook, chapter = verseNumber)
        return favoriteVerseUseCase.updateFavoriteVerses(bibleResponses)
    }
}