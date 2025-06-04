package com.example.mydevotional.state

sealed interface DailyReadingUiState {
    object Loading : DailyReadingUiState
    data class Success(
        val isReadingCompleted: Boolean = false,
    ) : DailyReadingUiState
    data class Error(val message: String) : DailyReadingUiState
} 