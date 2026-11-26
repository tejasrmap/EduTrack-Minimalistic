package com.gtinnox.edutrack.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gtinnox.edutrack.data.auth.AuthRepository
import com.gtinnox.edutrack.data.local.entity.AcademicProfileEntity
import com.gtinnox.edutrack.data.local.entity.StudyPreferencesEntity
import com.gtinnox.edutrack.data.local.entity.SubjectEntity
import com.gtinnox.edutrack.data.repository.EduTrackRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.Instant
import java.util.UUID

data class SubjectDraft(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val code: String,
    val color: String = "#355B48",
    val weeklyTargetHours: Int = 5,
    val credits: Int = 3
)

data class OnboardingWizardState(
    val currentStep: Int = 1,
    val totalSteps: Int = 7,
    // Step 1: Personal Profile
    val fullName: String = "",
    val avatarInitials: String = "",
    // Step 2: Academic Info
    val institution: String = "",
    val degree: String = "",
    val major: String = "",
    val academicYear: String = "Year 2 (Sophomore)",
    val semester: String = "Fall 2026",
    val studentId: String = "",
    // Step 3: Subjects Setup
    val subjects: List<SubjectDraft> = listOf(
        SubjectDraft(name = "Algorithms & Data Structures", code = "CS 201", color = "#355B48", weeklyTargetHours = 6),
        SubjectDraft(name = "Linear Algebra & Calculus", code = "MATH 210", color = "#16A37B", weeklyTargetHours = 5),
        SubjectDraft(name = "Database Systems", code = "CS 220", color = "#8E67E8", weeklyTargetHours = 4)
    ),
    // Step 4: Study Goals
    val dailyGoalMinutes: Int = 180,
    val weeklyGoalHours: Int = 20,
    val focusDurationMinutes: Int = 25,
    val shortBreakMinutes: Int = 5,
    val longBreakMinutes: Int = 15,
    // Step 5: Schedule
    val preferredDays: Set<String> = setOf("Mon", "Tue", "Wed", "Thu", "Fri"),
    val typicalStudyTime: String = "afternoon",
    val studyReminders: Boolean = true,
    // Step 6: Notifications
    val notificationsEnabled: Boolean = true,
    val isFinishing: Boolean = false
)

