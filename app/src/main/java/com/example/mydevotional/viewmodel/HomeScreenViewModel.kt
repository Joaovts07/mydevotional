package com.example.mydevotional.viewmodel

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mydevotional.model.BibleResponse
import com.example.mydevotional.model.Verses
import com.example.mydevotional.usecase.GetVersesForDayUseCase
import com.example.mydevotional.usecase.SaveReadingsFromImageUseCase
import com.example.mydevotional.usecase.ToggleFavoriteVerseUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    private val getVersesForDayUseCase: GetVersesForDayUseCase,
    private val toggleFavoriteVerseUseCase: ToggleFavoriteVerseUseCase,
    private val saveReadingsFromImageUseCase: SaveReadingsFromImageUseCase
) : ViewModel() {

    private val _bibleResponses = MutableStateFlow<List<BibleResponse>>(emptyList())
    val bibleResponse: StateFlow<List<BibleResponse>> = _bibleResponses.asStateFlow()

    private val _selectedDate = MutableStateFlow(Date())
    val selectedDate: StateFlow<Date> = _selectedDate.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _loadFailed = MutableStateFlow(false)
    val loadFailed: StateFlow<Boolean> = _loadFailed.asStateFlow()

    private val _uiMessage = MutableStateFlow<String?>(null)
    val uiMessage: StateFlow<String?> = _uiMessage.asStateFlow()

    private var loadJob: Job? = null

    init {
        loadVerses()
    }

    fun selectDate(newDate: Date) {
        _selectedDate.value = newDate
        loadVerses()
    }

    fun retry() {
        loadVerses()
    }

    // Cancels the previous load so a slow response for an older date can't overwrite a newer one.
    private fun loadVerses() {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            _isLoading.value = true
            _loadFailed.value = false
            getVersesForDayUseCase(_selectedDate.value)
                .onSuccess { _bibleResponses.value = it }
                .onFailure {
                    _bibleResponses.value = emptyList()
                    _loadFailed.value = true
                }
            _isLoading.value = false
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

    fun saveReadingsFromImage(bitmap: Bitmap) {
        viewModelScope.launch {
            _isLoading.value = true
            _uiMessage.value = null
            _uiMessage.value = saveReadingsFromImageUseCase(bitmap).fold(
                onSuccess = { days -> "$days dias de leitura salvos com sucesso!" },
                onFailure = { "Erro ao salvar as leituras." }
            )
            _isLoading.value = false
            loadVerses()
        }
    }

    fun messageShown() {
        _uiMessage.value = null
    }
}
