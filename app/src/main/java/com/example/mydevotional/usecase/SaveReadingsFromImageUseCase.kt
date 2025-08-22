package com.example.mydevotional.usecase

import android.graphics.Bitmap
import com.example.mydevotional.BibleBooks
import com.example.mydevotional.repositorie.BibleRepository
import javax.inject.Inject
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.tasks.await
import javax.inject.Singleton

@Singleton
class SaveReadingsFromImageUseCase @Inject constructor(
    private val bibleRepository: BibleRepository
) {
    suspend operator fun invoke(bitmap: Bitmap, date: String): Boolean {
        // Step 1: Initialize the TextRecognizer
        val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
        val image = InputImage.fromBitmap(bitmap, 0) // O ângulo de rotação é 0

        // Step 2: Process the image to get text
        val result = recognizer.process(image).await()
        val rawText = result.text

        // Step 3: Parse the text to extract Bible passages
        val passages = parseBiblePassages(rawText)

        // Step 4: Save the structured passages to Firestore
        return bibleRepository.savePassages(date, passages) // Você precisará criar esta função
    }

    private fun parseBiblePassages(text: String): List<Map<String, Any>> {
        val parsedPassages = mutableListOf<Map<String, Any>>()
        val lines = text.split("\n")

        val bibleBookMap = BibleBooks.books.associateBy { it.name }

        lines.forEach { line ->
            // Exemplo de lógica de parsing (pode precisar ser mais complexa)
            // Regex para capturar "Livro Capítulo" ou "Livro Capítulo:Verso"
            val regex = "(\\d?\\s?[a-zA-ZáàâãéèêíïóôõöúüçÁÀÂÃÉÈÊÍÏÓÔÕÖÚÜÇ]+)\\s+(\\d+)"
            val matchResult = Regex(regex).find(line)

            matchResult?.let {
                val bookName = it.groupValues[1].trim()
                val chapter = it.groupValues[2].toInt()
                val bibleBook = bibleBookMap[bookName]

                if (bibleBook != null) {
                    val passage = mapOf(
                        "bookAbbr" to bibleBook.abbreviation,
                        "chapter" to chapter,
                        "type" to "chapter" // Assumindo capítulo inteiro por simplicidade
                    )
                    parsedPassages.add(passage)
                }
            }
        }
        return parsedPassages
    }
}