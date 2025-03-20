package com.example.mydevotional.repositorie

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.mydevotional.model.Verses
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton
import com.google.gson.Gson


@Singleton
class FavoriteVersesRepository @Inject constructor(
    private val context: Context,
    private val gson: Gson
) {

    private val favoriteVersesKey = stringPreferencesKey("favorite_verses_map")

    suspend fun toggleFavorite(verse: Verses) {
        val verseId = generateVerseId(verse)
        val verseJson = gson.toJson(verse)

        context.dataStore.edit { preferences ->
            val currentFavoritesJson = preferences[favoriteVersesKey] ?: "{}"
            val currentFavoritesMap = gson.fromJson(currentFavoritesJson, mutableMapOf<String, String>().javaClass)

            if (currentFavoritesMap.containsKey(verseId)) {
                currentFavoritesMap.remove(verseId)
            } else {
                currentFavoritesMap[verseId] = verseJson
            }

            preferences[favoriteVersesKey] = gson.toJson(currentFavoritesMap)
        }
    }

    suspend fun isVerseFavorite(verse: Verses): Boolean {
        val verseId = generateVerseId(verse)
        val currentFavoritesJson = context.dataStore.data.map { it[favoriteVersesKey] ?: "{}" }.first()
        val currentFavoritesMap = gson.fromJson(currentFavoritesJson, mutableMapOf<String, String>().javaClass)
        return currentFavoritesMap.containsKey(verseId)
    }

    suspend fun getFavoriteVerses(): List<Verses> {
        val currentFavoritesJson = context.dataStore.data.map { it[favoriteVersesKey] ?: "{}" }.first()
        val currentFavoritesMap = gson.fromJson(currentFavoritesJson, mutableMapOf<String, String>().javaClass)
        return currentFavoritesMap.values.map { gson.fromJson(it, Verses::class.java) }
    }

    private fun generateVerseId(verse: Verses): String {
        return "${verse.book_id}_${verse.chapter}_${verse.verse}"
    }
}

