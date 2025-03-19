package com.example.mydevotional.repositorie

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import com.example.mydevotional.model.Verses
import kotlinx.coroutines.flow.Flow
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

    private val favoriteVersesKey = stringSetPreferencesKey("favorite_verses")

    val favoriteVerses: Flow<Set<String>> = context.dataStore.data
        .map { preferences ->
            preferences[favoriteVersesKey] ?: emptySet()
        }

    suspend fun toggleFavorite(verse: Verses) {
        val verseJson = gson.toJson(verse)
        val verseId = "${verse.book_id}_${verse.chapter}_${verse.verse}"

        context.dataStore.edit { preferences ->
            val currentFavorites = preferences[favoriteVersesKey] ?: emptySet()
            val updatedFavorites = if (currentFavorites.contains(verseJson)) { // Check for the serialized object
                currentFavorites - verseJson
            } else {
                currentFavorites + verseJson
            }
            preferences[favoriteVersesKey] = updatedFavorites
        }
    }

    suspend fun isVerseFavorite(verse: Verses): Boolean {
        val verseJson = gson.toJson(verse)
        val currentFavorites = context.dataStore.data.map { it[favoriteVersesKey] ?: emptySet() }.first()
        return currentFavorites.contains(verseJson)
    }

    suspend fun getFavoriteVerses(): List<Verses> {
        val favoriteVerseJsons = context.dataStore.data.map { it[favoriteVersesKey] ?: emptySet() }.first()
        return favoriteVerseJsons.map { verseJson ->
            gson.fromJson(verseJson, Verses::class.java)
        }
    }
}

