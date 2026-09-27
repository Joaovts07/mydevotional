package com.example.mydevotional

import com.example.mydevotional.usecase.ReadingPlanParser
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ReadingPlanParserTest {

    @Test
    fun parse_zeroPadsDateAndExpandsChapterRanges() {
        val result = ReadingPlanParser.parse("5/JAN Gênesis 1-3; Mateus 2", year = 2026)

        assertEquals(
            mapOf("2026-01-05" to listOf("Genesis 1", "Genesis 2", "Genesis 3", "Matthew 2")),
            result
        )
    }

    @Test
    fun parse_keepsVerseRanges() {
        val result = ReadingPlanParser.parse("12/DEZ João 3:16-18", year = 2026)

        assertEquals(mapOf("2026-12-12" to listOf("John 3:16-18")), result)
    }

    @Test
    fun parse_prefersNumberedBooksAndIgnoresMissingAccents() {
        val result = ReadingPlanParser.parse("1/fev 1 Joao 2\nGenesis 4", year = 2026)

        assertEquals(mapOf("2026-02-01" to listOf("1 John 2", "Genesis 4")), result)
    }

    @Test
    fun parse_groupsPassagesUnderTheirDates() {
        val text = """
            5/JAN
            Gênesis 1
            Salmos 1
            6/JAN Êxodo 1
        """.trimIndent()

        val result = ReadingPlanParser.parse(text, year = 2026)

        assertEquals(
            mapOf(
                "2026-01-05" to listOf("Genesis 1", "Psalms 1"),
                "2026-01-06" to listOf("Exodus 1"),
            ),
            result
        )
    }

    @Test
    fun parse_ignoresPassagesBeforeAnyDateAndUnknownBooks() {
        val result = ReadingPlanParser.parse("Gênesis 1\n7/JAN Plano de leitura 3", year = 2026)

        assertTrue(result.isEmpty())
    }
}
