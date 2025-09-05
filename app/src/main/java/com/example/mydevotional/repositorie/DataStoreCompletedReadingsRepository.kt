package com.example.mydevotional.repositorie

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class CompletedReadingsRepositoryImpl @Inject constructor(private val context: Context) :
    CompletedReadingsRepository {

    override fun getCompletedReadings(): Flow<Set<String>> {
        return context.dataStore.data.map { preferences ->
            preferences[COMPLETED_READINGS_KEY] ?: emptySet()
        }
    }

    override suspend fun toggleReadingComplete(date: String) {
        context.dataStore.edit { preferences ->
            val currentReadings = preferences[COMPLETED_READINGS_KEY] ?: emptySet()
            if (currentReadings.contains(date)) {
                preferences[COMPLETED_READINGS_KEY] = currentReadings - date
            } else {
                preferences[COMPLETED_READINGS_KEY] = currentReadings + date
            }
        }
    }

    companion object {
        private val COMPLETED_READINGS_KEY = stringSetPreferencesKey("completed_readings")

    }
}