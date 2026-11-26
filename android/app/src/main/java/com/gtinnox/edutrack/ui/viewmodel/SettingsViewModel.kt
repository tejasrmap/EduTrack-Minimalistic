package com.gtinnox.edutrack.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gtinnox.edutrack.data.local.entity.UserProfileEntity
import com.gtinnox.edutrack.data.repository.EduTrackRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class SettingsUiState(
    val isDarkMode: Boolean = false,
    val pomodoroMinutes: Int = 25,
    val shortBreakMinutes: Int = 5,
    val longBreakMinutes: Int = 15,
    val soundEnabled: Boolean = true,
    val dailyTargetMinutes: Int = 180,
    val exportedJson: String? = null
)

class SettingsViewModel(
    private val repository: EduTrackRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    private val _eventFlow = MutableSharedFlow<String>()
    val eventFlow: SharedFlow<String> = _eventFlow.asSharedFlow()

    val userProfile: StateFlow<UserProfileEntity?> = repository.userProfile.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        null
    )

    fun toggleDarkMode(enable: Boolean) {
        _uiState.value = _uiState.value.copy(isDarkMode = enable)
    }

    fun setPomodoroMinutes(minutes: Int) {
        _uiState.value = _uiState.value.copy(pomodoroMinutes = minutes)
    }

    fun setSoundEnabled(enable: Boolean) {
        _uiState.value = _uiState.value.copy(soundEnabled = enable)
    }

    fun resetToDefaults() {
        viewModelScope.launch {
            repository.resetToDefaultData()
            _eventFlow.emit("Database successfully restored to default academic curriculum")
        }
    }

    fun exportData() {
        viewModelScope.launch {
            val subjects = repository.allSubjects.first()
            val sessions = repository.allSessions.first()
            val tasks = repository.allTasks.first()
            val exams = repository.allExaminations.first()
            val goals = repository.allGoals.first()
            val notes = repository.allNotes.first()
            val events = repository.allEvents.first()
            val profile = repository.userProfile.first()

            val json = repository.exportDataAsJson(
                subjects, sessions, tasks, exams, goals, notes, events, profile
            )
            _uiState.value = _uiState.value.copy(exportedJson = json)
            _eventFlow.emit("Full backup generated (${json.length} characters)")
        }
    }

    fun importData(json: String) {
        viewModelScope.launch {
            val success = repository.importDataFromJson(json)
            if (success) {
                _eventFlow.emit("Backup restored successfully!")
            } else {
                _eventFlow.emit("Failed to import: invalid JSON format")
            }
        }
    }
}
