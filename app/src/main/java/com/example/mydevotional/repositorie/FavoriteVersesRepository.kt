package com.example.mydevotional.repositorie

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.mydevotional.model.Verses
import com.google.common.reflect.TypeToken
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow

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

    fun getFavoriteVersesFlow(): Flow<List<Verses>> {
        return context.dataStore.data.map { preferences ->
            val currentFavoritesJson = preferences[favoriteVersesKey] ?: "{}"
            val type = object : TypeToken<Map<String, String>>() {}.type
            val currentFavoritesMap: Map<String, String> = gson.fromJson(currentFavoritesJson, type) ?: emptyMap()

            currentFavoritesMap.values.map { verseJson ->
                gson.fromJson(verseJson, Verses::class.java).copy(isFavorite = true)
            }
        }
    }

    private fun generateVerseId(verse: Verses): String {
        return "${verse.bookId}_${verse.chapter}_${verse.verse}"
    }
}

