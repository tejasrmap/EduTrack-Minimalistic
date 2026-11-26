package com.gtinnox.edutrack.data.auth

import java.security.MessageDigest
import java.security.SecureRandom
import java.util.Base64
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

sealed class PasswordValidationResult {
    object Valid : PasswordValidationResult()
    data class Invalid(val reason: String) : PasswordValidationResult()
}

object PasswordSecurity {
    private const val ITERATIONS = 10000
    private const val KEY_LENGTH = 256
    private const val SALT_LENGTH = 16

    fun generateSalt(): String {
        val random = SecureRandom()
        val salt = ByteArray(SALT_LENGTH)
        random.nextBytes(salt)
        return Base64.getEncoder().encodeToString(salt)
    }

    fun hashPassword(password: String, saltBase64: String): String {
        return try {
            val salt = Base64.getDecoder().decode(saltBase64)
            val spec = PBEKeySpec(password.toCharArray(), salt, ITERATIONS, KEY_LENGTH)
            val skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
            val hash = skf.generateSecret(spec).encoded
            Base64.getEncoder().encodeToString(hash)
        } catch (e: Exception) {
            val md = MessageDigest.getInstance("SHA-256")
            md.update(saltBase64.toByteArray())
            val digest = md.digest(password.toByteArray())
            Base64.getEncoder().encodeToString(digest)
        }
    }

    fun verifyPassword(password: String, saltBase64: String, expectedHashBase64: String): Boolean {
        val computedHash = hashPassword(password, saltBase64)
        val computedBytes = Base64.getDecoder().decode(computedHash)
        val expectedBytes = try {
            Base64.getDecoder().decode(expectedHashBase64)
        } catch (e: Exception) {
            return false
        }
        return MessageDigest.isEqual(computedBytes, expectedBytes)
    }

    fun validatePassword(password: String): PasswordValidationResult {
        if (password.length < 6) {
            return PasswordValidationResult.Invalid("Password must be at least 6 characters long")
        }
        val hasLetter = password.any { it.isLetter() }
        val hasDigit = password.any { it.isDigit() }
        if (!hasLetter || !hasDigit) {
            return PasswordValidationResult.Invalid("Password must contain both letters and numbers")
        }
        return PasswordValidationResult.Valid
    }

    fun isValidEmail(email: String): Boolean {
        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}\$".toRegex()
        return email.isNotBlank() && emailRegex.matches(email.trim())
    }
}
