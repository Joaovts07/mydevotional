package com.example.mydevotional.usecase

import android.graphics.Bitmap
import com.example.mydevotional.repositorie.BibleRepository
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.tasks.await
import java.util.Calendar
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SaveReadingsFromImageUseCase @Inject constructor(
    private val bibleRepository: BibleRepository
) {
    /** Returns the number of days saved, or a failure if nothing could be read from the image. */
    suspend operator fun invoke(bitmap: Bitmap): Result<Int> {
        val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
        val text = try {
            recognizer.process(InputImage.fromBitmap(bitmap, 0)).await().text
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            return Result.failure(e)
        } finally {
            recognizer.close()
        }

        val passagesByDate = ReadingPlanParser.parse(text, Calendar.getInstance().get(Calendar.YEAR))
        if (passagesByDate.isEmpty()) {
            return Result.failure(IllegalArgumentException("No readings found in the image"))
        }
        return bibleRepository.savePassages(passagesByDate).map { passagesByDate.size }
    }
}
