package com.example.mydevotional.usecase

import com.example.mydevotional.repositorie.FavoriteVersesRepository
import com.example.mydevotional.model.Verses
import javax.inject.Inject

class ToggleFavoriteVerseUseCase @Inject constructor(
    private val repository: FavoriteVersesRepository
) {
    suspend operator fun invoke(verses: Verses) {
        repository.toggleFavorite(verses)
    }
}