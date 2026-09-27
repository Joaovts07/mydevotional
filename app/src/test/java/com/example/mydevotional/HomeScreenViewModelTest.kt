package com.example.mydevotional

import com.example.mydevotional.extensions.formatDate
import com.example.mydevotional.model.BibleResponse
import com.example.mydevotional.model.Verses
import com.example.mydevotional.usecase.CompleteReadingsUseCase
import com.example.mydevotional.usecase.FavoriteVerseUseCase
import com.example.mydevotional.usecase.GetVersesForDayUseCase
import com.example.mydevotional.usecase.SaveReadingsFromImageUseCase
import com.example.mydevotional.usecase.ToggleFavoriteVerseUseCase
import com.example.mydevotional.viewmodel.HomeScreenViewModel
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import java.io.IOException
import java.util.Date
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class HomeScreenViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val verse = Verses(bookName = "John", chapter = 3, verse = 16, text = "For God so loved...")
    private val reading = BibleResponse(reference = "John 3:16", verses = listOf(verse))

    private val favorites = MutableStateFlow<List<Verses>>(emptyList())
    private val completedDays = MutableStateFlow<Set<String>>(emptySet())

    private val getVersesForDay = mockk<GetVersesForDayUseCase>()
    private val saveReadingsFromImage = mockk<SaveReadingsFromImageUseCase>()
    private val toggleFavorite = mockk<ToggleFavoriteVerseUseCase> {
        coEvery { this@mockk(any()) } answers {
            val toggled = firstArg<Verses>()
            favorites.update { current ->
                if (current.any { it.verse == toggled.verse }) current - toggled else current + toggled
            }
        }
    }
    private val favoriteVerse = mockk<FavoriteVerseUseCase> {
        every { getFavoriteVersesFlow() } returns favorites
    }
    private val completeReadings = mockk<CompleteReadingsUseCase> {
        every { getCompletedReadingsFlow() } returns completedDays
        coEvery { toggleCompletion(any()) } answers {
            val date = firstArg<String>()
            completedDays.update { if (date in it) it - date else it + date }
        }
    }

    // uiState is shared WhileSubscribed, so keep a collector alive like the screen would.
    private fun TestScope.createViewModel() =
        HomeScreenViewModel(getVersesForDay, toggleFavorite, saveReadingsFromImage, completeReadings, favoriteVerse)
            .also { viewModel ->
                backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiState.collect {} }
            }

    @Test
    fun init_loadsTodaysReading() = runTest {
        coEvery { getVersesForDay(any()) } returns Result.success(listOf(reading))

        val viewModel = createViewModel()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(listOf(reading), state.readings)
        assertFalse(state.loadFailed)
        assertFalse(state.isLoading)
    }

    @Test
    fun loadFailure_setsErrorAndRetryRecovers() = runTest {
        coEvery { getVersesForDay(any()) } returns Result.failure(IOException("offline"))
        val viewModel = createViewModel()
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.loadFailed)
        assertTrue(viewModel.uiState.value.readings.isEmpty())

        coEvery { getVersesForDay(any()) } returns Result.success(listOf(reading))
        viewModel.retry()
        advanceUntilIdle()

        assertFalse(viewModel.uiState.value.loadFailed)
        assertEquals(listOf(reading), viewModel.uiState.value.readings)
    }

    @Test
    fun selectDate_slowOlderResponseDoesNotOverwriteNewerOne() = runTest {
        val oldDate = Date(0)
        val newDate = Date(86_400_000)
        val slowOldResponse = CompletableDeferred<Result<List<BibleResponse>>>()
        val newReading = reading.copy(reference = "Genesis 1")
        coEvery { getVersesForDay(any()) } returns Result.success(emptyList())
        coEvery { getVersesForDay(oldDate) } coAnswers { slowOldResponse.await() }
        coEvery { getVersesForDay(newDate) } returns Result.success(listOf(newReading))
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.selectDate(oldDate)
        advanceUntilIdle()
        viewModel.selectDate(newDate)
        advanceUntilIdle()
        slowOldResponse.complete(Result.success(listOf(reading)))
        advanceUntilIdle()

        assertEquals(newDate, viewModel.uiState.value.selectedDate)
        assertEquals(listOf(newReading), viewModel.uiState.value.readings)
    }

    @Test
    fun toggleFavorite_updatesTheVerseFromTheFavoritesFlow() = runTest {
        coEvery { getVersesForDay(any()) } returns Result.success(listOf(reading))
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.toggleFavorite(verse)
        advanceUntilIdle()
        assertTrue(viewModel.uiState.value.readings.single().verses.single().isFavorite)

        // Unfavoriting from another screen must also reach this one.
        favorites.value = emptyList()
        advanceUntilIdle()
        assertFalse(viewModel.uiState.value.readings.single().verses.single().isFavorite)
    }

    @Test
    fun toggleReadingComplete_marksTheSelectedDateAndReports() = runTest {
        val date = Date(0)
        coEvery { getVersesForDay(any()) } returns Result.success(listOf(reading))
        val viewModel = createViewModel()
        viewModel.selectDate(date)
        advanceUntilIdle()
        assertFalse(viewModel.uiState.value.isReadingCompleted)

        viewModel.toggleReadingComplete()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state.isReadingCompleted)
        assertEquals(setOf(date.formatDate("yyyy-MM-dd")), state.completedDays)
        assertEquals("Leitura marcada como lida!", state.message)

        viewModel.messageShown()
        viewModel.toggleReadingComplete()
        advanceUntilIdle()

        assertFalse(viewModel.uiState.value.isReadingCompleted)
        assertEquals("Leitura Desmarcada!", viewModel.uiState.value.message)
    }

    @Test
    fun saveReadingsFromImage_reportsResultAndClearsAfterShown() = runTest {
        coEvery { getVersesForDay(any()) } returns Result.success(emptyList())
        coEvery { saveReadingsFromImage(any()) } returns Result.success(3)
        val viewModel = createViewModel()

        viewModel.saveReadingsFromImage(mockk())
        advanceUntilIdle()

        assertEquals("3 dias de leitura salvos com sucesso!", viewModel.uiState.value.message)
        viewModel.messageShown()
        advanceUntilIdle()
        assertNull(viewModel.uiState.value.message)
    }
}
