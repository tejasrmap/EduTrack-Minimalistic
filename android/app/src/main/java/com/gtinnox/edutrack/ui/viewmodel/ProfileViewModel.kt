package com.gtinnox.edutrack.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gtinnox.edutrack.data.local.entity.UserProfileEntity
import com.gtinnox.edutrack.data.repository.EduTrackRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.Instant

data class ScholarPersona(
    val name: String,
    val email: String,
    val university: String,
    val program: String,
    val academicYear: String,
    val semester: String,
    val preferredStudyHours: Int,
    val avatar: String
)

class ProfileViewModel(
    private val repository: EduTrackRepository
) : ViewModel() {

    val userProfile: StateFlow<UserProfileEntity?> = repository.userProfile.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        null
    )

    private val _eventFlow = MutableSharedFlow<String>()
    val eventFlow: SharedFlow<String> = _eventFlow.asSharedFlow()

    val personas = listOf(
        ScholarPersona(
            name = "Julian Vance",
            email = "julian.vance@university.edu",
            university = "Oxford University / Institute of Technology",
            program = "B.Sc. Computer Science & Mathematics",
            academicYear = "Year 2 (Sophomore)",
            semester = "Fall Semester 2026",
            preferredStudyHours = 25,
            avatar = "JV"
        ),
        ScholarPersona(
            name = "Elena Rostova",
            email = "elena.rostova@cambridge.ac.uk",
            university = "University of Cambridge",
            program = "M.Sc. Artificial Intelligence & Cognitive Systems",
            academicYear = "Master's Candidate",
            semester = "Michaelmas Term 2026",
            preferredStudyHours = 32,
            avatar = "ER"
        ),
        ScholarPersona(
            name = "Marcus Sterling",
            email = "m.sterling@harvard.edu",
            university = "Harvard Medical Sciences",
            program = "Pre-Med Cellular & Molecular Biology",
            academicYear = "Junior (Year 3)",
            semester = "Fall 2026",
            preferredStudyHours = 35,
            avatar = "MS"
        )
    )

    fun applyPersona(persona: ScholarPersona) {
        viewModelScope.launch {
            val entity = UserProfileEntity(
                id = "user-default-1",
                name = persona.name,
                email = persona.email,
                university = persona.university,
                program = persona.program,
                academicYear = persona.academicYear,
                semester = persona.semester,
                preferredStudyHours = persona.preferredStudyHours,
                dailyTargetMinutes = persona.preferredStudyHours * 60 / 7,
                avatar = persona.avatar,
                isOnboarded = true,
                createdAt = Instant.now().toString()
            )
            repository.updateProfile(entity)
            _eventFlow.emit("Active scholar persona switched to ${persona.name}")
        }
    }

    fun updateProfile(
        name: String,
        email: String,
        university: String,
        program: String,
        academicYear: String,
        semester: String,
        preferredStudyHours: Int
    ) {
        viewModelScope.launch {
            if (name.isBlank() || university.isBlank()) {
                _eventFlow.emit("Please enter your name and university")
                return@launch
            }
            val entity = UserProfileEntity(
                id = "user-default-1",
                name = name.trim(),
                email = email.trim(),
                university = university.trim(),
                program = program.trim(),
                academicYear = academicYear.trim(),
                semester = semester.trim(),
                preferredStudyHours = preferredStudyHours,
                dailyTargetMinutes = preferredStudyHours * 60 / 7,
                avatar = name.split(" ").mapNotNull { it.firstOrNull()?.toString() }.take(2).joinToString("").uppercase(),
                isOnboarded = true,
                createdAt = Instant.now().toString()
            )
            repository.updateProfile(entity)
            _eventFlow.emit("Scholar profile updated")
        }
    }
}
