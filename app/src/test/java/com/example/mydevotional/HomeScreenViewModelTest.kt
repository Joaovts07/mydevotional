package com.example.mydevotional

import com.example.mydevotional.model.BibleResponse
import com.example.mydevotional.model.Verses
import com.example.mydevotional.usecase.GetVersesForDayUseCase
import com.example.mydevotional.usecase.SaveReadingsFromImageUseCase
import com.example.mydevotional.usecase.ToggleFavoriteVerseUseCase
import com.example.mydevotional.viewmodel.HomeScreenViewModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import java.io.IOException
import java.util.Date
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class HomeScreenViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val verse = Verses(bookName = "John", chapter = 3, verse = 16, text = "For God so loved...")
    private val reading = BibleResponse(reference = "John 3:16", verses = listOf(verse))

    private val getVersesForDay = mockk<GetVersesForDayUseCase>()
    private val toggleFavorite = mockk<ToggleFavoriteVerseUseCase>(relaxed = true)
    private val saveReadingsFromImage = mockk<SaveReadingsFromImageUseCase>()

    private fun createViewModel() =
        HomeScreenViewModel(getVersesForDay, toggleFavorite, saveReadingsFromImage)

    @Test
    fun init_loadsTodaysReading() = runTest {
        coEvery { getVersesForDay(any()) } returns Result.success(listOf(reading))

        val viewModel = createViewModel()
        advanceUntilIdle()

        assertEquals(listOf(reading), viewModel.bibleResponse.value)
        assertFalse(viewModel.loadFailed.value)
        assertFalse(viewModel.isLoading.value)
    }

    @Test
    fun loadFailure_setsErrorAndRetryRecovers() = runTest {
        coEvery { getVersesForDay(any()) } returns Result.failure(IOException("offline"))
        val viewModel = createViewModel()
        advanceUntilIdle()

        assertTrue(viewModel.loadFailed.value)
        assertTrue(viewModel.bibleResponse.value.isEmpty())

        coEvery { getVersesForDay(any()) } returns Result.success(listOf(reading))
        viewModel.retry()
        advanceUntilIdle()

        assertFalse(viewModel.loadFailed.value)
        assertEquals(listOf(reading), viewModel.bibleResponse.value)
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

        assertEquals(listOf(newReading), viewModel.bibleResponse.value)
    }

    @Test
    fun toggleFavorite_flipsTheVerseState() = runTest {
        coEvery { getVersesForDay(any()) } returns Result.success(listOf(reading))
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.toggleFavorite(verse)
        advanceUntilIdle()

        coVerify { toggleFavorite(verse) }
        assertTrue(viewModel.bibleResponse.value.single().verses.single().isFavorite)
    }

    @Test
    fun saveReadingsFromImage_reportsResultAndClearsAfterShown() = runTest {
        coEvery { getVersesForDay(any()) } returns Result.success(emptyList())
        coEvery { saveReadingsFromImage(any()) } returns Result.success(3)
        val viewModel = createViewModel()

        viewModel.saveReadingsFromImage(mockk())
        advanceUntilIdle()

        assertEquals("3 dias de leitura salvos com sucesso!", viewModel.uiMessage.value)
        viewModel.messageShown()
        assertEquals(null, viewModel.uiMessage.value)
    }
}
