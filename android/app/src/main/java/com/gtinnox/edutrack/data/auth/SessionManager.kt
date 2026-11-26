package com.gtinnox.edutrack.data.auth

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

class SessionManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("edutrack_secure_session", Context.MODE_PRIVATE)

    private val _currentUserId = MutableStateFlow<String?>(prefs.getString(KEY_USER_ID, null))
    val currentUserId: StateFlow<String?> = _currentUserId.asStateFlow()

    fun getActiveUserId(): String? = _currentUserId.value

    fun isAuthenticated(): Boolean = !_currentUserId.value.isNullOrBlank()

    fun login(userId: String) {
        val sessionToken = UUID.randomUUID().toString()
        prefs.edit()
            .putString(KEY_USER_ID, userId)
            .putString(KEY_SESSION_TOKEN, sessionToken)
            .putLong(KEY_LOGIN_TIMESTAMP, System.currentTimeMillis())
            .apply()
        _currentUserId.value = userId
    }

    fun logout() {
        prefs.edit()
            .remove(KEY_USER_ID)
            .remove(KEY_SESSION_TOKEN)
            .remove(KEY_LOGIN_TIMESTAMP)
            .apply()
        _currentUserId.value = null
    }

    companion object {
        private const val KEY_USER_ID = "active_user_id"
        private const val KEY_SESSION_TOKEN = "session_token"
        private const val KEY_LOGIN_TIMESTAMP = "login_timestamp"
    }
}
