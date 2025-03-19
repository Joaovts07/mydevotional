package com.example.mydevotional.usecase

import com.example.mydevotional.model.BibleResponse
import com.example.mydevotional.repositorie.BibleRepository
import com.example.mydevotional.model.Verses
import java.util.Date
import javax.inject.Inject

class GetVersesForDayUseCase @Inject constructor(
    private val repository: BibleRepository
) {
    suspend operator fun invoke(date: Date): List<Verses> {
        return getVersesForDay(repository.getVersesForDay(date))
    }

    private fun getVersesForDay(verses: BibleResponse): List<Verses> {
        return verses.verses
    }
}