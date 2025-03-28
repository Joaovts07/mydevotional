package com.example.mydevotional.usecase

import kotlinx.coroutines.flow.Flow

interface GetCompletedReadingsUseCase {
    suspend operator fun invoke(): Flow<Set<String>>
}