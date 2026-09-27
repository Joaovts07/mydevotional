package com.example.mydevotional.state

import com.example.mydevotional.model.BibleResponse

data class VersesUiState(
    val readings: List<BibleResponse> = emptyList(),
    val isLoading: Boolean = false,
    val loadFailed: Boolean = false
)
