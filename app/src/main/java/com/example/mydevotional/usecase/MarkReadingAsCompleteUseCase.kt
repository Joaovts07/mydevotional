package com.example.mydevotional.usecase

import com.example.mydevotional.extensions.formatDate
import com.example.mydevotional.repositorie.CompletedReadingsRepository
import java.util.Date
import javax.inject.Inject

class MarkReadingAsCompleteUseCase @Inject constructor(
    private val completedReadingsRepository: CompletedReadingsRepository
){
    suspend operator fun invoke(readingId: String) {
        if (readingId.isEmpty()) {
            completedReadingsRepository.markReadingAsComplete(Date().formatDate("yyyy-MM-dd"))
        } else {
            completedReadingsRepository.markReadingAsComplete(readingId)
        }
    }
}
