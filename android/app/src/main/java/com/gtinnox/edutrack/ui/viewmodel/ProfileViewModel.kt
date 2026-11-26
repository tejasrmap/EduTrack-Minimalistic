package com.gtinnox.edutrack.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gtinnox.edutrack.data.auth.AuthRepository
import com.gtinnox.edutrack.data.local.entity.AcademicProfileEntity
import com.gtinnox.edutrack.data.local.entity.StudyPreferencesEntity
import com.gtinnox.edutrack.data.local.entity.UserAccountEntity
import com.gtinnox.edutrack.data.repository.EduTrackRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val authRepository: AuthRepository,
    private val academicRepository: EduTrackRepository
) : ViewModel() {

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

    private val _eventFlow = MutableSharedFlow<String>()
    val eventFlow: SharedFlow<String> = _eventFlow.asSharedFlow()

    fun updateProfile(
        name: String,
        university: String,
        program: String,
        academicYear: String,
        semester: String,
        studentId: String? = null
    ) {
        val uid = authRepository.currentUserId.value ?: return
        viewModelScope.launch {
            authRepository.updateFullName(uid, name)
            val existing = authRepository.getAcademicProfileDirect(uid)
            val updated = (existing ?: AcademicProfileEntity(userId = uid, updatedAt = "")).copy(
                institution = university.trim(),
                degree = program.trim(),
                major = program.trim(),
                academicYear = academicYear.trim(),
                semester = semester.trim(),
                studentId = studentId?.trim()?.ifBlank { null }
            )
            authRepository.saveAcademicProfile(updated)
            _eventFlow.emit("Profile updated successfully")
        }
    }

    fun updatePreferences(
        dailyTargetMinutes: Int,
        weeklyHours: Int
    ) {
        val uid = authRepository.currentUserId.value ?: return
        viewModelScope.launch {
            val existing = authRepository.getStudyPreferencesDirect(uid)
            val updated = (existing ?: StudyPreferencesEntity(userId = uid, updatedAt = "")).copy(
                dailyStudyGoalMinutes = dailyTargetMinutes,
                weeklyStudyGoalHours = weeklyHours
            )
            authRepository.saveStudyPreferences(updated)
            _eventFlow.emit("Study preferences updated")
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
            when (val res = authRepository.changePassword(uid, currentPass, newPass, confirmPass)) {
                is com.gtinnox.edutrack.data.auth.AuthResult.Success -> {
                    _eventFlow.emit("Password changed successfully")
                    onSuccess()
                }
                is com.gtinnox.edutrack.data.auth.AuthResult.Error -> {
                    _eventFlow.emit(res.message)
                }
            }
        }
    }

    fun resetTutorials() {
        val uid = authRepository.currentUserId.value ?: return
        viewModelScope.launch {
            authRepository.resetAllTutorials(uid)
            _eventFlow.emit("Tutorials reset. Guided tours will appear on key screens.")
        }
    }

    fun logout(onLoggedOut: () -> Unit) {
        authRepository.logout()
        onLoggedOut()
    }
}
