package com.example.mydevotional.state

import com.example.mydevotional.model.BibleResponse
import java.util.Date

data class HomeUiState(
    val selectedDate: Date = Date(),
    val readings: List<BibleResponse> = emptyList(),
    val isLoading: Boolean = false,
    val loadFailed: Boolean = false,
    val completedDays: Set<String> = emptySet(),
    val isReadingCompleted: Boolean = false,
    val message: String? = null
)
