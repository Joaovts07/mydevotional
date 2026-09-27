package com.example.mydevotional.viewmodel

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mydevotional.extensions.formatDate
import com.example.mydevotional.model.BibleResponse
import com.example.mydevotional.model.Verses
import com.example.mydevotional.model.id
import com.example.mydevotional.model.withFavorites
import com.example.mydevotional.state.HomeUiState
import com.example.mydevotional.usecase.CompleteReadingsUseCase
import com.example.mydevotional.usecase.FavoriteVerseUseCase
import com.example.mydevotional.usecase.GetVersesForDayUseCase
import com.example.mydevotional.usecase.SaveReadingsFromImageUseCase
import com.example.mydevotional.usecase.ToggleFavoriteVerseUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    private val getVersesForDayUseCase: GetVersesForDayUseCase,
    private val toggleFavoriteVerseUseCase: ToggleFavoriteVerseUseCase,
    private val saveReadingsFromImageUseCase: SaveReadingsFromImageUseCase,
    private val completeReadingsUseCase: CompleteReadingsUseCase,
    favoriteVerseUseCase: FavoriteVerseUseCase
) : ViewModel() {

    private data class ReadingLoad(
        val readings: List<BibleResponse> = emptyList(),
        val isLoading: Boolean = false,
        val loadFailed: Boolean = false
    )

    private val selectedDate = MutableStateFlow(Date())
    private val readingLoad = MutableStateFlow(ReadingLoad())
    private val message = MutableStateFlow<String?>(null)
    private val favoriteIds = favoriteVerseUseCase.getFavoriteVersesFlow()
        .map { favorites -> favorites.map { it.id }.toSet() }

    val uiState: StateFlow<HomeUiState> = combine(
        selectedDate,
        readingLoad,
        favoriteIds,
        completeReadingsUseCase.getCompletedReadingsFlow(),
        message
    ) { date, load, favorites, completedDays, message ->
        HomeUiState(
            selectedDate = date,
            readings = load.readings.withFavorites(favorites),
            isLoading = load.isLoading,
            loadFailed = load.loadFailed,
            completedDays = completedDays,
            isReadingCompleted = date.toKey() in completedDays,
            message = message
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HomeUiState()
    )

    private var loadJob: Job? = null

    init {
        loadVerses()
    }

    fun selectDate(newDate: Date) {
        selectedDate.value = newDate
        loadVerses()
    }

    fun retry() {
        loadVerses()
    }

    // Cancels the previous load so a slow response for an older date can't overwrite a newer one.
    private fun loadVerses() {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            readingLoad.update { it.copy(isLoading = true, loadFailed = false) }
            readingLoad.value = getVersesForDayUseCase(selectedDate.value).fold(
                onSuccess = { ReadingLoad(readings = it) },
                onFailure = { ReadingLoad(loadFailed = true) }
            )
        }
    }

    fun toggleFavorite(verse: Verses) {
        viewModelScope.launch {
            toggleFavoriteVerseUseCase(verse)
        }
    }

    fun toggleReadingComplete() {
        val dateKey = selectedDate.value.toKey()
        viewModelScope.launch {
            val wasCompleted = dateKey in completeReadingsUseCase.getCompletedReadingsFlow().first()
            completeReadingsUseCase.toggleCompletion(dateKey)
            message.value = if (wasCompleted) "Leitura Desmarcada!" else "Leitura marcada como lida!"
        }
    }

    fun saveReadingsFromImage(bitmap: Bitmap) {
        viewModelScope.launch {
            readingLoad.update { it.copy(isLoading = true) }
            message.value = saveReadingsFromImageUseCase(bitmap).fold(
                onSuccess = { days -> "$days dias de leitura salvos com sucesso!" },
                onFailure = { "Erro ao salvar as leituras." }
            )
            loadVerses()
        }
    }

    fun messageShown() {
        message.value = null
    }

    private fun Date.toKey() = formatDate("yyyy-MM-dd")
}
