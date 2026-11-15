package com.akram.babsharhisab.core.security

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

data class BackupConfigState(
    val webAppUrl: String = "",
    val secretToken: String = "BABSHAR_HISAB_SECRET_2026",
    val autoBackupEnabled: Boolean = true,
    val includeDepreciationInProfit: Boolean = true,
    val useClassicFont: Boolean = false
) {
    val isConfigured: Boolean
        get() = webAppUrl.trim().startsWith("https://script.google.com/macros/s/")
}

@Singleton
class SecureConfigStore @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val prefs: SharedPreferences by lazy {
        try {
            val masterKey = MasterKey.Builder(context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()
            EncryptedSharedPreferences.create(
                context,
                PREFS_FILE_NAME,
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
        } catch (e: Exception) {
            context.getSharedPreferences(PREFS_FILE_NAME_FALLBACK, Context.MODE_PRIVATE)
        }
    }

    private val _configFlow = MutableStateFlow(readConfig())
    val configFlow: StateFlow<BackupConfigState> = _configFlow.asStateFlow()

    private fun readConfig(): BackupConfigState {
        return BackupConfigState(
            webAppUrl = prefs.getString(KEY_WEB_APP_URL, "") ?: "",
            secretToken = prefs.getString(KEY_SECRET_TOKEN, "BABSHAR_HISAB_2026") ?: "BABSHAR_HISAB_2026",
            autoBackupEnabled = prefs.getBoolean(KEY_AUTO_BACKUP, true),
            includeDepreciationInProfit = prefs.getBoolean(KEY_INCLUDE_DEPRECIATION, true),
            useClassicFont = prefs.getBoolean(KEY_CLASSIC_FONT, false)
        )
    }

    fun updateBackupCredentials(webAppUrl: String, secretToken: String, autoBackupEnabled: Boolean) {
        prefs.edit()
            .putString(KEY_WEB_APP_URL, webAppUrl.trim())
            .putString(KEY_SECRET_TOKEN, secretToken.trim())
            .putBoolean(KEY_AUTO_BACKUP, autoBackupEnabled)
            .apply()
        _configFlow.value = readConfig()
    }

    fun setIncludeDepreciationInProfit(include: Boolean) {
        prefs.edit().putBoolean(KEY_INCLUDE_DEPRECIATION, include).apply()
        _configFlow.value = readConfig()
    }

    fun setUseClassicFont(useClassic: Boolean) {
        prefs.edit().putBoolean(KEY_CLASSIC_FONT, useClassic).apply()
        _configFlow.value = readConfig()
    }

    fun getCurrentConfig(): BackupConfigState = _configFlow.value

    companion object {
        private const val PREFS_FILE_NAME = "babshar_hisab_secure_prefs"
        private const val PREFS_FILE_NAME_FALLBACK = "babshar_hisab_fallback_prefs"
        private const val KEY_WEB_APP_URL = "sheets_web_app_url"
        private const val KEY_SECRET_TOKEN = "sheets_secret_token"
        private const val KEY_AUTO_BACKUP = "auto_backup_enabled"
        private const val KEY_INCLUDE_DEPRECIATION = "include_depreciation_in_profit"
        private const val KEY_CLASSIC_FONT = "use_classic_solaiman_font"
    }
}
