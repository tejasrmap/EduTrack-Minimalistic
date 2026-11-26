package com.gtinnox.edutrack.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gtinnox.edutrack.data.auth.AuthRepository
import com.gtinnox.edutrack.data.auth.AuthResult
import com.gtinnox.edutrack.data.local.entity.AcademicProfileEntity
import com.gtinnox.edutrack.data.local.entity.StudyPreferencesEntity
import com.gtinnox.edutrack.data.local.entity.UserAccountEntity
import com.gtinnox.edutrack.data.repository.EduTrackRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class AuthUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val isSessionValid: Boolean = false
)

class AuthViewModel(
    private val authRepository: AuthRepository,
    private val academicRepository: EduTrackRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    private val _eventFlow = MutableSharedFlow<String>()
    val eventFlow: SharedFlow<String> = _eventFlow.asSharedFlow()

    val currentUserId: StateFlow<String?> = authRepository.currentUserId

    val currentUser: StateFlow<UserAccountEntity?> = authRepository.currentUserId.flatMapLatest { uid ->
        if (uid.isNullOrBlank()) flowOf(null)
        else authRepository.getCurrentUser()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val currentProfile: StateFlow<AcademicProfileEntity?> = authRepository.currentUserId.flatMapLatest { uid ->
        if (uid.isNullOrBlank()) flowOf(null)
        else authRepository.getAcademicProfile(uid)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val currentPreferences: StateFlow<StudyPreferencesEntity?> = authRepository.currentUserId.flatMapLatest { uid ->
        if (uid.isNullOrBlank()) flowOf(null)
        else authRepository.getStudyPreferences(uid)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }

    fun signUp(
        fullName: String,
        email: String,
        password: String,
        confirmPassword: String,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            when (val result = authRepository.signUp(fullName, email, password, confirmPassword)) {
                is AuthResult.Success -> {
                    _uiState.value = _uiState.value.copy(isLoading = false, isSessionValid = true)
                    _eventFlow.emit("Account created successfully. Welcome, ${result.data.fullName}!")
                    onSuccess()
                }
                is AuthResult.Error -> {
                    _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = result.message)
                }
            }
        }
    }

    fun login(email: String, password: String, onSuccess: (Boolean) -> Unit) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            when (val result = authRepository.login(email, password)) {
                is AuthResult.Success -> {
                    _uiState.value = _uiState.value.copy(isLoading = false, isSessionValid = true)
                    _eventFlow.emit("Welcome back, ${result.data.fullName}!")
                    onSuccess(result.data.isOnboardingCompleted)
                }
                is AuthResult.Error -> {
                    _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = result.message)
                }
            }
        }
    }

    fun resetPassword(email: String, newPassword: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            when (val result = authRepository.resetPassword(email, newPassword)) {
                is AuthResult.Success -> {
                    _uiState.value = _uiState.value.copy(isLoading = false)
                    _eventFlow.emit("Password reset successfully. Please sign in.")
                    onSuccess()
                }
                is AuthResult.Error -> {
                    _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = result.message)
                }
            }
        }
    }

    fun changePassword(
        currentPass: String,
        newPass: String,
        confirmPass: String,
        onSuccess: () -> Unit
    ) {
        val uid = authRepository.currentUserId.value ?: return
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            when (val result = authRepository.changePassword(uid, currentPass, newPass, confirmPass)) {
                is AuthResult.Success -> {
                    _uiState.value = _uiState.value.copy(isLoading = false)
                    _eventFlow.emit("Password updated successfully")
                    onSuccess()
                }
                is AuthResult.Error -> {
                    _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = result.message)
                }
            }
        }
    }

    fun updateProfile(
        fullName: String,
        institution: String,
        degree: String,
        major: String,
        academicYear: String,
        semester: String,
        studentId: String? = null
    ) {
        val uid = authRepository.currentUserId.value ?: return
        viewModelScope.launch {
            authRepository.updateFullName(uid, fullName)
            val current = authRepository.getAcademicProfileDirect(uid)
            val updated = (current ?: AcademicProfileEntity(userId = uid, updatedAt = "")).copy(
                institution = institution.trim(),
                degree = degree.trim(),
                major = major.trim(),
                academicYear = academicYear.trim(),
                semester = semester.trim(),
                studentId = studentId?.trim()?.ifBlank { null }
            )
            authRepository.saveAcademicProfile(updated)
            _eventFlow.emit("Academic profile updated")
        }
    }

    fun updatePreferences(
        dailyStudyGoalMinutes: Int,
        weeklyStudyGoalHours: Int,
        focusDurationMinutes: Int,
        shortBreakMinutes: Int,
        longBreakMinutes: Int,
        reminderPreferences: Boolean = true,
        typicalStudyTime: String = "afternoon",
        preferredStudyDays: String = "Mon,Tue,Wed,Thu,Fri"
    ) {
        val uid = authRepository.currentUserId.value ?: return
        viewModelScope.launch {
            val updated = StudyPreferencesEntity(
                userId = uid,
                dailyStudyGoalMinutes = dailyStudyGoalMinutes,
                weeklyStudyGoalHours = weeklyStudyGoalHours,
                focusDurationMinutes = focusDurationMinutes,
                shortBreakMinutes = shortBreakMinutes,
                longBreakMinutes = longBreakMinutes,
                reminderPreferences = reminderPreferences,
                typicalStudyTime = typicalStudyTime,
                preferredStudyDays = preferredStudyDays,
                updatedAt = ""
            )
            authRepository.saveStudyPreferences(updated)
            _eventFlow.emit("Study preferences saved")
        }
    }

    fun completeOnboarding(onSuccess: () -> Unit) {
        val uid = authRepository.currentUserId.value ?: return
        viewModelScope.launch {
            authRepository.setOnboardingCompleted(uid)
            _eventFlow.emit("Welcome to EduTrack! Your workspace is ready.")
            onSuccess()
        }
    }

    fun markTutorialCompleted(key: String) {
        val uid = authRepository.currentUserId.value ?: return
        viewModelScope.launch {
            authRepository.markTutorialCompleted(uid, key)
        }
    }

    fun resetTutorials() {
        val uid = authRepository.currentUserId.value ?: return
        viewModelScope.launch {
            authRepository.resetAllTutorials(uid)
            _eventFlow.emit("Tutorials reset. Helpful guidance will appear on key screens.")
        }
    }

    fun logout(onLoggedOut: () -> Unit) {
        authRepository.logout()
        _uiState.value = AuthUiState()
        onLoggedOut()
    }
}
