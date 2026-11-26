package com.gtinnox.edutrack.data.auth

import com.gtinnox.edutrack.data.local.dao.AcademicProfileDao
import com.gtinnox.edutrack.data.local.dao.StudyPreferencesDao
import com.gtinnox.edutrack.data.local.dao.UserAccountDao
import com.gtinnox.edutrack.data.local.entity.AcademicProfileEntity
import com.gtinnox.edutrack.data.local.entity.StudyPreferencesEntity
import com.gtinnox.edutrack.data.local.entity.UserAccountEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import java.time.Instant
import java.util.UUID

sealed class AuthResult<out T> {
    data class Success<out T>(val data: T) : AuthResult<T>()
    data class Error(val message: String) : AuthResult<Nothing>()
}

class AuthRepository(
    private val userAccountDao: UserAccountDao,
    private val academicProfileDao: AcademicProfileDao,
    private val studyPreferencesDao: StudyPreferencesDao,
    private val sessionManager: SessionManager
) {
    val currentUserId = sessionManager.currentUserId

    fun getCurrentUser(): Flow<UserAccountEntity?> {
        val uid = sessionManager.getActiveUserId() ?: ""
        return userAccountDao.getUserById(uid)
    }

    suspend fun getCurrentUserDirect(): UserAccountEntity? {
        val uid = sessionManager.getActiveUserId() ?: return null
        return userAccountDao.getUserByIdDirect(uid)
    }

    fun getAcademicProfile(userId: String): Flow<AcademicProfileEntity?> =
        academicProfileDao.getProfile(userId)

    suspend fun getAcademicProfileDirect(userId: String): AcademicProfileEntity? =
        academicProfileDao.getProfileDirect(userId)

    fun getStudyPreferences(userId: String): Flow<StudyPreferencesEntity?> =
        studyPreferencesDao.getPreferences(userId)

    suspend fun getStudyPreferencesDirect(userId: String): StudyPreferencesEntity? =
        studyPreferencesDao.getPreferencesDirect(userId)

    suspend fun signUp(
        fullName: String,
        email: String,
        password: String,
        confirmPassword: String
    ): AuthResult<UserAccountEntity> = withContext(Dispatchers.IO) {
        val cleanName = fullName.trim()
        val cleanEmail = email.trim().lowercase()

        if (cleanName.isBlank()) {
            return@withContext AuthResult.Error("Please enter your full name.")
        }
        if (!PasswordSecurity.isValidEmail(cleanEmail)) {
            return@withContext AuthResult.Error("Please enter a valid email address.")
        }
        if (password != confirmPassword) {
            return@withContext AuthResult.Error("Passwords do not match.")
        }

        when (val validation = PasswordSecurity.validatePassword(password)) {
            is PasswordValidationResult.Invalid -> {
                return@withContext AuthResult.Error(validation.reason)
            }
            PasswordValidationResult.Valid -> Unit
        }

        // Check uniqueness
        val existing = userAccountDao.getUserByEmail(cleanEmail)
        if (existing != null) {
            return@withContext AuthResult.Error("An account with this email already exists. Please sign in.")
        }

        val salt = PasswordSecurity.generateSalt()
        val hash = PasswordSecurity.hashPassword(password, salt)
        val now = Instant.now().toString()
        val newUserId = "user-${UUID.randomUUID().toString().take(12)}"

        val account = UserAccountEntity(
            id = newUserId,
            fullName = cleanName,
            email = cleanEmail,
            passwordHash = hash,
            passwordSalt = salt,
            createdAt = now,
            updatedAt = now,
            isOnboardingCompleted = false,
            tutorialsCompleted = ""
        )

        userAccountDao.insertUser(account)

        // Seed initial academic profile and preferences for the new user
        val profile = AcademicProfileEntity(
            userId = newUserId,
            institution = "",
            degree = "",
            major = "",
            academicYear = "",
            semester = "",
            studentId = null,
            profilePhotoUri = null,
            updatedAt = now
        )
        academicProfileDao.insertOrUpdateProfile(profile)

        val preferences = StudyPreferencesEntity(
            userId = newUserId,
            dailyStudyGoalMinutes = 180,
            weeklyStudyGoalHours = 20,
            focusDurationMinutes = 25,
            shortBreakMinutes = 5,
            longBreakMinutes = 15,
            reminderPreferences = true,
            typicalStudyTime = "afternoon",
            preferredStudyDays = "Mon,Tue,Wed,Thu,Fri",
            updatedAt = now
        )
        studyPreferencesDao.insertOrUpdatePreferences(preferences)

        // Automatically log in newly created user
        sessionManager.login(newUserId)
        AuthResult.Success(account)
    }

    suspend fun login(email: String, password: String): AuthResult<UserAccountEntity> = withContext(Dispatchers.IO) {
        val cleanEmail = email.trim().lowercase()
        if (cleanEmail.isBlank()) {
            return@withContext AuthResult.Error("Please enter your email address.")
        }
        if (password.isBlank()) {
            return@withContext AuthResult.Error("Please enter your password.")
        }

        val account = userAccountDao.getUserByEmail(cleanEmail)
            ?: return@withContext AuthResult.Error("No account found with this email. Please check your credentials or create an account.")

        val isMatch = PasswordSecurity.verifyPassword(password, account.passwordSalt, account.passwordHash)
        if (!isMatch) {
            return@withContext AuthResult.Error("Incorrect password. Please try again or use Forgot Password.")
        }

        sessionManager.login(account.id)
        AuthResult.Success(account)
    }

    suspend fun changePassword(
        userId: String,
        currentPassword: String,
        newPassword: String,
        confirmNewPassword: String
    ): AuthResult<Unit> = withContext(Dispatchers.IO) {
        val account = userAccountDao.getUserByIdDirect(userId)
            ?: return@withContext AuthResult.Error("Account not found.")

        if (!PasswordSecurity.verifyPassword(currentPassword, account.passwordSalt, account.passwordHash)) {
            return@withContext AuthResult.Error("Current password is incorrect.")
        }
        if (newPassword != confirmNewPassword) {
            return@withContext AuthResult.Error("New passwords do not match.")
        }
        when (val validation = PasswordSecurity.validatePassword(newPassword)) {
            is PasswordValidationResult.Invalid -> return@withContext AuthResult.Error(validation.reason)
            PasswordValidationResult.Valid -> Unit
        }

        val newSalt = PasswordSecurity.generateSalt()
        val newHash = PasswordSecurity.hashPassword(newPassword, newSalt)
        userAccountDao.updateUser(
            account.copy(
                passwordHash = newHash,
                passwordSalt = newSalt,
                updatedAt = Instant.now().toString()
            )
        )
        AuthResult.Success(Unit)
    }

    suspend fun resetPassword(email: String, newPassword: String): AuthResult<Unit> = withContext(Dispatchers.IO) {
        val cleanEmail = email.trim().lowercase()
        val account = userAccountDao.getUserByEmail(cleanEmail)
            ?: return@withContext AuthResult.Error("No account found with that email address.")

        when (val validation = PasswordSecurity.validatePassword(newPassword)) {
            is PasswordValidationResult.Invalid -> return@withContext AuthResult.Error(validation.reason)
            PasswordValidationResult.Valid -> Unit
        }

        val newSalt = PasswordSecurity.generateSalt()
        val newHash = PasswordSecurity.hashPassword(newPassword, newSalt)
        userAccountDao.updateUser(
            account.copy(
                passwordHash = newHash,
                passwordSalt = newSalt,
                updatedAt = Instant.now().toString()
            )
        )
        AuthResult.Success(Unit)
    }

    suspend fun saveAcademicProfile(profile: AcademicProfileEntity) = withContext(Dispatchers.IO) {
        academicProfileDao.insertOrUpdateProfile(profile.copy(updatedAt = Instant.now().toString()))
    }

    suspend fun saveStudyPreferences(preferences: StudyPreferencesEntity) = withContext(Dispatchers.IO) {
        studyPreferencesDao.insertOrUpdatePreferences(preferences.copy(updatedAt = Instant.now().toString()))
    }

    suspend fun updateFullName(userId: String, newName: String) = withContext(Dispatchers.IO) {
        val account = userAccountDao.getUserByIdDirect(userId) ?: return@withContext
        userAccountDao.updateUser(account.copy(fullName = newName.trim(), updatedAt = Instant.now().toString()))
    }

    suspend fun setOnboardingCompleted(userId: String) = withContext(Dispatchers.IO) {
        userAccountDao.setOnboardingCompleted(userId, true)
    }

    suspend fun isTutorialCompleted(userId: String, tutorialKey: String): Boolean = withContext(Dispatchers.IO) {
        val account = userAccountDao.getUserByIdDirect(userId) ?: return@withContext false
        val keys = account.tutorialsCompleted.split(",").map { it.trim() }
        keys.contains(tutorialKey)
    }

    suspend fun markTutorialCompleted(userId: String, tutorialKey: String) = withContext(Dispatchers.IO) {
        val account = userAccountDao.getUserByIdDirect(userId) ?: return@withContext
        val keys = account.tutorialsCompleted.split(",").filter { it.isNotBlank() }.map { it.trim() }.toMutableSet()
        keys.add(tutorialKey)
        userAccountDao.updateTutorialsCompleted(userId, keys.joinToString(","))
    }

    suspend fun resetAllTutorials(userId: String) = withContext(Dispatchers.IO) {
        userAccountDao.updateTutorialsCompleted(userId, "")
    }

    suspend fun hasAnyUsers(): Boolean = withContext(Dispatchers.IO) {
        userAccountDao.getUserCount() > 0
    }

    fun logout() {
        sessionManager.logout()
    }
}
