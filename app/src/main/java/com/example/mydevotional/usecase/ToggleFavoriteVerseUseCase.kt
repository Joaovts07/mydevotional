package com.example.mydevotional.usecase

import com.example.mydevotional.repositorie.FavoriteVersesRepository
import javax.inject.Inject

class ToggleFavoriteVerseUseCase @Inject constructor(
    private val repository: FavoriteVersesRepository
) {
    suspend operator fun invoke(verseId: String) {
        repository.toggleFavorite(verseId)
    }
}