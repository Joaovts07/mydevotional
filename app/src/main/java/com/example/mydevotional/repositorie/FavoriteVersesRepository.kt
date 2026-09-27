package com.example.mydevotional.repositorie

import android.content.Context
import androidx.datastore.preferences.core.edit
import com.example.mydevotional.model.Verses
import com.example.mydevotional.model.id
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

    suspend fun toggleFavorite(verse: Verses) {
        val verseId = verse.id
        val verseJson = gson.toJson(verse)

        context.dataStore.edit { preferences ->
            val currentFavoritesJson = preferences[FAVORITE_VERSES_KEY] ?: "{}"
            val currentFavoritesMap = gson.fromJson(currentFavoritesJson, mutableMapOf<String, String>().javaClass)

            if (currentFavoritesMap.containsKey(verseId)) {
                currentFavoritesMap.remove(verseId)
            } else {
                currentFavoritesMap[verseId] = verseJson
            }

            preferences[FAVORITE_VERSES_KEY] = gson.toJson(currentFavoritesMap)
        }
    }

    suspend fun isVerseFavorite(verse: Verses): Boolean {
        val verseId = verse.id
        val currentFavoritesJson = context.dataStore.data.map { it[FAVORITE_VERSES_KEY] ?: "{}" }.first()
        val currentFavoritesMap = gson.fromJson(currentFavoritesJson, mutableMapOf<String, String>().javaClass)
        return currentFavoritesMap.containsKey(verseId)
    }

    fun getFavoriteVersesFlow(): Flow<List<Verses>> {
        return context.dataStore.data.map { preferences ->
            val currentFavoritesJson = preferences[FAVORITE_VERSES_KEY] ?: "{}"
            val type = object : TypeToken<Map<String, String>>() {}.type
            val currentFavoritesMap: Map<String, String> = gson.fromJson(currentFavoritesJson, type) ?: emptyMap()

            currentFavoritesMap.values.map { verseJson ->
                gson.fromJson(verseJson, Verses::class.java).copy(isFavorite = true)
            }
        }
    }
}

