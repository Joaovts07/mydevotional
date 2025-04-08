package com.example.mydevotional.repositorie

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class DataStoreCompletedReadingsRepository @Inject constructor(private val context: Context) :
    CompletedReadingsRepository {

    override suspend fun markReadingAsComplete(date: String) {
        context.dataStore.edit { preferences ->
            val currentReadings = preferences[COMPLETED_READINGS_KEY] ?: emptySet()
            preferences[COMPLETED_READINGS_KEY] = currentReadings + date
        }
    }

    override fun getCompletedReadings(): Flow<List<String>> {
        return context.dataStore.data.map { preferences ->
            (preferences[COMPLETED_READINGS_KEY] ?: emptySet()).toList()
        }
    } companion object {
        private val COMPLETED_READINGS_KEY = stringSetPreferencesKey("completed_readings")

    }
}