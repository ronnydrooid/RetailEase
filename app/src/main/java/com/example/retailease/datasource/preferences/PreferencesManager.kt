package com.example.retailease.datasource.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
@Singleton
class PreferencesManager @Inject constructor(
    private val context: Context
) {
    private val PRINT_MODE = booleanPreferencesKey("printing_mode")
    private val OPEN_CASH_DRAWER = booleanPreferencesKey("open_cash_drawer")

    val printModeFlow: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[PRINT_MODE] ?: true
    }
    val openCashDrawerFlow: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[OPEN_CASH_DRAWER] ?: false
    }


    suspend fun changePrintMode(value: Boolean){
        context.dataStore.edit {preferences ->
            preferences[PRINT_MODE] = value
        }
    }

    suspend fun changeOpenCashDrawer(value: Boolean){
        context.dataStore.edit { preferences ->
            preferences[OPEN_CASH_DRAWER] = value
        }
    }
}