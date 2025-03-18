package com.example.mydevotional.repositorie

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import com.example.mydevotional.model.Verses
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FavoriteVersesRepository(context: Context) {

    private val dataStore = context.dataStore

    companion object {
        private val FAVORITE_VERSES_KEY = stringSetPreferencesKey("favorite_verses")
    }

    val favoriteVerses: Flow<Set<String>> = dataStore.data
        .map { preferences ->
            preferences[FAVORITE_VERSES_KEY] ?: emptySet()
        }

    suspend fun toggleFavorite(verse: Verses) {
        val verseId = "${verse.book_id}_${verse.chapter}_${verse.verse}"

        dataStore.edit { preferences ->
            val currentFavorites = preferences[FAVORITE_VERSES_KEY] ?: emptySet()
            val updatedFavorites = if (currentFavorites.contains(verseId)) {
                currentFavorites - verseId
            } else {
                currentFavorites + verseId
            }
            preferences[FAVORITE_VERSES_KEY] = updatedFavorites
        }
    }
}


