package com.example.mydevotional.viewmodel

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mydevotional.extensions.formatDate
import com.example.mydevotional.model.BibleResponse
import com.example.mydevotional.model.Verses
import com.example.mydevotional.state.DailyReadingUiState
import com.example.mydevotional.usecase.GetVersesForDayUseCase
import com.example.mydevotional.usecase.MarkReadingAsCompleteUseCase
import com.example.mydevotional.usecase.SaveReadingsFromImageUseCase
import com.example.mydevotional.usecase.ToggleFavoriteVerseUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    private val getVersesForDayUseCase: GetVersesForDayUseCase,
    private val toggleFavoriteVerseUseCase: ToggleFavoriteVerseUseCase,
    private val markReadingAsCompleteUseCase: MarkReadingAsCompleteUseCase,
    private val saveReadingsFromImageUseCase: SaveReadingsFromImageUseCase
) : ViewModel() {

    private val _bibleResponses = MutableStateFlow<List<BibleResponse>>(emptyList())
    val bibleResponse: StateFlow<List<BibleResponse>> = _bibleResponses.asStateFlow()

    private val _selectedDate = MutableStateFlow<Date?>(null)
    val selectedDate: StateFlow<Date?> = _selectedDate.asStateFlow()

    private val _completedReadings = MutableStateFlow<List<String>>(emptyList())
    val completedReadings: StateFlow<List<String>> = _completedReadings.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _readingState = MutableStateFlow<DailyReadingUiState>(DailyReadingUiState.Success())
    val readingState: StateFlow<DailyReadingUiState> = _readingState.asStateFlow()

    private val _uiMessage = MutableStateFlow<String?>(null)
    val uiMessage: StateFlow<String?> = _uiMessage.asStateFlow()


    init {
        loadVersesForToday()
        loadCompletedReadings()
    }


    private fun loadVersesForToday() {
        viewModelScope.launch {
            _isLoading.value = true
            _bibleResponses.value = getVersesForDayUseCase(Date())
            _isLoading.value = false
        }
    }

    fun selectDate(newDate: Date) {
        _selectedDate.value = newDate
        viewModelScope.launch {
            _isLoading.value = true
            _bibleResponses.value = getVersesForDayUseCase(newDate)
            _isLoading.value = false
        }
    }

    private fun loadCompletedReadings() {
        viewModelScope.launch {
            _readingState.value = DailyReadingUiState.Success(true)
        }
    }

    fun toggleFavorite(verse: Verses) {
        viewModelScope.launch {
            toggleFavoriteVerseUseCase(verse)
            updateVerseFavoriteState(verse)
        }
    }

    fun  markReadingAsComplete(date: String) {
        viewModelScope.launch {
            try {
                markReadingAsCompleteUseCase(date)
                _readingState.update { currentState ->
                    if (currentState is DailyReadingUiState.Success) {
                        currentState.copy(isReadingCompleted = true)
                    } else {
                        currentState
                    }
                }
            } catch (e: Exception) {
                _readingState.value = DailyReadingUiState.Error(e.message ?: "Erro desconhecido")
            }
        }
    }

    private fun updateVerseFavoriteState(verse: Verses) {
        _bibleResponses.value = _bibleResponses.value.map { bibleResponse ->
            bibleResponse.copy(
                verses = bibleResponse.verses.map {
                    if (it == verse) {
                        it.copy(isFavorite = !it.isFavorite)
                    } else {
                        it
                    }
                }
            )
        }
    }

    fun verifyDailyIsReading() : Boolean {
        return _completedReadings.value.contains(selectedDate.value?.formatDate("yyy-MM-dd"))
    }

    fun saveReadingsFromImage(bitmap: Bitmap) {
        viewModelScope.launch {
            _isLoading.value = true
            _uiMessage.value = null

            try {
                val success = saveReadingsFromImageUseCase(bitmap)

                if (success) {
                    _uiMessage.value = "Leituras salvas com sucesso!"
                } else {
                    _uiMessage.value = "Erro ao salvar as leituras."
                }
            } catch (e: Exception) {
                _uiMessage.value = "Erro: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}
