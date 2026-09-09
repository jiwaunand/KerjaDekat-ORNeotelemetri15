package com.example.myjobseeker.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")

class UserPreferences(private val context: Context) {
    companion object {
        private val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
        private val USER_ID = intPreferencesKey("user_id")
        
        fun getThemeKey(userId: Int) = booleanPreferencesKey("theme_user_$userId")
    }

    val isLoggedIn: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[IS_LOGGED_IN] ?: false
        }

    val userId: Flow<Int> = context.dataStore.data
        .map { preferences ->
            preferences[USER_ID] ?: -1
        }

    fun getTheme(userId: Int): Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[getThemeKey(userId)] ?: false
        }

    suspend fun setTheme(userId: Int, isDark: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[getThemeKey(userId)] = isDark
        }
    }

    suspend fun saveLoginSession(userId: Int) {
        context.dataStore.edit { preferences ->
            preferences[IS_LOGGED_IN] = true
            preferences[USER_ID] = userId
        }
    }

    suspend fun clearLoginSession() {
        context.dataStore.edit { preferences ->
            preferences[IS_LOGGED_IN] = false
            preferences[USER_ID] = -1
        }
    }
}
