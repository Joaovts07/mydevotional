package com.example.mydevotional.usecase

import android.graphics.Bitmap
import android.util.Log
import com.example.mydevotional.BibleBooks
import com.example.mydevotional.repositorie.BibleRepository
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.tasks.await
import java.util.Calendar
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SaveReadingsFromImageUseCase @Inject constructor(
    private val bibleRepository: BibleRepository
) {
    suspend operator fun invoke(bitmap: Bitmap): Boolean {
        val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
        val image = InputImage.fromBitmap(bitmap, 0)

        return try {
            val result = recognizer.process(image).await()
            val rawText = result.text

            val readings = parseImageText(rawText)

            if (readings.isEmpty()) {
                Log.d("SaveReadings", "No readings found in text: $rawText")
                return false
            }

            Log.d("SaveReadings", "Parsed readings: $readings")
            bibleRepository.saveWeeklyReadings(readings)
        } catch (e: Exception) {
            Log.e("SaveReadings", "Error processing image", e)
            false
        }
    }

    private fun parseImageText(text: String): List<Pair<String, List<String>>> {
        val lines = text.split("\n")
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        val readings = mutableListOf<Pair<String, List<String>>>()

        // Map Portuguese book names to English/API names or Abbreviations
        val bookMap = BibleBooks.books.associate { it.name.lowercase() to it.abbreviation }

        // Regex for "DD/MM Book Chapter to Chapter" or "DD/MM Book Chapter"
        // Matches patterns like "02/03 Lucas 13 a 15" or "03/03 Lucas 16"
        val lineRegex = """(\d{1,2})/(\d{1,2})\s+([a-zA-Z\s]+?)\s+(\d+)(?:\s+a\s+(\d+))?""".toRegex(RegexOption.IGNORE_CASE)

        lines.forEach { line ->
            val match = lineRegex.find(line.trim())
            if (match != null) {
                val (day, month, bookNameRaw, startChapter, endChapter) = match.destructured
                val formattedDate = String.format("%d-%02d-%02d", currentYear, month.toInt(), day.toInt())
                
                val bookName = bookNameRaw.trim().lowercase()
                val abbreviation = bookMap[bookName] ?: bookName // Fallback to raw name if not found in map
                
                val passages = mutableListOf<String>()
                val start = startChapter.toInt()
                val end = endChapter.takeIf { it.isNotEmpty() }?.toInt() ?: start
                
                for (ch in start..end) {
                    passages.add("$abbreviation $ch")
                }

                readings.add(formattedDate to passages)
            }
        }
        return readings
    }
}
