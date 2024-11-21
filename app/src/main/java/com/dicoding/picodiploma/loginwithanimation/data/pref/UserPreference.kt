package com.dicoding.picodiploma.loginwithanimation.data.pref

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Ekstensi untuk membuat DataStore
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "session")

class UserPreference private constructor(private val dataStore: DataStore<Preferences>) {

    // Singleton untuk instance UserPreference
    companion object {
        private val EMAIL_KEY = stringPreferencesKey("email")
        private val TOKEN_KEY = stringPreferencesKey("token")
        private val IS_LOGIN_KEY = booleanPreferencesKey("isLogin")

        @Volatile
        private var INSTANCE: UserPreference? = null

        fun getInstance(dataStore: DataStore<Preferences>): UserPreference {
            return INSTANCE ?: synchronized(this) {
                val instance = UserPreference(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }

    // Fungsi untuk menyimpan data session
    suspend fun saveSession(user: UserModel) {
        dataStore.edit { preferences ->
            preferences[EMAIL_KEY] = user.email
            preferences[TOKEN_KEY] = user.token
            preferences[IS_LOGIN_KEY] = true
        }

        if(dataStore.data == null) {
            Log.d("UserPreference", "Session saved: not found")
        } else {
            Log.d("UserPreference", "Session saved: ${dataStore.data}")
        }
    }

    // Fungsi untuk membaca session
    fun getSession(): Flow<UserModel> {
        return dataStore.data.map { preferences ->
            val email = preferences[EMAIL_KEY] ?: ""
            val token = preferences[TOKEN_KEY] ?: ""
            val isLogin = preferences[IS_LOGIN_KEY] ?: false

            UserModel(email, token, isLogin)
        }


    }

    // Fungsi untuk logout
    suspend fun logout() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}
