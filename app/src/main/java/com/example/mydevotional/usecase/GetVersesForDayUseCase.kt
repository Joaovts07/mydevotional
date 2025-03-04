package com.example.mydevotional.usecase

import com.example.mydevotional.repositorie.BibleRepository
import com.example.mydevotional.ui.theme.Verse
import java.util.Date
import javax.inject.Inject

class GetVersesForDayUseCase @Inject constructor(
    private val repository: BibleRepository
) {
    suspend operator fun invoke(date: Date): List<Verse> {
        return repository.getVersesForDay(date)
    }
}