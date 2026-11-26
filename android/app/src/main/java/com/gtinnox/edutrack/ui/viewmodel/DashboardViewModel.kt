package com.gtinnox.edutrack.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gtinnox.edutrack.data.auth.AuthRepository
import com.gtinnox.edutrack.data.local.entity.ExaminationEntity
import com.gtinnox.edutrack.data.local.entity.ScheduleEventEntity
import com.gtinnox.edutrack.data.local.entity.TaskEntity
import com.gtinnox.edutrack.data.local.entity.UserAccountEntity
import com.gtinnox.edutrack.data.local.entity.UserProfileEntity
import com.gtinnox.edutrack.data.local.relations.SessionWithSubject
import com.gtinnox.edutrack.data.local.relations.SubjectWithSessions
import com.gtinnox.edutrack.data.repository.EduTrackRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate

data class DashboardUiState(
    val userAccount: UserAccountEntity? = null,
    val userProfile: UserProfileEntity? = null,
    val userName: String = "Scholar",
    val userProgram: String = "Academic Studies",
    val subjectsWithSessions: List<SubjectWithSessions> = emptyList(),
    val todayStudyMinutes: Int = 0,
    val weeklyStudyMinutes: Int = 0,
    val weeklyTargetMinutes: Int = 1500, // 25 hours
    val pendingTasks: List<TaskEntity> = emptyList(),
    val upcomingExams: List<ExaminationEntity> = emptyList(),
    val todaySchedule: List<ScheduleEventEntity> = emptyList(),
    val recentSessions: List<SessionWithSubject> = emptyList(),
    val streakDays: Int = 7,
    val showTutorial: Boolean = false,
    val weeklyTrends: List<DayTrend> = listOf(
        DayTrend("Mon", 140),
        DayTrend("Tue", 190),
        DayTrend("Wed", 210),
        DayTrend("Thu", 175),
        DayTrend("Fri", 230),
        DayTrend("Sat", 160),
        DayTrend("Sun", 200)
    )
)

class DashboardViewModel(
    private val repository: EduTrackRepository,
    private val authRepository: AuthRepository? = null
) : ViewModel() {

    private val _tutorialDismissed = MutableStateFlow(false)

    private val primaryFlow = combine(
        repository.userProfile,
        repository.allSubjectsWithSessions,
        repository.allSessionsWithSubject
    ) { profile, subjects, sessions ->
        Triple(profile, subjects, sessions)
    }

    private val secondaryFlow = combine(
        repository.allTasks,
        repository.allExaminations,
        repository.allEvents
    ) { tasks, exams, events ->
        Triple(tasks, exams, events)
    }

    private val authFlow = if (authRepository != null) {
        authRepository.getCurrentUser()
    } else {
        kotlinx.coroutines.flow.flowOf(null)
    }

    val uiState: StateFlow<DashboardUiState> = combine(
        primaryFlow,
        secondaryFlow,
        authFlow,
        _tutorialDismissed
    ) { (profile, subjectsWithSessions, sessionsWithSubject), (tasks, exams, events), authUser, dismissed ->
        val todayStr = LocalDate.now().toString()

        val todaySessions = sessionsWithSubject.filter {
            it.session.startTime.startsWith(todayStr)
        }
        val todayMinutes = todaySessions.sumOf { it.session.durationMinutes }

        val totalWeeklyTargetHours = subjectsWithSessions.sumOf { it.subject.weeklyTargetHours }
        val targetMinutes = if (totalWeeklyTargetHours > 0) totalWeeklyTargetHours * 60 else (profile?.preferredStudyHours ?: 25) * 60

        val weeklyMinutes = sessionsWithSubject.sumOf { it.session.durationMinutes }

        val pendingTasks = tasks.filter { it.status != "completed" }.take(4)
        val upcomingExams = exams.take(3)
        val todaySchedule = events.filter { it.date == todayStr || events.isNotEmpty() }.take(3)
        val recentSessions = sessionsWithSubject.take(5)

        val resolvedName = authUser?.fullName?.ifBlank { null } ?: profile?.name ?: "Scholar"
        val hasCompletedTutorial = authUser?.tutorialsCompleted?.contains("dashboard") == true
        val shouldShowTutorial = !hasCompletedTutorial && !dismissed

        DashboardUiState(
            userAccount = authUser,
            userProfile = profile,
            userName = resolvedName,
            userProgram = profile?.program ?: "Computer Science & Mathematics",
            subjectsWithSessions = subjectsWithSessions,
            todayStudyMinutes = todayMinutes,
            weeklyStudyMinutes = weeklyMinutes,
            weeklyTargetMinutes = targetMinutes,
            pendingTasks = pendingTasks,
            upcomingExams = upcomingExams,
            todaySchedule = todaySchedule,
            recentSessions = recentSessions,
            streakDays = if (weeklyMinutes > 0) 7 else 1,
            showTutorial = shouldShowTutorial
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        DashboardUiState()
    )

    fun markTutorialCompleted() {
        _tutorialDismissed.value = true
        val uid = authRepository?.currentUserId?.value ?: return
        viewModelScope.launch {
            authRepository.markTutorialCompleted(uid, "dashboard")
        }
    }

    fun toggleTask(task: TaskEntity) {
        viewModelScope.launch {
            repository.toggleTaskCompleted(task)
        }
    }
}
