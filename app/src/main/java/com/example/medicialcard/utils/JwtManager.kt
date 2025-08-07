package com.example.medicialcard.utils

import android.content.Context
import android.content.SharedPreferences

object JwtManager {
    private lateinit var sharedPreferences: SharedPreferences

    fun init(context: Context){
        this.sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }


    private const val PREFS_NAME = "jwt_prefs"
    private const val KEY_JWT_TOKEN = "jwt_token"
    private const val KEY_PATIENT_ID = "patient_id"


    fun saveToken(token: String) {
        sharedPreferences.edit().putString(KEY_JWT_TOKEN, token).apply()
    }

    fun getToken(): String? {
        return sharedPreferences.getString(KEY_JWT_TOKEN, null)
    }

    fun clearToken() {
        sharedPreferences.edit().remove(KEY_JWT_TOKEN).apply()
        sharedPreferences.edit().remove(KEY_PATIENT_ID).apply()
    }

    fun hasToken(): Boolean {
        return sharedPreferences.contains(KEY_JWT_TOKEN)
    }

    fun savePatientId(id: Long){
        sharedPreferences.edit().putString(KEY_PATIENT_ID, id.toString()).apply()
    }

    fun getCurrentPatientId(): String?{
        return sharedPreferences.getString(KEY_PATIENT_ID, null)
    }
}