package com.rudra.legalassistantbd.core.security

import android.content.Context
import android.content.SharedPreferences
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.concurrent.Executor
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SecurityManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val securePrefs: SharedPreferences = EncryptedSharedPreferences.create(
        context,
        "legal_assistant_secure_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    fun isAppLockEnabled(): Boolean = securePrefs.getBoolean(KEY_APP_LOCK_ENABLED, false)

    fun setAppLockEnabled(enabled: Boolean) {
        securePrefs.edit().putBoolean(KEY_APP_LOCK_ENABLED, enabled).apply()
    }

    fun getAppPin(): String? = securePrefs.getString(KEY_APP_PIN, null)

    fun setAppPin(pin: String) {
        securePrefs.edit().putString(KEY_APP_PIN, pin).apply()
    }

    fun isPinSet(): Boolean = getAppPin() != null

    fun verifyPin(pin: String): Boolean = getAppPin() == pin

    fun isBiometricAvailable(): Boolean {
        val biometricManager = BiometricManager.from(context)
        return biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG) ==
                BiometricManager.BIOMETRIC_SUCCESS
    }

    fun isBiometricEnabled(): Boolean = securePrefs.getBoolean(KEY_BIOMETRIC_ENABLED, false)

    fun setBiometricEnabled(enabled: Boolean) {
        securePrefs.edit().putBoolean(KEY_BIOMETRIC_ENABLED, enabled).apply()
    }

    companion object {
        private const val KEY_APP_LOCK_ENABLED = "app_lock_enabled"
        private const val KEY_APP_PIN = "app_pin"
        private const val KEY_BIOMETRIC_ENABLED = "biometric_enabled"
    }
}
