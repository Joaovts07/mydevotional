package com.example.mydevotional.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mydevotional.model.BibleResponse
import com.example.mydevotional.model.Verses
import com.example.mydevotional.usecase.FavoriteVerseUseCase
import com.example.mydevotional.usecase.GetVersesForDayUseCase
import com.example.mydevotional.usecase.ToggleFavoriteVerseUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    private val getVersesForDayUseCase: GetVersesForDayUseCase,
    private val favoriteVerseUseCase: FavoriteVerseUseCase,
    private val toggleFavoriteVerseUseCase: ToggleFavoriteVerseUseCase
) : ViewModel() {

    private val _bibleResponses = MutableStateFlow<List<BibleResponse>>(emptyList())
    val bibleResponse: StateFlow<List<BibleResponse>> = _bibleResponses.asStateFlow()

    private val _selectedDate = MutableStateFlow<Date?>(null)
    val selectedDate: StateFlow<Date?> = _selectedDate.asStateFlow()

    private val _completedReadings = MutableStateFlow<List<String>>(emptyList())
    val completedReadings: StateFlow<List<String>> = _completedReadings.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

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
            _completedReadings.value = emptyList()
        }
    }

    fun toggleFavorite(verse: Verses) {
        viewModelScope.launch {
            toggleFavoriteVerseUseCase(verse)
            updateVerseFavoriteState(verse)
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
}
