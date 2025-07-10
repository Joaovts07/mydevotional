package com.example.mydevotional.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mydevotional.extensions.formatDate
import com.example.mydevotional.model.BibleResponse
import com.example.mydevotional.model.BibleTranslation
import com.example.mydevotional.model.Verses
import com.example.mydevotional.state.DailyReadingUiState
import com.example.mydevotional.usecase.GetCompletedReadingsUseCase
import com.example.mydevotional.usecase.GetSelectedTranslationUseCase
import com.example.mydevotional.usecase.GetVersesForDayUseCase
import com.example.mydevotional.usecase.MarkReadingAsCompleteUseCase
import com.example.mydevotional.usecase.SetSelectedTranslationUseCase
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
    private val getCompletedReadingsUseCase: GetCompletedReadingsUseCase,
    private val markReadingAsCompleteUseCase: MarkReadingAsCompleteUseCase,
    private val getSelectedTranslationUseCase: GetSelectedTranslationUseCase,
    private val setSelectedTranslationUseCase: SetSelectedTranslationUseCase
) : ViewModel() {

    private val _bibleResponses = MutableStateFlow<List<BibleResponse>>(emptyList())
    val bibleResponse: StateFlow<List<BibleResponse>> = _bibleResponses.asStateFlow()

    private val _selectedDate = MutableStateFlow<Date?>(null)
    val selectedDate: StateFlow<Date?> = _selectedDate.asStateFlow()

    private val _completedReadings = MutableStateFlow<List<String>>(emptyList())
    val completedReadings: StateFlow<List<String>> = _completedReadings.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _uiState = MutableStateFlow<DailyReadingUiState>(DailyReadingUiState.Success())

    private val _selectedTranslation = MutableStateFlow(BibleTranslation.WEB) // Valor default
    val selectedTranslation: StateFlow<BibleTranslation> = _selectedTranslation.asStateFlow()

    init {
        loadVersesForToday()
        loadCompletedReadings()
        observeSelectedTranslation()
    }

    private fun observeSelectedTranslation() {
        viewModelScope.launch {
            getSelectedTranslationUseCase().collect { translation ->
                _selectedTranslation.value = translation
                // Recarrega os versículos com a nova tradução
                _selectedDate.value?.let { date ->
                    _isLoading.value = true
                    _bibleResponses.value = getVersesForDayUseCase(date)
                    _isLoading.value = false
                } ?: run {
                    // Se não houver data selecionada, recarrega o dia atual
                    loadVersesForToday()
                }
            }
        }
    }

    fun setTranslation(translation: BibleTranslation) {
        viewModelScope.launch {
            setSelectedTranslationUseCase(translation)
            // observeSelectedTranslation() vai reagir e recarregar
        }
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
            getCompletedReadingsUseCase().collect { reading ->
                _completedReadings.value = reading
            }
            _uiState.value = DailyReadingUiState.Success(true)
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
                _uiState.update { currentState ->
                    if (currentState is DailyReadingUiState.Success) {
                        currentState.copy(isReadingCompleted = true)
                    } else {
                        currentState
                    }
                }
            } catch (e: Exception) {
                _uiState.value = DailyReadingUiState.Error(e.message ?: "Erro desconhecido")
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
}
