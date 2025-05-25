package com.example.nagomiatoru.data

import android.R
import android.content.Context
import android.content.SharedPreferences
import com.example.nagomiatoru.models.User

object SessionManager {
    private const val PREF_NAME = "user_session"
    private const val KEY_UID = "uid"
    private const val KEY_EMAIL = "email"
    private const val KEY_NAME = "name"
    private const val KEY_PHONE = "phone"
    private const val KEY_BIRTH_DATE = "birth_date"
    private const val KEY_SEX = "sex"

    private lateinit var prefs: SharedPreferences
    private const val TAG = "SessionManager"

    fun init(context: Context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    fun saveSession(user: User) {
        val success = prefs.edit().apply {
            putString(KEY_UID, user.uid)
            putString(KEY_EMAIL, user.email)
            putString(KEY_NAME, user.name)
            putString(KEY_PHONE, user.phone)
            putString(KEY_BIRTH_DATE, user.birthDate)
            putString(KEY_SEX, user.sex)
        }.commit() // Cambiado a commit() que es síncrono
    }

    fun getUid(): String? = prefs.getString(KEY_UID, null)
    fun getEmail(): String? = prefs.getString(KEY_EMAIL, null)
    fun getName(): String? = prefs.getString(PREF_NAME, null)
    fun getPhone(): String? = prefs.getString(KEY_PHONE, null)
    fun getBirthDate(): String? = prefs.getString(KEY_BIRTH_DATE, null)
    fun getSex(): String? = prefs.getString(KEY_SEX, null)

    fun clearSession() {
        prefs.edit().clear().apply()
    }
}