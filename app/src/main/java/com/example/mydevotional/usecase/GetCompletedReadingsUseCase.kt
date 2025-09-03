package com.example.mydevotional.usecase

import com.example.mydevotional.repositorie.CompletedReadingsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CompleteReadingsUseCase @Inject constructor(
    private val repository: CompletedReadingsRepository
) {
    fun getCompletedReadingsFlow(): Flow<Set<String>> {
        return repository.getCompletedReadings()
    }

    suspend fun toggleCompletion(date: String) {
        repository.toggleReadingComplete(date)
    }

}

