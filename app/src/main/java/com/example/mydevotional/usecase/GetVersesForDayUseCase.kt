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
        val bibleResponses = repository.getVersesForDay(date)
        return transformBibleResponsesToVerses(bibleResponses)
    }

    private fun transformBibleResponsesToVerses(bibleResponses: List<BibleResponse>): List<Verses> {
        val versesForDay = mutableListOf<Verses>()
        bibleResponses.forEach { bibleResponse ->
            bibleResponse.verses.forEach { verse ->
                versesForDay.add(verse.copy(textChapter = bibleResponse.text))
            }
        }
        return versesForDay
    }
}