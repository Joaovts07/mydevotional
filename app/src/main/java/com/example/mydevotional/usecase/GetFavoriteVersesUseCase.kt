package com.example.mydevotional.usecase

import com.example.mydevotional.repositorie.FavoriteVersesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetFavoriteVersesUseCase @Inject constructor(
    private val repository: FavoriteVersesRepository
) {
    operator fun invoke(): Flow<Set<String>> {
        return repository.favoriteVerses
    }
}