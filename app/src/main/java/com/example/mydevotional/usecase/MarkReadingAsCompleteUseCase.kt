package com.example.mydevotional.usecase

import com.example.mydevotional.repositorie.CompletedReadingsRepository
import javax.inject.Inject

class MarkReadingAsCompleteUseCase @Inject constructor(
    private val completedReadingsRepository: CompletedReadingsRepository
){
    suspend operator fun invoke(readingId: String) {
        completedReadingsRepository.markReadingAsComplete(readingId)
    }
}
