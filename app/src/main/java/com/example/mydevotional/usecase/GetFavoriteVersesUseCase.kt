package com.example.mydevotional.usecase

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
}