class OnboardingViewModel(
    private val authRepository: AuthRepository,
    private val academicRepository: EduTrackRepository
) : ViewModel() {

    private val _state = MutableStateFlow(OnboardingWizardState())
    val state: StateFlow<OnboardingWizardState> = _state.asStateFlow()

    private val _eventFlow = MutableSharedFlow<String>()
    val eventFlow: SharedFlow<String> = _eventFlow.asSharedFlow()

    init {
        loadActiveUserData()
    }

    private fun loadActiveUserData() {
        viewModelScope.launch {
            val user = authRepository.getCurrentUserDirect()
            if (user != null) {
                val initials = user.fullName.split(" ")
                    .filter { it.isNotBlank() }
                    .take(2)
                    .map { it.first().uppercase() }
                    .joinToString("")
                _state.value = _state.value.copy(
                    fullName = user.fullName,
                    avatarInitials = initials
                )
            }
        }
    }

    fun updatePersonalProfile(name: String) {
        val initials = name.split(" ")
            .filter { it.isNotBlank() }
            .take(2)
            .map { it.first().uppercase() }
            .joinToString("")
        _state.value = _state.value.copy(fullName = name, avatarInitials = initials)
    }

    fun updateAcademicInfo(
        institution: String? = null,
        degree: String? = null,
        major: String? = null,
        academicYear: String? = null,
        semester: String? = null,
        studentId: String? = null
    ) {
        _state.value = _state.value.copy(
            institution = institution ?: _state.value.institution,
            degree = degree ?: _state.value.degree,
            major = major ?: _state.value.major,
            academicYear = academicYear ?: _state.value.academicYear,
            semester = semester ?: _state.value.semester,
            studentId = studentId ?: _state.value.studentId
        )
    }

    fun addSubject(name: String, code: String, color: String, targetHours: Int) {
        val cleanName = name.trim()
        val cleanCode = code.trim().uppercase()
        if (cleanName.isBlank()) return
        val newDraft = SubjectDraft(
            name = cleanName,
            code = cleanCode.ifBlank { cleanName.take(3).uppercase() },
            color = color,
            weeklyTargetHours = targetHours.coerceAtLeast(1)
        )
        _state.value = _state.value.copy(subjects = _state.value.subjects + newDraft)
    }

    fun removeSubject(subjectId: String) {
        _state.value = _state.value.copy(
            subjects = _state.value.subjects.filterNot { it.id == subjectId }
        )
    }

    fun updateGoals(
        dailyGoalMinutes: Int? = null,
        weeklyGoalHours: Int? = null,
        focusDurationMinutes: Int? = null,
        shortBreakMinutes: Int? = null,
        longBreakMinutes: Int? = null
    ) {
        _state.value = _state.value.copy(
            dailyGoalMinutes = dailyGoalMinutes ?: _state.value.dailyGoalMinutes,
            weeklyGoalHours = weeklyGoalHours ?: _state.value.weeklyGoalHours,
            focusDurationMinutes = focusDurationMinutes ?: _state.value.focusDurationMinutes,
            shortBreakMinutes = shortBreakMinutes ?: _state.value.shortBreakMinutes,
            longBreakMinutes = longBreakMinutes ?: _state.value.longBreakMinutes
        )
    }

    fun toggleStudyDay(day: String) {
        val current = _state.value.preferredDays.toMutableSet()
        if (current.contains(day)) {
            if (current.size > 1) current.remove(day)
        } else {
            current.add(day)
        }
        _state.value = _state.value.copy(preferredDays = current)
    }

    fun updateSchedulePreferences(typicalTime: String? = null, reminders: Boolean? = null) {
        _state.value = _state.value.copy(
            typicalStudyTime = typicalTime ?: _state.value.typicalStudyTime,
            studyReminders = reminders ?: _state.value.studyReminders
        )
    }

    fun setNotificationsEnabled(enabled: Boolean) {
        _state.value = _state.value.copy(notificationsEnabled = enabled)
    }

    fun nextStep() {
        if (_state.value.currentStep < _state.value.totalSteps) {
            _state.value = _state.value.copy(currentStep = _state.value.currentStep + 1)
        }
    }

    fun prevStep() {
        if (_state.value.currentStep > 1) {
            _state.value = _state.value.copy(currentStep = _state.value.currentStep - 1)
        }
    }

    fun finishOnboarding(onSuccess: () -> Unit) {
        val s = _state.value
        val uid = authRepository.currentUserId.value ?: return

        viewModelScope.launch {
            _state.value = _state.value.copy(isFinishing = true)

            // 1. Update Full Name
            if (s.fullName.isNotBlank()) {
                authRepository.updateFullName(uid, s.fullName)
            }

            // 2. Save Academic Profile
            val profile = AcademicProfileEntity(
                userId = uid,
                institution = s.institution.trim().ifBlank { "University / College" },
                degree = s.degree.trim().ifBlank { "Academic Program" },
                major = s.major.trim().ifBlank { "General Studies" },
                academicYear = s.academicYear,
                semester = s.semester,
                studentId = s.studentId.trim().ifBlank { null },
                profilePhotoUri = null,
                updatedAt = Instant.now().toString()
            )
            authRepository.saveAcademicProfile(profile)

            // 3. Save Study Preferences
            val preferences = StudyPreferencesEntity(
                userId = uid,
                dailyStudyGoalMinutes = s.dailyGoalMinutes,
                weeklyStudyGoalHours = s.weeklyGoalHours,
                focusDurationMinutes = s.focusDurationMinutes,
                shortBreakMinutes = s.shortBreakMinutes,
                longBreakMinutes = s.longBreakMinutes,
                reminderPreferences = s.studyReminders,
                typicalStudyTime = s.typicalStudyTime,
                preferredStudyDays = s.preferredDays.joinToString(","),
                updatedAt = Instant.now().toString()
            )
            authRepository.saveStudyPreferences(preferences)

            // 4. Save Created Subjects to Room
            val nowStr = Instant.now().toString()
            val entities = s.subjects.map { draft ->
                SubjectEntity(
                    id = "subj-${UUID.randomUUID().toString().take(8)}",
                    userId = uid,
                    name = draft.name,
                    code = draft.code,
                    color = draft.color,
                    description = "${draft.name} coursework",
                    semester = s.semester,
                    instructor = "Course Faculty",
                    weeklyTargetHours = draft.weeklyTargetHours,
                    attendanceRate = 100,
                    createdAt = nowStr
                )
            }
            if (entities.isNotEmpty()) {
                academicRepository.insertSubjects(entities)
            }

            // 5. Mark Onboarding Completed
            authRepository.setOnboardingCompleted(uid)
            _eventFlow.emit("Welcome to EduTrack! Your academic space is ready.")
            onSuccess()
        }
    }
}
