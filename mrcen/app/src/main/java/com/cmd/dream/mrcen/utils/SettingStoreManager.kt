package com.cmd.dream.mrcen.utils

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.cmd.dream.mrcen.model.SettingModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking
import java.io.IOException


class SettingStoreManager(private val context: Context) {
    companion object {
        private const val SETTING_PREFERENCES_NAME = "setting_preferences"
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = SETTING_PREFERENCES_NAME)
        val VOL_KEY = intPreferencesKey("VOL_KEY")
        val SETTING = stringPreferencesKey("SETTING")
    }

    suspend fun saveInt(key: Preferences.Key<Int>, value: Int) {
        context.dataStore.edit { preferences ->
            preferences[key] = value
        }
    }

    suspend fun saveStr(key: Preferences.Key<String>, value: String) {
        context.dataStore.edit { preferences ->
            preferences[key] = value
        }
    }

    suspend fun saveSetting(value: SettingModel) {
        context.dataStore.edit { preferences ->
            preferences[SETTING] = GsonUtils.gson.toJson(value)
        }
    }

    fun getIntData(key: Preferences.Key<Int>): Flow<Int> {
        return context.dataStore.data.catch { exception ->
            // dataStore.data throws an IOException when an error is encountered when reading data
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }.map {
            it[key]?: 0
        }
    }

    fun getSetting(): Flow<SettingModel> {
        return context.dataStore.data.catch { exception ->
            // dataStore.data throws an IOException when an error is encountered when reading data
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }.map { preferences ->
            Log.d("cenmingdi", "map")
            GsonUtils.gson.fromJson(preferences[SETTING]?: "", SettingModel::class.java)?: SettingModel()
        }
    }
}