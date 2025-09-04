package com.example.mydevotional.repositorie

import kotlinx.coroutines.flow.Flow

interface CompletedReadingsRepository {
    fun getCompletedReadings(): Flow<Set<String>>
    suspend fun toggleReadingComplete(date: String)
}