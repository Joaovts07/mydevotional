package com.example.mydevotional.usecase

import com.example.mydevotional.model.BibleResponse
import com.example.mydevotional.model.Verses
import com.example.mydevotional.repositorie.FavoriteVersesRepository
import javax.inject.Inject

class FavoriteVerseUseCase @Inject constructor(
    private val favoriteVersesRepository: FavoriteVersesRepository
) {

    suspend fun toggleFavorite(verse: Verses) {
        favoriteVersesRepository.toggleFavorite(verse)
    }

    suspend fun isVerseFavorite(verse: Verses): Boolean {
        return favoriteVersesRepository.isVerseFavorite(verse)
    }

    suspend fun getFavoriteVerses(): List<Verses> {
        return favoriteVersesRepository.getFavoriteVerses()
    }

    suspend fun updateFavoriteVerses(bibleResponses: List<BibleResponse>): List<BibleResponse> {
        val updatedBibleResponses = bibleResponses.map { bibleResponse ->
            val updatedVerses = bibleResponse.verses.map { verse ->
                val isFavorite = isVerseFavorite(verse)
                verse.copy(isFavorite = isFavorite)
            }
            bibleResponse.copy(verses = updatedVerses)
        }
        return updatedBibleResponses
    }
}