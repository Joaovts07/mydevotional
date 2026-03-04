package com.example.mydevotional.viewmodel

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mydevotional.usecase.SaveReadingsFromImageUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReadingScannerViewModel @Inject constructor(
    private val saveReadingsFromImageUseCase: SaveReadingsFromImageUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<ScannerUiState>(ScannerUiState.Idle)
    val uiState = _uiState.asStateFlow()

    fun onImageCaptured(bitmap: Bitmap) {
        viewModelScope.launch {
            _uiState.value = ScannerUiState.Loading
            val success = saveReadingsFromImageUseCase(bitmap)
            if (success) {
                _uiState.value = ScannerUiState.Success
            } else {
                _uiState.value = ScannerUiState.Error("Failed to parse or save readings. Please try again.")
            }
        }
    }

    fun resetState() {
        _uiState.value = ScannerUiState.Idle
    }
}

sealed class ScannerUiState {
    object Idle : ScannerUiState()
    object Loading : ScannerUiState()
    object Success : ScannerUiState()
    data class Error(val message: String) : ScannerUiState()
}
