package com.dlapps.acahelp.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.time.Instant


val Context.dataStore by preferencesDataStore(name = "auth_prefs")
class TokenManager (val context: Context){

    companion object {
        val JWT_TOKEN_KEY = stringPreferencesKey("JWT_TOKEN")
        val JWT_TOKEN_EXPIRES_AT_KEY = stringPreferencesKey("EXPIRES_AT")
    }

    suspend fun saveToken(token: String, expiresAt: String){
        context.dataStore.edit { preferences ->
            preferences[JWT_TOKEN_KEY] = token
            preferences[JWT_TOKEN_EXPIRES_AT_KEY] = expiresAt
        }
    }
    val tokenFlow: Flow<String?> = context.dataStore.data
        .map { preferences ->
            preferences[JWT_TOKEN_KEY]
        }

    val expiresIn: Flow<String?> = context.dataStore.data.map { it[JWT_TOKEN_EXPIRES_AT_KEY] }

    // MÉTODO PARA BORRAR (Logout)
    suspend fun deleteToken() {
        context.dataStore.edit { it.remove(JWT_TOKEN_KEY) }
    }
    suspend fun isSessionValid(): Boolean{
        val prefs = context.dataStore.data.first()

        val token = prefs[JWT_TOKEN_KEY]
        val expiresAt = prefs[JWT_TOKEN_EXPIRES_AT_KEY]
        if (token.isNullOrBlank() || expiresAt.isNullOrBlank()) {
            return false
        }


        return try {
            val expirationInstant = Instant.parse(expiresAt)
            expirationInstant > Instant.now()

        } catch (e: Exception) {
            false
        }
    }



}