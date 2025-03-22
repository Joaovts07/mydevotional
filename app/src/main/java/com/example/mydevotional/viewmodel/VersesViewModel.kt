package com.example.mydevotional.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mydevotional.BibleBook
import com.example.mydevotional.model.BibleResponse
import com.example.mydevotional.model.Verses
import com.example.mydevotional.usecase.FavoriteVerseUseCase
import com.example.mydevotional.usecase.GetBibleBooksUseCase
import com.example.mydevotional.usecase.GetBibleChaptersUseCase
import com.example.mydevotional.usecase.GetVerseBibleUseCase
import com.example.mydevotional.usecase.ToggleFavoriteVerseUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VersesViewModel @Inject constructor(
    private val getVerseBibleUseCase: GetVerseBibleUseCase,
    private val getBibleBooksUseCase: GetBibleBooksUseCase,
    private val getBibleChaptersUseCase: GetBibleChaptersUseCase,
    private val favoriteVerseUseCase: FavoriteVerseUseCase,
    private val toggleFavoriteVerseUseCase: ToggleFavoriteVerseUseCase
) : ViewModel() {

    private val _books = MutableStateFlow<List<BibleBook>>(emptyList())
    val books: StateFlow<List<BibleBook>> = _books

    private val _chapters = MutableStateFlow(0)
    val chapters: StateFlow<Int> = _chapters

    private val _bibleResponses = MutableStateFlow<List<BibleResponse>>(emptyList())
    val bibleResponses: StateFlow<List<BibleResponse>> = _bibleResponses

    private val _selectedBook = MutableStateFlow<BibleBook?>(null)
    val selectedBook: StateFlow<BibleBook?> = _selectedBook

    private val _selectedChapter = MutableStateFlow<Int?>(null)
    val selectedChapter: StateFlow<Int?> = _selectedChapter

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _favoriteVerses = MutableStateFlow<List<Verses>>(emptyList())
    val favoriteVerses: StateFlow<List<Verses>> = _favoriteVerses.asStateFlow()

    init {
        fetchBooks()
        verifyFavoriteVerses()
    }

    fun verifyFavoriteVerses() {
        viewModelScope.launch {
            _favoriteVerses.value = favoriteVerseUseCase.getFavoriteVerses()
        }
    }


    private fun fetchBooks() {
        viewModelScope.launch {
            _isLoading.value = true
            _books.value = getBibleBooksUseCase()
            _isLoading.value = false
        }
    }

    fun selectBook(book: BibleBook) {
        _selectedBook.value = book
        fetchChapters(book)
    }

    private fun fetchChapters(book: BibleBook) {
        viewModelScope.launch {
            _isLoading.value = true
            _chapters.value = getBibleChaptersUseCase(book)
            _isLoading.value = false
        }
    }

    fun selectChapter(book: String,chapter: Int) {
        _selectedChapter.value = chapter
        fetchVerses(book, chapter)
    }

    private fun fetchVerses(book: String,chapter: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            _bibleResponses.value = getVerseBibleUseCase(book, chapter)
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
}
