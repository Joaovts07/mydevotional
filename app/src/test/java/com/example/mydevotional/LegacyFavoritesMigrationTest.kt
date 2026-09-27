package com.example.mydevotional

import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.preferencesOf
import com.example.mydevotional.model.Verses
import com.example.mydevotional.repositorie.FAVORITE_VERSES_KEY
import com.example.mydevotional.repositorie.LegacyFavoritesMigration
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.test.runTest
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class LegacyFavoritesMigrationTest {

    private val gson = Gson()
    private val migration = LegacyFavoritesMigration(gson)

    // What the app stored before bookId was mapped: the field was written as "bookId" and was always empty.
    private fun legacyJson(bookName: String, chapter: Int, verse: Int) =
        """{"bookId":"","book_name":"$bookName","chapter":$chapter,"verse":$verse,"text":"...","isFavorite":false}"""

    private fun storeOf(vararg favorites: Pair<String, String>): Preferences =
        preferencesOf(FAVORITE_VERSES_KEY to gson.toJson(linkedMapOf(*favorites)))

    private fun Preferences.favorites(): Map<String, Verses> {
        val map: Map<String, String> =
            gson.fromJson(this[FAVORITE_VERSES_KEY], object : TypeToken<LinkedHashMap<String, String>>() {}.type)
        return map.mapValues { gson.fromJson(it.value, Verses::class.java) }
    }

    @Test
    fun rekeysLegacyEntriesByBookName_inBothTranslations() = runTest {
        val store = storeOf(
            "_3_16" to legacyJson("João", 3, 16),
            "_23_1" to legacyJson("Psalms", 23, 1),
            "_1_2" to legacyJson("Oséias", 1, 2)
        )
        assertTrue(migration.shouldMigrate(store))

        val favorites = migration.migrate(store).favorites()

        assertEquals(listOf("JHN_3_16", "PSA_23_1", "HOS_1_2"), favorites.keys.toList())
        assertEquals("JHN", favorites.getValue("JHN_3_16").bookId)
    }

    @Test
    fun keepsTheCurrentEntryWhenTheVerseWasFavoritedAgain() = runTest {
        val current = gson.toJson(Verses(bookId = "JHN", bookName = "João", chapter = 3, verse = 16, text = "new"))
        val store = storeOf("_3_16" to legacyJson("João", 3, 16), "JHN_3_16" to current)

        val favorites = migration.migrate(store).favorites()

        assertEquals(listOf("JHN_3_16"), favorites.keys.toList())
        assertEquals("new", favorites.getValue("JHN_3_16").text)
    }

    @Test
    fun keepsEntriesWhoseBookIsUnknown_andDoesNotRunAgainForThem() = runTest {
        val store = storeOf("_1_1" to legacyJson("Unknown", 1, 1))

        assertFalse(migration.shouldMigrate(store))
        assertEquals(listOf("_1_1"), migration.migrate(store).favorites().keys.toList())
    }

    @Test
    fun doesNothingWithoutLegacyEntries() = runTest {
        val current = gson.toJson(Verses(bookId = "GEN", bookName = "Gênesis", chapter = 1, verse = 1))

        assertFalse(migration.shouldMigrate(storeOf("GEN_1_1" to current)))
        assertFalse(migration.shouldMigrate(preferencesOf()))
    }
}
