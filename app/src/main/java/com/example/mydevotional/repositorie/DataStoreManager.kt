package com.example.mydevotional.repositorie

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore

internal val FAVORITE_VERSES_KEY = stringPreferencesKey("favorite_verses_map")

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = "favorites_store",
    produceMigrations = { listOf(LegacyFavoritesMigration()) }
)
