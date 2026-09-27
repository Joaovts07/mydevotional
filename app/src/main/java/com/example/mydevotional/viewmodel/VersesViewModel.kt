package com.example.mydevotional.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mydevotional.BibleBook
import com.example.mydevotional.model.id
import com.example.mydevotional.model.Verses
import com.example.mydevotional.model.withFavorites
import com.example.mydevotional.state.VersesUiState
import com.example.mydevotional.usecase.FavoriteVerseUseCase
import com.example.mydevotional.usecase.GetBibleBooksUseCase
import com.example.mydevotional.usecase.GetBibleChaptersUseCase
import com.example.mydevotional.usecase.GetVerseBibleUseCase
import com.example.mydevotional.usecase.ToggleFavoriteVerseUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VersesViewModel @Inject constructor(
    private val getVerseBibleUseCase: GetVerseBibleUseCase,
    getBibleBooksUseCase: GetBibleBooksUseCase,
    private val getBibleChaptersUseCase: GetBibleChaptersUseCase,
    private val toggleFavoriteVerseUseCase: ToggleFavoriteVerseUseCase,
    favoriteVerseUseCase: FavoriteVerseUseCase
) : ViewModel() {

    val books: List<BibleBook> = getBibleBooksUseCase()

    private val _chapters = MutableStateFlow(0)
    val chapters: StateFlow<Int> = _chapters.asStateFlow()

    private val chapterLoad = MutableStateFlow(VersesUiState())
    private val favoriteIds = favoriteVerseUseCase.getFavoriteVersesFlow()
        .map { favorites -> favorites.map { it.id }.toSet() }

    val uiState: StateFlow<VersesUiState> = combine(chapterLoad, favoriteIds) { load, favorites ->
        load.copy(readings = load.readings.withFavorites(favorites))
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = VersesUiState()
    )

    private var selectedChapter: Pair<String, Int>? = null
    private var loadJob: Job? = null

    fun selectBook(book: BibleBook) {
        _chapters.value = getBibleChaptersUseCase(book)
    }

    fun selectChapter(book: String, chapter: Int) {
        selectedChapter = book to chapter
        loadVerses()
    }

    fun retry() {
        loadVerses()
    }

    private fun loadVerses() {
        val (book, chapter) = selectedChapter ?: return
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            chapterLoad.update { it.copy(isLoading = true, loadFailed = false) }
            chapterLoad.value = getVerseBibleUseCase(book, chapter).fold(
                onSuccess = { VersesUiState(readings = it) },
                onFailure = { VersesUiState(loadFailed = true) }
            )
        }
    }

    fun toggleFavorite(verse: Verses) {
        viewModelScope.launch {
            toggleFavoriteVerseUseCase(verse)
        }
    }
}
