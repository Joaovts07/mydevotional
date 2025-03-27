package com.example.mydevotional.usecase

interface MarkReadingAsCompleteUseCase {
    suspend operator fun invoke(date: String)
}
