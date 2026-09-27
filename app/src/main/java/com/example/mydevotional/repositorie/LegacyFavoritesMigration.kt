package com.example.mydevotional.repositorie

import androidx.datastore.core.DataMigration
import androidx.datastore.preferences.core.Preferences
import com.example.mydevotional.BibleBooks
import com.example.mydevotional.model.Verses
import com.example.mydevotional.model.id
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * Favorites saved before [Verses.bookId] was read from the API are keyed "_<chapter>_<verse>",
 * so the same chapter and verse in different books collided. This recovers the book from the
 * stored book name and re-keys those entries. Entries whose book can't be found are kept as-is.
 */
class LegacyFavoritesMigration(private val gson: Gson = Gson()) : DataMigration<Preferences> {

    override suspend fun shouldMigrate(currentData: Preferences): Boolean {
        val json = currentData[FAVORITE_VERSES_KEY] ?: return false
        return decode(json).any { (key, value) -> key.isLegacyKey() && recover(value) != null }
    }

    override suspend fun migrate(currentData: Preferences): Preferences {
        val json = currentData[FAVORITE_VERSES_KEY] ?: return currentData
        return currentData.toMutablePreferences().apply {
            this[FAVORITE_VERSES_KEY] = gson.toJson(migrate(decode(json)))
        }
    }

    override suspend fun cleanUp() = Unit

    // Keeps the original order. If the verse was already favorited again under its new id,
    // that entry wins and the legacy one is dropped.
    private fun migrate(favorites: Map<String, String>): Map<String, String> {
        val result = LinkedHashMap<String, String>()
        favorites.forEach { (key, value) ->
            if (!key.isLegacyKey()) {
                result[key] = value
                return@forEach
            }
            val recovered = recover(value)
            val newKey = recovered?.id ?: key
            if (newKey !in favorites || newKey == key) {
                result.putIfAbsent(newKey, recovered?.let(gson::toJson) ?: value)
            }
        }
        return result
    }

    private fun recover(verseJson: String): Verses? {
        val verse = gson.fromJson(verseJson, Verses::class.java) ?: return null
        val book = BibleBooks.findByName(verse.bookName) ?: return null
        return verse.copy(bookId = book.abbreviation)
    }

    private fun decode(json: String): Map<String, String> =
        gson.fromJson(json, object : TypeToken<LinkedHashMap<String, String>>() {}.type) ?: emptyMap()

    private fun String.isLegacyKey() = startsWith("_")
}
