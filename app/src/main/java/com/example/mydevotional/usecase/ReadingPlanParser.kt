package com.example.mydevotional.usecase

import com.example.mydevotional.BibleBook
import com.example.mydevotional.BibleBooks
import java.text.Normalizer

/**
 * Parses the OCR text of a printed reading plan, e.g.
 *
 * ```
 * 5/JAN Gênesis 1-3; Mateus 2
 * 6/JAN João 3:16-18
 * ```
 *
 * into bible-api references keyed by "yyyy-MM-dd":
 * `{"2026-01-05": ["Genesis 1", "Genesis 2", "Genesis 3", "Matthew 2"], "2026-01-06": ["John 3:16-18"]}`.
 */
object ReadingPlanParser {

    private val months = listOf("JAN", "FEV", "MAR", "ABR", "MAI", "JUN", "JUL", "AGO", "SET", "OUT", "NOV", "DEZ")

    private val dateRegex = Regex("""(\d{1,2})\s*/\s*(${months.joinToString("|")})""", RegexOption.IGNORE_CASE)

    private val booksByNormalizedName: Map<String, BibleBook> =
        BibleBooks.books.associateBy { normalize(it.name) }

    // Longest names first so "1 joao" wins over "joao".
    private val passageRegex: Regex = run {
        val names = booksByNormalizedName.keys
            .sortedByDescending { it.length }
            .joinToString("|") { Regex.escape(it) }
        Regex("""(?<![\p{L}\d])($names)\s+(\d+)(?:\s*-\s*(\d+))?(?:\s*:\s*(\d+)(?:\s*-\s*(\d+))?)?""")
    }

    fun parse(text: String, year: Int): Map<String, List<String>> {
        val readings = linkedMapOf<String, MutableList<String>>()
        var currentDate: String? = null

        text.lines().forEach { rawLine ->
            var line = normalize(rawLine)
            dateRegex.find(line)?.let { match ->
                val day = match.groupValues[1].toInt()
                val month = months.indexOf(match.groupValues[2].uppercase()) + 1
                currentDate = "%04d-%02d-%02d".format(year, month, day)
                line = line.removeRange(match.range)
            }
            val date = currentDate ?: return@forEach
            val passages = parsePassages(line)
            if (passages.isNotEmpty()) {
                readings.getOrPut(date) { mutableListOf() } += passages
            }
        }
        return readings
    }

    private fun parsePassages(line: String): List<String> {
        return passageRegex.findAll(line).flatMap { match ->
            val (name, chapter, chapterEnd, verse, verseEnd) = match.destructured
            val book = booksByNormalizedName.getValue(name).englishName
            when {
                verse.isNotEmpty() -> {
                    val verses = if (verseEnd.isNotEmpty()) "$verse-$verseEnd" else verse
                    sequenceOf("$book $chapter:$verses")
                }
                chapterEnd.isNotEmpty() -> {
                    (chapter.toInt()..chapterEnd.toInt()).asSequence().map { "$book $it" }
                }
                else -> sequenceOf("$book $chapter")
            }
        }.toList()
    }

    /** Lowercase without accents, since OCR often drops or mangles them. */
    private fun normalize(value: String): String {
        return Normalizer.normalize(value, Normalizer.Form.NFD)
            .replace(Regex("\\p{Mn}+"), "")
            .lowercase()
    }
}
