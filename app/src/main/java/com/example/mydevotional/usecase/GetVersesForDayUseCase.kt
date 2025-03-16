package com.example.mydevotional.usecase

import com.example.mydevotional.repositorie.BibleRepository
import com.example.mydevotional.ui.theme.Verses
import java.util.Date
import javax.inject.Inject

class GetVersesForDayUseCase @Inject constructor(
    private val repository: BibleRepository
) {
    suspend operator fun invoke(date: Date): List<Verses> {
        return repository.getVersesForDay(date)
    }
}