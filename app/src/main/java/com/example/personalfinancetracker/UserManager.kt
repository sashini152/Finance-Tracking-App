package com.example.personalfinancetracker

import android.content.Context
import android.content.SharedPreferences
import java.security.MessageDigest

object UserManager {
    private const val PREFS_NAME = "UserPrefs"
    private const val KEY_NIC_PASSPORT = "nic_passport"
    private const val KEY_ACCOUNT_NUMBER = "account_number"
    private const val KEY_PASSWORD = "password"
    private const val KEY_PHONE = "phone"
    private const val KEY_IS_LOGGED_IN = "is_logged_in"

    private lateinit var sharedPreferences: SharedPreferences

    fun initialize(context: Context) {
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun registerUser(accountNumber: String, nicPassport: String, password: String, phone: String): Boolean {
        // Check if user already exists
        if (sharedPreferences.contains(nicPassport)) {
            return false
        }

        // Store user data
        with(sharedPreferences.edit()) {
            putString("${nicPassport}_$KEY_ACCOUNT_NUMBER", accountNumber)
            putString("${nicPassport}_$KEY_PHONE", phone)
            putString("${nicPassport}_$KEY_PASSWORD", hashPassword(password))
            apply()
        }
        return true
    }

    fun loginUser(nicPassport: String, password: String): Boolean {
        val storedPassword = sharedPreferences.getString("${nicPassport}_$KEY_PASSWORD", null)
        if (storedPassword == null || storedPassword != hashPassword(password)) {
            return false
        }

        // Store login state
        with(sharedPreferences.edit()) {
            putString(KEY_NIC_PASSPORT, nicPassport)
            putBoolean(KEY_IS_LOGGED_IN, true)
            apply()
        }
        return true
    }

    fun logout() {
        with(sharedPreferences.edit()) {
            remove(KEY_NIC_PASSPORT)
            putBoolean(KEY_IS_LOGGED_IN, false)
            apply()
        }
    }

    fun isLoggedIn(): Boolean {
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false)
    }

    fun getCurrentUser(): User? {
        if (!isLoggedIn()) return null
        val nicPassport = sharedPreferences.getString(KEY_NIC_PASSPORT, null) ?: return null
        val accountNumber = sharedPreferences.getString("${nicPassport}_$KEY_ACCOUNT_NUMBER", null) ?: return null
        val phone = sharedPreferences.getString("${nicPassport}_$KEY_PHONE", null) ?: return null
        val password = sharedPreferences.getString("${nicPassport}_$KEY_PASSWORD", null) ?: return null
        return User(nicPassport, accountNumber, phone, password)
    }

    fun updateUser(oldNicPassport: String, newNicPassport: String, newAccountNumber: String, newPhone: String): Boolean {
        try {
            // Get current user data
            val currentPassword = sharedPreferences.getString("${oldNicPassport}_$KEY_PASSWORD", null) ?: return false

            // Remove old data
            with(sharedPreferences.edit()) {
                remove("${oldNicPassport}_$KEY_ACCOUNT_NUMBER")
                remove("${oldNicPassport}_$KEY_PHONE")
                remove("${oldNicPassport}_$KEY_PASSWORD")
                apply()
            }

            // Store new data
            with(sharedPreferences.edit()) {
                putString("${newNicPassport}_$KEY_ACCOUNT_NUMBER", newAccountNumber)
                putString("${newNicPassport}_$KEY_PHONE", newPhone)
                putString("${newNicPassport}_$KEY_PASSWORD", currentPassword)
                // Update logged in user if it's the current user
                if (sharedPreferences.getString(KEY_NIC_PASSPORT, null) == oldNicPassport) {
                    putString(KEY_NIC_PASSPORT, newNicPassport)
                }
                apply()
            }
            return true
        } catch (e: Exception) {
            return false
        }
    }

    fun verifyPassword(nicPassport: String, password: String): Boolean {
        val storedPassword = sharedPreferences.getString("${nicPassport}_$KEY_PASSWORD", null)
        return storedPassword != null && storedPassword == hashPassword(password)
    }

    fun changePassword(nicPassport: String, newPassword: String): Boolean {
        try {
            with(sharedPreferences.edit()) {
                putString("${nicPassport}_$KEY_PASSWORD", hashPassword(newPassword))
                apply()
            }
            return true
        } catch (e: Exception) {
            return false
        }
    }

    private fun hashPassword(password: String): String {
        val bytes = password.toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        return digest.fold("") { str, it -> str + "%02x".format(it) }
    }
} 