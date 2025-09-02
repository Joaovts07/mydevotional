package com.example.mydevotional.usecase


import android.graphics.Bitmap
import android.util.Log
import com.example.mydevotional.BibleBook
import com.example.mydevotional.BibleBooks
import com.example.mydevotional.repositorie.BibleRepository
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.tasks.await
import java.util.Calendar
import java.util.regex.Pattern
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SaveReadingsFromImageUseCase @Inject constructor(
    private val bibleRepository: BibleRepository
) {
    suspend operator fun invoke(bitmap: Bitmap): Boolean {
        val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
        val image = InputImage.fromBitmap(bitmap, 0)

        val result = recognizer.process(image).await()
        val rawText = result.text

        val passagesMap = parseImageText(rawText)

        if (passagesMap.isEmpty()) {
            return false
        }

        val date = passagesMap.keys.first()
        val passages = passagesMap[date] ?: emptyList()

        Log.e("passages", passages.toString())
        Log.e("date", date)
        return true
        //return bibleRepository.savePassages(date, passages)
    }

    private fun parseImageText(text: String): Map<String, List<Map<String, Any>>> {
        val parsedReadings = mutableMapOf<String, List<Map<String, Any>>>()
        val lines = text.split("\n")

        val bibleBookMap = BibleBooks.books.associateBy { it.name }

        val dateRegex = "(\\d{1,2})/(JAN|FEV|MAR|ABR|MAI|JUN|JUL|AGO|SET|OUT|NOV|DEZ)"
        val passageRegex = "(.*?)\\s+(\\d+)(?:-(\\d+))?(?::(\\d+)(?:-(\\d+))?)?"

        var currentDateString: String? = null
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)

        lines.forEach { line ->
            val dateMatch = Pattern.compile(dateRegex, Pattern.CASE_INSENSITIVE).matcher(line)
            if (dateMatch.find()) {
                val day = dateMatch.group(1)
                val month = dateMatch.group(2)
                val monthNumber = when (month?.uppercase()) {
                    "JAN" -> 1
                    "FEV" -> 2
                    "MAR" -> 3
                    "ABR" -> 4
                    "MAI" -> 5
                    "JUN" -> 6
                    "JUL" -> 7
                    "AGO" -> 8
                    "SET" -> 9
                    "OUT" -> 10
                    "NOV" -> 11
                    "DEZ" -> 12
                    else -> null
                }
                if (day != null && monthNumber != null) {
                    currentDateString = String.format("%d-%02d-%s", currentYear, monthNumber, day)
                }
            }

            if (currentDateString != null) {
                // Lógica de parsing das passagens para o dia atual
                val passages = parsePassagesInLine(line, bibleBookMap)
                if (passages.isNotEmpty()) {
                    parsedReadings[currentDateString!!] = passages
                }
            }
        }
        return parsedReadings
    }

    private fun parsePassagesInLine(line: String, bookMap: Map<String, BibleBook>): List<Map<String, Any>> {
        val passages = mutableListOf<Map<String, Any>>()

        val passageRegex = "(.*?)\\s+(\\d+)(?:-(\\d+))?(?::(\\d+)(?:-(\\d+))?)?"
        val matcher = Pattern.compile(passageRegex, Pattern.CASE_INSENSITIVE).matcher(line)

        while (matcher.find()) {
            val bookName = matcher.group(1)?.trim()
            val chapter = matcher.group(2)?.toIntOrNull()
            val bibleBook = bookMap[bookName]

            if (bookName != null && chapter != null && bibleBook != null) {
                val passageMap = mutableMapOf<String, Any>()
                passageMap["bookAbbr"] = bibleBook.abbreviation
                passageMap["chapter"] = chapter
                passageMap["type"] = "chapter"

                passages.add(passageMap)
            }
        }

        return passages
    }
}