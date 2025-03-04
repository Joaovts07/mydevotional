package com.example.mydevotional.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mydevotional.BibleBook
import com.example.mydevotional.GetBibleContentUseCase
import com.example.mydevotional.ui.theme.Verse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VersesViewModel @Inject constructor(
    private val getBibleContentUseCase: GetBibleContentUseCase
) : ViewModel() {

    private val _books = MutableStateFlow<List<BibleBook>>(emptyList())
    val books: StateFlow<List<BibleBook>> = _books

    private val _chapters = MutableStateFlow(0)
    val chapters: StateFlow<Int> = _chapters

    private val _verses = MutableStateFlow<List<Verse>>(emptyList())
    val verses: StateFlow<List<Verse>> = _verses

    private val _selectedBook = MutableStateFlow<BibleBook?>(null)
    val selectedBook: StateFlow<BibleBook?> = _selectedBook

    private val _selectedChapter = MutableStateFlow<Int?>(null)
    val selectedChapter: StateFlow<Int?> = _selectedChapter

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        fetchBooks()
    }

    private fun fetchBooks() {
        viewModelScope.launch {
            _isLoading.value = true
            _books.value = getBibleContentUseCase.getBooks()
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
            _chapters.value = getBibleContentUseCase.getChapters(book)
            _isLoading.value = false
        }
    }

    fun selectChapter(chapter: Int) {
        _selectedChapter.value = chapter
        fetchVerses(chapter)
    }

    private fun fetchVerses(chapter: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            _verses.value = getBibleContentUseCase.getVerses(_selectedBook.value!!, chapter)
            _isLoading.value = false
        }
    }
}
