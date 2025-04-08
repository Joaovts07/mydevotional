package com.example.mydevotional.repositorie

import kotlinx.coroutines.flow.Flow

interface CompletedReadingsRepository {
    suspend fun markReadingAsComplete(date: String)
    fun getCompletedReadings(): Flow<List<String>>
}