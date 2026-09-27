package com.example.mydevotional

import com.example.mydevotional.model.Verses
import com.example.mydevotional.model.id
import com.google.gson.Gson
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class VerseIdTest {

    private val gson = Gson()

    private fun parse(json: String) = gson.fromJson(json, Verses::class.java)

    @Test
    fun bookId_isReadFromTheApiResponse() {
        val verse = parse("""{"book_id":"JHN","book_name":"John","chapter":3,"verse":16,"text":"For God so loved..."}""")

        assertEquals("JHN", verse.bookId)
        assertEquals("JHN_3_16", verse.id)
    }

    @Test
    fun sameChapterAndVerseInDifferentBooks_haveDifferentIds() {
        val john = parse("""{"book_id":"JHN","chapter":3,"verse":16}""")
        val genesis = parse("""{"book_id":"GEN","chapter":3,"verse":16}""")

        assertNotEquals(john.id, genesis.id)
    }
}
