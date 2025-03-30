package com.example.mydevotional.usecase

import com.example.mydevotional.repositorie.CompletedReadingsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCompletedReadingsUseCase @Inject constructor(
    private val completedReadingsRepository: CompletedReadingsRepository
){
    operator fun invoke(): Flow<Set<String>> {
        return completedReadingsRepository.getCompletedReadings()
    }
}