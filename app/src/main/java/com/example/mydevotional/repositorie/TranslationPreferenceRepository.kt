package com.example.mydevotional.repositorie

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.mydevotional.model.BibleTranslation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.translationDataStore: DataStore<Preferences>
    by preferencesDataStore(name = "bible_translation_prefs")

@Singleton
class TranslationPreferenceRepository @Inject constructor(
    private val context: Context
) {
    private val SELECTED_TRANSLATION_KEY = stringPreferencesKey("selected_bible_translation")

    // Retorna o Flow da tradução selecionada
    fun getTranslationPreference(): Flow<BibleTranslation> {
        return context.translationDataStore.data.map { preferences ->
            val apiCode = preferences[SELECTED_TRANSLATION_KEY] ?: BibleTranslation.WEB.apiCode // Default é WEB
            BibleTranslation.values().find { it.apiCode == apiCode } ?: BibleTranslation.WEB
        }
    }

    // Salva a tradução selecionada
    suspend fun setTranslationPreference(translation: BibleTranslation) {
        context.translationDataStore.edit { preferences ->
            preferences[SELECTED_TRANSLATION_KEY] = translation.apiCode
        }
    }
}