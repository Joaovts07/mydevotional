package com.example.mydevotional.usecase

import com.example.mydevotional.model.BibleResponse
import com.example.mydevotional.repositorie.BibleRepository
import java.util.Date
import javax.inject.Inject

class GetVersesForDayUseCase @Inject constructor(
    private val repository: BibleRepository,
    private val favoriteVerseUseCase: FavoriteVerseUseCase
) {
    suspend operator fun invoke(date: Date): Result<List<BibleResponse>> {
        return repository.getVersesForDay(date)
            .map { favoriteVerseUseCase.updateFavoriteVerses(it) }
    }
}
