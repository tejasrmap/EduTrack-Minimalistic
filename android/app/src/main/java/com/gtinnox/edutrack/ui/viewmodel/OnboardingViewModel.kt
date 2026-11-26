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
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.Instant

data class OnboardingState(
    val currentStep: Int = 1,
    val name: String = "Julian Vance",
    val university: String = "Oxford University",
    val program: String = "B.Sc. Computer Science",
    val academicYear: String = "Year 2",
    val semester: String = "Fall 2026",
    val weeklyHours: Int = 25,
    val dailyMinutes: Int = 180
)

class OnboardingViewModel(
    private val repository: EduTrackRepository
) : ViewModel() {

    private val _state = MutableStateFlow(OnboardingState())
    val state: StateFlow<OnboardingState> = _state.asStateFlow()

    val userProfile: StateFlow<UserProfileEntity?> = repository.userProfile.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        null
    )

    private val _eventFlow = MutableSharedFlow<String>()
    val eventFlow: SharedFlow<String> = _eventFlow.asSharedFlow()

    fun updateField(
        name: String? = null,
        university: String? = null,
        program: String? = null,
        academicYear: String? = null,
        semester: String? = null,
        weeklyHours: Int? = null
    ) {
        _state.value = _state.value.copy(
            name = name ?: _state.value.name,
            university = university ?: _state.value.university,
            program = program ?: _state.value.program,
            academicYear = academicYear ?: _state.value.academicYear,
            semester = semester ?: _state.value.semester,
            weeklyHours = weeklyHours ?: _state.value.weeklyHours,
            dailyMinutes = (weeklyHours ?: _state.value.weeklyHours) * 60 / 7
        )
    }

    fun nextStep() {
        if (_state.value.currentStep < 3) {
            _state.value = _state.value.copy(currentStep = _state.value.currentStep + 1)
        } else {
            completeOnboarding()
        }
    }

    fun prevStep() {
        if (_state.value.currentStep > 1) {
            _state.value = _state.value.copy(currentStep = _state.value.currentStep - 1)
        }
    }

    fun completeOnboarding() {
        viewModelScope.launch {
            val s = _state.value
            val profile = UserProfileEntity(
                id = "user-default-1",
                name = s.name.trim(),
                email = "${s.name.lowercase().replace(" ", ".")}@university.edu",
                university = s.university.trim(),
                program = s.program.trim(),
                academicYear = s.academicYear.trim(),
                semester = s.semester.trim(),
                preferredStudyHours = s.weeklyHours,
                dailyTargetMinutes = s.dailyMinutes,
                avatar = s.name.split(" ").mapNotNull { it.firstOrNull()?.toString() }.take(2).joinToString("").uppercase(),
                isOnboarded = true,
                createdAt = Instant.now().toString()
            )
            repository.updateProfile(profile)
            _eventFlow.emit("Onboarding completed! Welcome to EduTrack.")
        }
    }
}
