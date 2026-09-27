package com.example.mydevotional

import com.example.mydevotional.model.BibleResponse
import com.example.mydevotional.model.Verses
import com.example.mydevotional.usecase.FavoriteVerseUseCase
import com.example.mydevotional.usecase.GetBibleBooksUseCase
import com.example.mydevotional.usecase.GetBibleChaptersUseCase
import com.example.mydevotional.usecase.GetVerseBibleUseCase
import com.example.mydevotional.usecase.ToggleFavoriteVerseUseCase
import com.example.mydevotional.viewmodel.VersesViewModel
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import java.io.IOException
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class VersesViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val book = BibleBooks.books.first()
    private val verse = Verses(bookName = "Genesis", chapter = 1, verse = 1, text = "In the beginning...")
    private val chapter = BibleResponse(reference = "Genesis 1", verses = listOf(verse))

    private val favorites = MutableStateFlow<List<Verses>>(emptyList())

    private val getVerseBible = mockk<GetVerseBibleUseCase>()
    private val getBibleBooks = mockk<GetBibleBooksUseCase> {
        every { this@mockk() } returns BibleBooks.books
    }
    private val getBibleChapters = mockk<GetBibleChaptersUseCase> {
        every { this@mockk(any()) } answers { firstArg<BibleBook>().chapters }
    }
    private val toggleFavorite = mockk<ToggleFavoriteVerseUseCase>(relaxed = true)
    private val favoriteVerse = mockk<FavoriteVerseUseCase> {
        every { getFavoriteVersesFlow() } returns favorites
    }

    private fun TestScope.createViewModel() =
        VersesViewModel(getVerseBible, getBibleBooks, getBibleChapters, toggleFavorite, favoriteVerse)
            .also { viewModel ->
                backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiState.collect {} }
            }

    @Test
    fun booksAndChapters_areAvailableWithoutLoading() = runTest {
        val viewModel = createViewModel()

        assertEquals(BibleBooks.books, viewModel.books)
        viewModel.selectBook(book)
        assertEquals(book.chapters, viewModel.chapters.value)
    }

    @Test
    fun selectChapter_loadsVersesMarkedWithFavorites() = runTest {
        coEvery { getVerseBible("Gênesis", 1) } returns Result.success(listOf(chapter))
        favorites.value = listOf(verse.copy(isFavorite = true))
        val viewModel = createViewModel()

        viewModel.selectChapter("Gênesis", 1)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertTrue(state.readings.single().verses.single().isFavorite)
    }

    @Test
    fun loadFailure_setsErrorAndRetryRecovers() = runTest {
        coEvery { getVerseBible(any(), any()) } returns Result.failure(IOException("offline"))
        val viewModel = createViewModel()

        viewModel.selectChapter("Gênesis", 1)
        advanceUntilIdle()
        assertTrue(viewModel.uiState.value.loadFailed)

        coEvery { getVerseBible(any(), any()) } returns Result.success(listOf(chapter))
        viewModel.retry()
        advanceUntilIdle()

        assertFalse(viewModel.uiState.value.loadFailed)
        assertEquals(listOf(chapter), viewModel.uiState.value.readings)
    }
}
